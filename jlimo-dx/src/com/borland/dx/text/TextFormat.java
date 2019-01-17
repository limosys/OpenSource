//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/TextFormat.java,v 7.0 2002/08/08 18:40:15 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import com.borland.jb.util.Trace;
import com.borland.jb.util.FastStringBuffer;
import com.borland.jb.util.DiagnosticJLimo;

import java.lang.*;
import java.util.*;
import java.text.*;

/**
 * This method extends the basic Format class but allows for special formatting of text.  Like the other
 * Format derivatives, it is based on a control pattern.  The format of the control pattern is as follows:
 *
 *     pattern ; keepLiterals ; fillCharacter ; replaceCharacter
 *
 * <pre>pattern</pre> can contain any of the following characters (they are a common format shared by
 * many products):
 *
 *     0                Digit 0:9, entry required, '+' and '-' not allowed
 *     9                Digit 0:9, entry optional, '+' and '-' not allowed
 *     #                Digit or space, entry optional, plus and minus signs allowed
 *     L                Letter A:Z, entry required
 *     l                Letter A:Z, entry optional
 *     ?                Letter A:Z, entry optional
 *     A                Letter A:Z or digit 0:9, entry required
 *     a                Letter A:Z or digit 0:9, entry optional
 *     C                Any character or space, entry required
 *     c                Any character or space, entry optional
 *     &                Any character or space, entry required
 *     <                Causes all characters following to be converted to lowercase
 *     >                Causes all characters following to be converted to uppercase
 *     !                Causes strings too short to fill from right to left
 *     \                Backslash escape -- allows any Unicode value to follow (e.g. "\u2003")
 *     ^                Initial place for cursor when editing begins
 *     ''               Encloses a literal expression (for example, the pattern
 *              "990' units sold'" would display as "27 units sold")
 *     **               Encloses a password encrypted string (for example,
 *              the pattern "*AAAAaaaa*" would accept a password at
 *              least 4, and at most 8 alphanumeric characters).
 *              Characters typed or displayed appear as the '*' character
 *
 *     An example of a US phone number might be "!(999)000-0000"
 *
 * <pre>keepLiterals</pre> If this value is "0", then literals in the string will be removed before
 * it is stored.  For example (408)555-5330 would become 4085555330.  Any other value (including
 * no value at all) will default to "1" which means the literals are preserved.
 *
 * <pre> fillCharacter </pre> Whenever a string is formatted which is too short to fill all the available
 * positions in the pattern, this character will be used to fill the extra space.  For example,
 * formatting "(   )555-5330 with the pattern "!(999)000-0000;1;* will produce "(***)555-5330".
 * Note that a value of zero (communicated via "\0" means no filling will occur.
 *
 * <pre> replaceCharacter </pre> Whenever a string is parsed, all occurances of "fillCharacter" are replaced
 * with "replaceCharacter".  Using the example above, parsing (***)555-5330 with the pattern
 * "!(999)000-0000;1;*;_" will produce (___)555-5330.  Again, a zero value is allowed in this field
 * (using "\0" and will have the effect of removing the fillCharacters.
 *
 * <P><B>Note:</B> Each of the special fields (keepLiterals, fillCharacter, replaceCharacter) have their
 * own methods to get or set them.
 */
public class TextFormat extends java.text.Format {

  /**
   * Not a valid character.
   */
  public static char NOT_A_CHAR = 0xffff;
//  static char PasswordChar = 0xfffe;
  static final String TRACE = "TextFormat";

  static final byte LITERAL_CHAR =  (byte) 0x01;
  static final byte OPTIONAL_CHAR = (byte) 0x02;
  static final byte PASSWORD_CHAR = (byte) 0x04;

  private String pattern;
  boolean rightToLeft;
  boolean injectLiterals;
  char[] litMask;
  byte[] infoMask;
  int charCount;                // true count of characters we can parse/format
  int litCount;                 // count of literal chars which will be seen/injected
  char fillChar;                // the char which is interpreted to mean "this is not a char"
  char replaceChar;                     // the char that will replace fillChar (0 = strip fillChars)
  boolean explicitFillChar;

  /**
   * Constructs a TextFormat object with no specified string pattern.
   */
  public TextFormat () {
    applyPattern(null);
    //Diagnostic.addTraceCategory(TRACE);
}

/**
 * Constructs a TextFormat object with the specified string pattern.
 * @param pattern String
 */
public TextFormat (String pattern) {
    this();
    applyPattern(pattern);
  }


  /**
   * Retrieves and sets the fill character used in the string
   * @param c char
   */
  public void setFillCharacter(char c) {
    if (c == NOT_A_CHAR) {
      fillChar = ' ';
      explicitFillChar = false;
    }
    else  {
      fillChar = c;
      explicitFillChar = true;
    }
  }

  public char getFillCharacter() {
    return (explicitFillChar) ? fillChar : NOT_A_CHAR;
  }

  public void setReplaceCharacter(char c) {
    replaceChar = c;
  }

  public char getReplaceCharacter() {
    return replaceChar;
  }

  public void setKeepLiterals(boolean tf) {
    injectLiterals = !tf;
  }

  public boolean getKeepLiterals() {
    return !injectLiterals;
  }

  /**
   * Sets the pattern to the specified value.
   * @param pattern String
   */
  public void applyPattern(String pattern) {
    this.pattern = (pattern != null) ? new String(pattern) : null;
    decomposePattern();
  }

  /**
   * Returns the pattern used for formatting
   *
   */
  public String toPattern() {
    return (pattern == null) ? null : pattern.toString();
  }

  private void decomposePattern() {

    charCount = 0;              // a null pattern still needs to init some stuff
    litCount = 0;
    rightToLeft = false;
    injectLiterals = false;
    fillChar = ' ';             // character which means "this is not a real char"
    explicitFillChar = false;
    replaceChar = 0;            // char to replace fillChar with (0 = strip out fillChar's)
    litMask = null;
    if (pattern == null)
      return;

    DiagnosticJLimo.trace(TRACE, "Decomposing pattern: " + pattern);
    //new NullPointerException().printStackTrace();

    int subRegion = 0;
    int iLen = pattern.length();
    FastStringBuffer fsb = new FastStringBuffer(pattern);
    FastStringBuffer fsb2;
    int i;
    char c;
    int passwordCount = 0;
    //
    // The first pass collects information about the string (e.g. size, literals, etc.)
    //
    //!      for (i = 0; i < iLen; ++i) {
    for (c = fsb.firstChar(); c != FastStringBuffer.NOTACHAR; c = fsb.nextChar()) {
      switch (c)
      {
        //
        // The ';' char is a region separator.  They are controlString;stripLiterals;fillChar;replaceChar
        // Any region (but the first) maybe omitted, and the defaults will be used.
        // For example "!(999)000-0000;0;;*" would have a default fillChar and '*' as a replaceChar
        //
        case ';':
          ++subRegion;
          if (fsb.offset() > iLen-1)        // beware end of string
            c = 0;
          else c = fsb.peekNextChar();    // 2 semi's mean nothing here
          DiagnosticJLimo.trace(TRACE, "   next char is \'" + c + "\'");
          if (c == ';')
            c = 0;
          //else if (c == '\\') {           // could be backslash constant
          //  c = fsb.parseBackSlash();
          //  Diagnostic.trace(TRACE, "backslash at region " + subRegion + " is " + (int) c);
          // }
          else c = fsb.nextChar();          // else take next character
          if (c == '\\') {
            c = fsb.parseBackSlash();
            DiagnosticJLimo.trace(TRACE, "backslash at region " + subRegion + " is " + (int) c);
          }
          switch (subRegion)
          {
            case 1:
              if (c == '0')
                injectLiterals = true;
              continue;
            case 2:
              fillChar = c;
              explicitFillChar = true;
              continue;
            case 3:
              replaceChar = c;
              //!System.err.println("TextFormat sees replaceChar as <" + c + ">");
              // falls into
            default:
              fsb.lastChar();   // will stop the loop above
          }
          continue;
          //
          // We're just counting literal and character positions in this pass
          //

        case '!':
          rightToLeft = true;
          // falls into
        case '>':
        case '<':
        case '^':
        case '{':
        case '}':
          continue;
        case '*':       // ** brackets passwords
        case '\'':      // single quotes bracket a literal
          fsb2 = fsb.parseLiteral(c, (c == '\''));    // allow 2 quotes to become single quote
          if (c == '*') {
            ++passwordCount;
            charCount += fsb2.length();
          }
          else litCount += fsb2.length();
          continue;
        case '\\':
          fsb.parseBackSlash();
          ++litCount;
          continue;

        case '0':
        case '9':
        case '#':
        case 'L':
        case 'l':
        case '?':
        case 'A':
        case 'a':
        case '&':
        case 'C':
        case 'c':
          ++charCount;
          continue;

        default:        // everything else is a literal
          ++litCount;
      }
    }


    if (injectLiterals)
      DiagnosticJLimo.trace(TRACE, "We are injecting/stripping literals");
    else
      DiagnosticJLimo.trace(TRACE, "We are injecting " + litCount + " literals");
    if (rightToLeft)
      DiagnosticJLimo.trace(TRACE, "We are scanning right to left");
    DiagnosticJLimo.trace(TRACE, "The fillChar is \'" + fillChar + "\'");
    DiagnosticJLimo.trace(TRACE, "The replaceChar is \'" + replaceChar + "\'");

    //
    // We now know how many characters belong in the string and how many literals were encountered.
    // If we don't have to strip/inject literals, then we have a very simple case (where we only reproduce
    // the string as given).
    //

    if (litCount == 0 && passwordCount == 0) {
      DiagnosticJLimo.trace(TRACE, "Pattern has no literals or password chars");
      return;
    }

    //
    // Let's construct a map for every character we would see in this display.
    // Every place there is a fillable character spot is marked "NOT_A_CHAR",
    // every place that should echo as a password "*" is marked PasswordChar,
    // and every literal part of the string comes straight from that literal.
    DiagnosticJLimo.trace(TRACE, "Pattern has " + litCount + " literals and " + charCount + " char slots");

    int fillPos = 0;
    litMask = new char[charCount+litCount];
    infoMask = new byte[charCount+litCount];

    for (i = 0; i < litMask.length; ++i) {
      litMask[i] = NOT_A_CHAR;
      infoMask[i] = 0;
    }

    byte infoBits = 0;

    for (c = fsb.firstChar(); c != FastStringBuffer.NOTACHAR; c = fsb.nextChar()) {
      DiagnosticJLimo.trace(TRACE, "considering char " + c);
      switch(c)
      {
        case '!':       // directives occupy no room
        case '>':
        case '<':
        case '^':
          continue;
        case '{':
          infoBits |= (OPTIONAL_CHAR);
          //Diagnostic.trace(TRACE, "infoBits = " + infoBits);
          continue;

        case '}':
          infoBits &= ~(OPTIONAL_CHAR);
          //Diagnostic.trace(TRACE, "infoBits = " + infoBits);
          continue;

        case '*':       // password shares parsing logic
          infoBits ^= (PASSWORD_CHAR);
          continue;

        case '\'':      // single quotes bracket a literal
          fsb2 = fsb.parseLiteral(c, (c == '\''));    // double quotes means single quote literal
          System.arraycopy(fsb2.value(), 0, litMask, fillPos, fsb2.length());
          for (int j = 0; j < fsb2.length(); ++j)
            infoMask[fillPos++] = LITERAL_CHAR;
          //fillPos += fsb2.length();
          continue;

        case '\\':
          infoMask[fillPos] = LITERAL_CHAR;
          litMask[fillPos++] = fsb.parseBackSlash();
          continue;

        case '0':
        case 'L':
        case 'A':
        case 'c':
          //Diagnostic.trace(TRACE, " infoBits for '" + c + "' at pos " + fillPos + " = " + infoBits);
          infoMask[fillPos++] = infoBits;
          continue;

        case '9':
        case '#':
        case 'l':
        case '?':
        case 'a':
        case '&':
        case 'C':
          infoMask[fillPos++] = (byte) ((infoBits & PASSWORD_CHAR) | OPTIONAL_CHAR);
          continue;

        case ';':
          fsb.lastChar();       // to stop the loop
          break;

        default:        // everything else is a literal
          DiagnosticJLimo.trace(TRACE, "Litmask[" + fillPos + "] gets the char \'" + c + "\'");
          infoMask[fillPos] = LITERAL_CHAR;
          litMask[fillPos++] = c;
          continue;
      }
    }

    DiagnosticJLimo.trace(TRACE, "LitMask is " + litMask.length + " and we filled " + fillPos);

    //! Debug only

    //!$$$

    if (false) {
      StringBuffer dStr = new StringBuffer();
      for (i = 0; i < litMask.length; ++ i)
        dStr.append((litMask[i] == NOT_A_CHAR)
                    ? '_'
                    : ((infoMask[i] & PASSWORD_CHAR) != 0)
                      ? '*'
                      : litMask[i]);
      System.err.println(dStr.toString() + "  <-- literals");

      dStr = new StringBuffer();
      for (i = 0; i < infoMask.length; ++ i)
        dStr.append(((infoMask[i] & LITERAL_CHAR) != 0)
                    ? litMask[i]
                    : ((infoMask[i] & OPTIONAL_CHAR) != 0)
                      ? '_'
                      : '!');
      System.err.println(dStr.toString() + "  <-- !=required");
    }
  }


  /**
  This method will format the given String using the pattern associated with this object.
  If the input string has insufficient characters to fill the pattern, they will be filled
  with the 'fillCharacter' character (which can be set via setFillChar()).

  @param toBeFormatted The input String which requires formatting
  @param result The output StringBuffer containing the formatted result
  @param status Currently not used

  @return The 'result' parameter is also the returned value.

*/
/**
 * Formats the given String using the pattern associated with this object.
 * If the input string has insufficient characters to fill the pattern, it is
 *  filled with the character indicated
 * by setFillChar(). The result parameter is assigned the return value of this method.
 * @param toBeFormatted String
 * @param result StringBuffer
 * @param pos FieldPosition
 *
 */
  public StringBuffer format(String toBeFormatted, StringBuffer result, FieldPosition pos) {
    DiagnosticJLimo.trace(TRACE, "format(" + toBeFormatted + ", " + result + ", " + pos + ")");
    int maxChars = charCount + litCount;  // compute size of formatting pattern
    // Quick out if there is no pattern
    if (maxChars == 0) {
      result = new StringBuffer(toBeFormatted);
      return result;
    }

    char[] buffer;
    int iLen = toBeFormatted.length();

    DiagnosticJLimo.trace(TRACE, "Formatting \"" + toBeFormatted + "\" with \"" + pattern + "\""); //$$$

//!    //!DEBUG only
//!    /*
//!    if (Diagnosing.on && litMask != null) {
//!      for (int j = 0; j < litMask.length; ++j) {
//!        if (litMask[j] == NOT_A_CHAR)
//!          System.err.print("_");
//!        else System.err.print(litMask[j]);
//!      }
//!      System.err.println("");
//!    }
//!    */

    DiagnosticJLimo.trace(TRACE, "iLen = " + iLen + ", maxChars = " + maxChars + ", charCount = "
                       + charCount + ", litCount = " + litCount);
//!    /* We no longer check since it is permissible to pass in too short a formatting string
//!     *      if (iLen > maxChars) {
//!     *        throw new StringIndexOutOfBoundsException(0);
//!     *      }
//!     */
    buffer = new char[iLen];
    toBeFormatted.getChars(0, iLen, buffer, 0);
    result.setLength(0);


    int fillPos;
    int incr = 1;
    int i = 0;
    int srcPos = 0;
    int nRemaining = iLen;

    if (rightToLeft) {
      incr = -1;
      srcPos = iLen - 1;
      i = maxChars - 1;
    }

    char[] fillBuffer = new char[maxChars];
    DiagnosticJLimo.trace(TRACE, "Input buffer is " + buffer.length + ", FillBuffer is " + fillBuffer.length);
    if (litMask != null)
      DiagnosticJLimo.trace(TRACE, "litMask is " + litMask.length);

    for (fillPos = i; i >= 0 && i < maxChars; i += incr) {
      DiagnosticJLimo.trace(TRACE, "FillPos=" + fillPos + ", i=" + i + ", nRemaining=" +nRemaining);  //$$$

      //!DEBUG only
//!/*
//!      if (Diagnosing.on) {
//!        if (litMask != null) {
//!          if (litMask[i] != NOT_A_CHAR)
//!            Diagnostic.trace(TRACE, "LitMask[" + i + "] == " + litMask[i]);
//!          else Diagnostic.trace(TRACE, "LitMask expects a char here");
//!         }
//!      }
//!*/
      //!       Diagnostic.trace(TRACE, "FillBuffer[" + fillPos + "]");
      //
      // Take care, when we run out of input chars, we continue to fill with literals and/or fillChar
      //
      if (nRemaining <= 0) {
        //if (!spotForCharacter(i)) {
        if (isLiteral(i)) {
          DiagnosticJLimo.trace(TRACE, "nRemaining <= 0, filling with litMask " + i  + " at pos " + fillPos + ": " + litMask[i]); //$$$
          DiagnosticJLimo.trace(TRACE, " pads with literal \'" + litMask[i] + "\'");
          fillBuffer[fillPos] = litMask[i];
          fillPos += incr;
          continue;
        }

        if (replaceChar != 0) {
          DiagnosticJLimo.trace(TRACE, "nRemaining <= 0, filling with replaceChar at pos " + fillPos); //$$$
          fillBuffer[fillPos] = replaceChar;
          fillPos += incr;
        }

//!//        else if (explicitFillChar /*&& fillChar != 0*/) {
//!//          Diagnostic.trace(TRACE, "nRemaining <= 0, filling with fillChar at pos " + fillPos); //$$$
//!//          if (fillChar != 0) {
//!//            fillBuffer[fillPos] = fillChar;
//!//            fillPos += incr;
//!//          }
//!//        }
        else DiagnosticJLimo.trace(TRACE, " being skipped");  //$$$
        continue;
      }
      //
      // Still have input chars -- it we have a literal in this position and are injecting
      // them, then inject it (but DON'T advance the input buffer pointer)
      //
      //if (!spotForCharacter(i) && ((infoMask[i] & PASSWORD_CHAR) == 0)) {
      if (isLiteral(i)) {
        DiagnosticJLimo.trace(TRACE, "literal <" + litMask[i] + "> expected at this position");
        if (injectLiterals) {
          DiagnosticJLimo.trace(TRACE, " [" + fillPos + "] gets the literal \'" + litMask[i] + "\'");
          fillBuffer[fillPos] = litMask[i];
          fillPos += incr;
        }
        //
        // If we are NOT injecting literals, perform a quick test to see whether the input text
        // has the right literal here.  It should.  If it does not, it means it was formed with
        // a different pattern.  Try injecting the literal anyway and NOT advancing the source
        // pointer.  Perhaps we will synch up eventually.  In any case, they are asking us to
        // format something which does not fit the mask, so the best we can do is try to help.
        else {
          DiagnosticJLimo.trace(TRACE, "Considering pos " + srcPos + ": " + buffer[srcPos]);  //$$$
          if (buffer[srcPos] != litMask[i] && ((infoMask[i] & PASSWORD_CHAR) == 0)) {
            DiagnosticJLimo.trace(TRACE, "Yikes: expected to see literal /'" + litMask[i] + "\' here");
            DiagnosticJLimo.trace(TRACE, "  but saw the char '\'" + buffer[srcPos] + " '\'");
            DiagnosticJLimo.trace(TRACE, "  injecting literal anyway to try to recover");
            DiagnosticJLimo.trace(TRACE, "  pos " + srcPos + " != literal: filling with replaceChar");
            if (replaceChar != 0) {
              fillBuffer[fillPos] = replaceChar;  //! litMask[i]; TODO <ron> 2/19/97 bug 3293
              fillPos += incr;
            }
            continue;
          }
          else {
            if (isPassword(i)) {
              DiagnosticJLimo.trace(TRACE, " [" + fillPos + "] gets password fill");
              fillBuffer[fillPos] = '*';
            }
            else {
              DiagnosticJLimo.trace(TRACE, " [" + fillPos + "] copies \'" + buffer[srcPos] + "\' from input text");
              fillBuffer[fillPos] = buffer[srcPos];
            }
            fillPos += incr;
            srcPos += incr;
            --nRemaining;
          }
        }
      }
      //
      // Here if we have to fill a char from the input buffer
      //
      else {
        boolean spotForChar = spotForCharacter(i);
        if (spotForChar && buffer[srcPos] == fillChar) {
          DiagnosticJLimo.trace(TRACE, " there is a fill char at this position -- injecting replaceChar");
          if (replaceChar != 0) {
            fillBuffer[fillPos] = replaceChar;
            fillPos += incr;
          }
          else {
            DiagnosticJLimo.trace(TRACE, " skipping this pos because replaceChar is nil");
          }
          srcPos += incr;
          --nRemaining;
          continue;
        }

        //!RC 5/6/97 Bug 4257
        // If we are injecting a char at this position, look ahead and see if this char
        // is the next literal is this character.  If that is the case, we inject fillChars
        // until we synch up with that literal
        char nextLit = 0;
        if (spotForChar && litMask != null)
          for (int jj = i+incr; jj >= 0 && jj < maxChars; jj += incr)
            if (litMask[jj] != NOT_A_CHAR && ((infoMask[jj] & PASSWORD_CHAR) == 0)) {
              nextLit = litMask[jj];
              break;
            }

        DiagnosticJLimo.trace(TRACE, " (lookahead lit = \'" + nextLit + "\' <" + (int) nextLit + ">)"); //$$$

        if (nextLit != 0 && buffer[srcPos] == nextLit) {

          if (replaceChar != 0) {
            DiagnosticJLimo.trace(TRACE, "  space for char is lookahead lit -- injecting replaceChar");
            fillBuffer[fillPos] = replaceChar;
            fillPos += incr;
          }
//!//!RC 9/22/97 no longer fill with fillChar since it is now formally the "blank" char for input
//!//          else if (explicitFillChar /*&& fillChar != 0*/) {
//!//            Diagnostic.trace(TRACE, "  space for char is lookahead lit -- injecting fillChar");
//!//            if (fillChar != 0) {
//!//              fillBuffer[fillPos] = fillChar;
//!//              fillPos += incr;
//!//            }
//!//          }
//!          // If there is no fillChar or replaceChar -- don't fill with anything
//!//!RC          --nRemaining;      // Bug fix 7/16/97 Don't decrement remaining chars if filling with empties
          continue;
        }

        if (litMask != null && ((infoMask[i] & PASSWORD_CHAR) != 0)) {
          DiagnosticJLimo.trace(TRACE, " [" + fillPos + "] gets password fill");
          fillBuffer[fillPos] = '*';
        }
        else {
          DiagnosticJLimo.trace(TRACE, " [" + fillPos + "] copies '" + buffer[srcPos] + "' from text");
          fillBuffer[fillPos] = buffer[srcPos];
          srcPos += incr;
        }
        fillPos += incr;
        --nRemaining;
      }

    }

    i = 0;
    if (rightToLeft)
      i = fillPos;
    if (i < 0)
      i = 0;
    if (i >= maxChars)
      i = maxChars - 1;

    if (rightToLeft) {
      i = fillPos+1;
      iLen = maxChars - fillPos - 1;
    }
    else {
      i = 0;
      iLen = fillPos;
    }

    //!DEBUG only
//!/*
//!    if (Diagnosing.on) {
//!      String sb = new String(fillBuffer, 0, maxChars);
//!      Diagnostic.trace(TRACE, "The whole fillbuffer looks like \"" + sb + "\"");
//!      Diagnostic.trace(TRACE, "Asking to append from pos " + i + " for " + iLen + " chars");
//!    }
//!*/
    result.append(fillBuffer, i, iLen);
    DiagnosticJLimo.trace(TRACE, "returning " + result);
    return result;
  }

  /**
  This method will parse (or decompose) a string using the existing pattern.

  @param text This is the input String which is to be parsed.
  @param status Currently not used

  @return This method will allocate a new StringBuffer and fill it with the parsed version
  of the 'text' parameter.

*/

/**
 * This method parses (or decomposes) a String using the existing pattern. It
 * allocates a new StringBuffer and fills it with the parsed version of the text parameter.
 * @param text String
 * @param pos ParsePosition
 *
 */
  public StringBuffer parse(String text, ParsePosition pos) {
    int maxChars = charCount + litCount;

    // Patternless strings parse merely by copying the input to the output
    if (maxChars == 0)
      return new StringBuffer(text);

    int i;
    StringBuffer result;
    int iLen = text.length();
    char buffer[] = new char[iLen];
    text.getChars(0, iLen, buffer, 0);
    int startPos;
    char[] fillBuffer = new char[maxChars];
    DiagnosticJLimo.trace(TRACE, "The text " + text + " is " + iLen + " and we will fill into " + fillBuffer.length);

    int nFilled = 0;
    int fillPos = 0;
    char c;
    if (!injectLiterals || litCount == 0) {
      DiagnosticJLimo.trace(TRACE, "No literal stripping -- removing up to " + iLen + " chars from " + text);
      for(i = 0; i < maxChars; ++i) {
        DiagnosticJLimo.trace(TRACE, "Buffer[" + i + "]");
        //!RC Don't fill passwords with "*" ------------vvvvvvvvvvvvvvvvvvvvvvvvvv----- 6/24/97
        if (litMask != null && litMask[i] != NOT_A_CHAR && ((infoMask[i] & PASSWORD_CHAR) == 0)) {                      // literals copy thru directly
          DiagnosticJLimo.trace(TRACE, " filling literal " + litMask[i]);
          //fillBuffer[fillPos++] = (litMask[i] == PasswordChar) ? '*' : litMask[i];
          //!RC -- removed this ----^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ 6/24/97
          fillBuffer[fillPos++] = litMask[i];
          continue;
        }
        c = (i < iLen)
            ? buffer[i]
            : fillChar; // else comes form text -- beware running off end
        if (c == fillChar) {
          c = replaceChar;
          if (c == 0)                                   // also beware of fillChars turning into null
            continue;
        }
        DiagnosticJLimo.trace(TRACE, "  appending " + c);
        fillBuffer[fillPos++] = c;
      }
      DiagnosticJLimo.trace(TRACE, "And the final buffer has " + fillPos + " chars");
      result = new StringBuffer(fillPos);
      result.append(fillBuffer, 0, fillPos);
      return result;
    }

    i = 0;
    int incr = 1;
    int litPos = 0;
    int nRemaining = iLen;

    if (rightToLeft) {
      i = iLen - 1;
      incr = -1;
      fillPos = maxChars - 1;
      litPos = maxChars - 1;
    }

    for (; i >= 0 && i < iLen; i += incr, litPos += incr) {

      DiagnosticJLimo.trace(TRACE, "Buffer[" + i + "]");
      if (buffer[i] == fillChar) {              // fill chars are valid place holders but get stripped
        DiagnosticJLimo.trace(TRACE, "   fillchar -- skipping");
        continue;
      }
      if (litMask[litPos] == NOT_A_CHAR || ((infoMask[litPos] & PASSWORD_CHAR) != 0)) {
        c = buffer[i];
        DiagnosticJLimo.trace(TRACE, "   put '" + c + "' into fillBuffer[" + fillPos + "] and iLen = " + iLen);
        --nRemaining;
        if (c == fillChar) {
          DiagnosticJLimo.trace(TRACE, "     it is a fillChar and gets translated to" + replaceChar);
          c = replaceChar;
          if (c == 0) {
            DiagnosticJLimo.trace(TRACE, "      and just got stripped");
            continue;
          }
        }
        fillBuffer[fillPos] = c;
        fillPos += incr;
        ++nFilled;
        continue;
      }
      if (litMask[litPos] == buffer[i]) {
        DiagnosticJLimo.trace(TRACE, "  matched literal, skipping");
        continue;
      }
      //
      // Hmmm -- we are here if the character in text[i] doesn't match the literal we expected.
      // This means it was not a legal string w.r.t. this mask -- what to do... let's stop the loop
      break;
    }

    result = new StringBuffer(nFilled);
    if (rightToLeft)
      result.append(fillBuffer, maxChars-nFilled, nFilled);
    else
      result.append(fillBuffer, 0, nFilled);
    return result;
  }


  /**
   * Parses (or decomposes) a String into an Object.
   * @param source String
   * @param pos ParsePosition
   *
   */
  public final Object parseObject(String source, ParsePosition pos) {
    return parse(source, pos);
  }

  /**
   *
   * @param obj Object
   * @param toAppendTo StringBuffer
   * @param pos FieldPosition
   *
   */
  public final StringBuffer format(Object obj,
                                   StringBuffer toAppendTo,
                                   FieldPosition pos) {
    return format(obj.toString(), toAppendTo, pos);
  }

  private final boolean isLiteral(int position) {
    return (infoMask == null)
        ? false
        : ((position < 0) || (position >= infoMask.length))
          ? false
          : ((infoMask[position] & LITERAL_CHAR) != 0);
  }

  private final boolean isPassword(int position) {
    return (infoMask == null)
        ? false
        : ((position < 0) || (position >= infoMask.length))
          ? false
          : ((infoMask[position] & PASSWORD_CHAR) != 0);
  }

  private final boolean isOptional(int position) {
    return (infoMask == null)
        ? true
        : ((position < 0) || (position >= infoMask.length))
          ? true
          : ((infoMask[position] & OPTIONAL_CHAR) != 0);
  }

  private final boolean spotForCharacter(int position) {
    return (infoMask == null) ? true : ((infoMask[position] & LITERAL_CHAR) != 0);
//!/*
//!    if (litMask == null)
//!      return true;
//!    if (litMask[position] == NOT_A_CHAR || ((infoMask[i] & PASSWORD_CHAR) != 0))
//!      return true;
//!    return false;
//!*/
  }
}
