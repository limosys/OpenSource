//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/util/FastStringBuffer.java,v 7.0 2002/08/08 18:40:52 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.util;

import com.borland.jb.util.Hex;
import java.lang.*;
import java.util.*;

/**
 * Use the <code>FastStringBuffer</code> component to replace use of the
 * <code>StringBuffer</code> class in cases where a buffer is not shared.
 * It removes some of the complications of synchronization and sharing.
 * <p>
 * <b>Warning:</b> Because none of the <code>FastStringBuffer</code> methods are
 * synchronized, these methods should not be used for objects which may be
 * accessed simultaneously. <code>FastStringBuffer</code> is intended for rapid
 * processing on local objects.
 */
public final class FastStringBuffer {

  private static char[] logicalLiteral = {'\r','\n','\t','\'','\\'};
  private static char[] displayLiteral = { 'r', 'n', 't','\'','\\'};            //NORES

  /**
   * Indicates that a fetch has run out of bounds.
   */
  public static final int NOTACHAR = 0;

  /**
   * The given character is invalid.
   */
  public static final int NOT_A_CHAR = 0;

  private char value[];  // the array we keep things in
  private int lastChar;  // offset of last char stored.
  private int offset;    // used to communicate current position when scanning
  private int maxCapacity;

  /**
   * Constructs a FastStringBuffer object with a default length of
   * 16 characters (char[16]). The offset and count are initialized to 0.
   */
  public FastStringBuffer() {
    this(16);
  }

  /**
   *  Constructs an empty FastStringBuffer of the given length.
   * @param length  The initial capacity.
   */
  public FastStringBuffer(int length) {
    value = new char[length];
    offset = 0;
    lastChar = -1;
    maxCapacity = value.length-1;
  }

  /**
   * Constructs a new FastStringBuffer from the given string.
   * The initial contents of the buffer are a copy of str.
   * The initial capacity of the buffer is equal to the length
   * of the string plus 16.
   * @param str   The string to create FastStringBuffer from.
   */
  public FastStringBuffer(String str) {
    this(str.length() + 16);
    append(str);
    offset = 0;
    lastChar = str.length()-1;
    maxCapacity = value.length-1;
  }

  /**
   *  Constructs FastStringBuffer with the given character.
   *  The initial capacity of the buffer is equal to the given character plus 16.
   * @param cArray    The character to construct FastStringBuffer with.
   */
  public FastStringBuffer(char[] cArray) {
    this(cArray.length + 16);
    append(cArray);
    offset = 0;
    lastChar = cArray.length-1;
    maxCapacity = value.length-1;
  }

  /**
   *  Constructs FastStringBuffer from the given character with the
   *  specified number of characters. The initial capacity of the buffer
   *  is equal to the specified number of characters plus 16.
   * @param cArray  The character to construct FastStringBuffer with.
   * @param offset  The location of FastStringBuffer.
   * @param len     The length of the buffer.
   */
  public FastStringBuffer(char[] cArray, int offset, int len) {
    this(len + 16);
    append(cArray, offset, len);
    offset = 0;
    lastChar = len-1;
    maxCapacity = value.length-1;
  }

  /**
   *  Constructs FastStringBuffer using the given number of repetitions
   *  and the given character. The initial capacity of the buffer is equal
   *  to the number of repetitions.
   * @param c       The character to construct <code>FastStringBuffer</code> with.
   * @param nChars  The number of repetitions.
   */
  public FastStringBuffer(char c, int nChars) {
    this(nChars);
    for (int i = 0; i < nChars; ++i)
      value[i] = c;
    lastChar = nChars-1;
    maxCapacity = value.length-1;
  }

/**
 *  Nulls the <code>FastStringBuffer</code>.
 */
  public void empty() {
    for (int i = 0; i < lastChar; i++)
      value[i] = '\0';
    lastChar = -1;
    offset = 0;
  }

  /**
   * Returns the internal character string used by <code>FastStringBuffer</code>.
   */
  public char[] value() {
    return value;
  }

  /**
   * Returns the internal character string used by <code>FastStringBuffer</code>.
   */
  public char[] getValue() {
    return value;
  }


/**
 * Returns the number of actual characters in <code>FastStringBuffer</code>.
 */
  public final int length() {
    return lastChar+1;
  }

  /**
   * Returns the number of actual characters in <code>FastStringBuffer</code>.
   */
  public final int getLength() {
    return lastChar+1;
  }

  /**
   *  Returns the current capacity of <code>FastStringBuffer</code>.
   */
  public int capacity() {
    return value.length;
  }

  /**
   *  Internal current position in <code>FastStringBuffer</code> used by the
   *  following methods:
   *  <p>
   *  <ul>
   *    <li>{@link #firstChar()}</li>
   *    <li>{@link #lastChar()}</li>
   *    <li>{@link #nextChar()}</li>
   *    <li>{@link #priorChar()}</li>
   *  </ul>
   * @return the offset of the current position in the buffer
   */
  public int offset() {
    return offset;
  }

  /**
   *  Internal current position in <code>FastStringBuffer</code> used by the
   *  following methods:
   *  <p>
   *  <ul>
   *    <li>{@link #firstChar()}</li>
   *    <li>{@link #lastChar()}</li>
   *    <li>{@link #nextChar()}</li>
   *    <li>{@link #priorChar()}</li>
   *  </ul>
   * @return the offset of the current position in the buffer
   */
  public int getOffset() {
    return offset;
  }

  /**
   * Specifies an offset in <code>FastStringBuffer</code>. Used by
   * <code>firstChar()</code>, <code>lastChar()</code>,
   * <code>nextChar()</code>, and <code>priorChar()</code>
   *
   * @param offset
   */
  public void setOffset(int offset) {
    this.offset = offset;
  }

  /**
   * Increases the size of the <code>FastStringBuffer</code> so that it will
   * hold at least the given number of characters.
   * @param needed    the minimum number of characters in the buffer
   */
  public final void makeroom(int needed) {
    int minCapacity = lastChar+needed;

    if (minCapacity > maxCapacity) {
      int newCapacity = (maxCapacity +1 + 1) * 2;
      if ((minCapacity+1) > newCapacity)
        newCapacity = minCapacity+1;
      char[] newValue = new char[newCapacity];
      System.arraycopy(value, 0, newValue, 0, lastChar+1);
      value = newValue;
      maxCapacity = value.length-1;
    }
  }

  private final void outOfBounds(int length) {
    throw new StringIndexOutOfBoundsException(length);
  }

  /**
   * Specifies number of actual characters in
   * <code>FastStringBuffer</code>.
   *
   * @param newLength
   */
  public final void setLength(int newLength) {
    if (newLength < 0)
      outOfBounds(newLength);

    if (lastChar < newLength) {
      makeroom(newLength-lastChar);
      while (lastChar < newLength) {
        value[++lastChar] = '\0';
      }
    }
    lastChar = newLength-1;
  }

  /**
   * Returns the character at the given position.
   * @param index The index position to examine; it must be
   * greater than or equal to 0, and less than the length
   * of <code>FastStringBuffer</code>.
   *
   * @return
   */
  public char charAt(int index) {
    if (index < 0 || index > lastChar)
      outOfBounds(index);
    offset = index;
    return value[index];
  }

  // Returns the first char in the FastStringBuffer -- used for loops.
  /**
   * Sets <code>offset()</code> to 0 and returns to the first character
   * in the buffer. This method is used for loops with <code>lastChar()</code>.
   * @return  the first character in the buffer
   */
  public char firstChar() {
    if (lastChar < 0)
      return NOTACHAR;
    offset = 0;    // gets ready for nextChar()
    return value[0];
  }

  /**
   * Moves the <code>offset()</code> to the last character in
   * <code>FastStringBuffer</code> and returns that character.
   * Meant to be used in a <code>lastChar()<code>
   * /<code>priorChar()<code> loop.
   *
   * @return the final char or <code>FastStringBuffer.NOTACHAR</code> if empty
   */
  public char lastChar() {
    if (lastChar < 0)
      return NOTACHAR;
    offset = lastChar;
    return value[offset];
  }

  // Returns the character at value[offset] -- used for loops.  NOTE!!! this STARTS at offset.
  /**
   * Returns the character at <code>offset</code>. Note that
   * this method starts at the current offset.
   *
   * @return <code>NOTACHAR</code> if empty
   */
  public char currentChar() {
    if ((offset < 0) || (offset > lastChar))
      return NOTACHAR;
    return value[offset];
  }

  // Returns next sequential char (value[offset+1).  Zero return means done.
  /**
   *  Moves the {@link #offset} to the next sequential character in
   *  <code>FastStringBuffer</code> and returns that character.
   * @return   Next sequential char (value[offset+1). Zero return means done.
   */
  public char nextChar() {
    if (offset < -1 || offset >= lastChar)
      return NOTACHAR;
    return value[++offset];
  }

  // Returns previous sequential char (value[offset-1).  Zero return means done.
  /**
   * Moves the {@link #offset} to the previous sequential character in
   * <code>FastStringBuffer</code> and returns that character. Meant to be used as
   * <code>lastChar()/priorChar()</code> loop.
   *
   * @return the prior character or <code>NOTACHAR</code> if empty
   */
  public char priorChar() {
    if (offset < 1 || offset >= lastChar)
      return NOTACHAR;
    return value[--offset];
  }

  /**
   * To be used in a firstChar()/nextChar() sort of loop, this method
   * peeks at the next character WITHOUT advancing any pointers.
   */

   /**
    *  Peeks at the next character <i>without</i> advancing any pointers.
    *  Used in a <code>firstChar()/nextChar()</code> type loop.
    *
    * @return the next character or <code>NOTACHAR</code> if empty
    */
  public char peekNextChar() {
    if (offset < -1 || offset >= lastChar)
      return NOTACHAR;
    return value[offset+1];
  }

  /**
   * Copies characters from <code>FastStringBuffer</code> into the destination character array.<p>
   *
   * The first character to be copied is at <code>srcBegin</code>.
   * The last character to be copied is at <code>srcEnd-1</code>,
   * making the total number of characters to be copied
   * <code>srcEnd - srcBegin</code>. The characters
   * are copied into the subarray of <code>dst</code>
   * starting at <code>dstBegin</code> and ending
   * at index: <p>
   * <code>dstbegin + (srcEnd-srcBegin) - 1</code>
   *
   * @param srcBegin    the index of the first character in the string to copy
   * @param srcEnd      the index after the last character in the string to copy
   * @param dst         the destination array
   * @param dstBegin    the start offset in the destination array
   * @exception <CODE>StringIndexOutOfBoundsException</CODE> if:
   * <ul><LI><CODE>srcBegin</CODE> is negative.
   * <LI><CODE>srcBegin</CODE> is greater than <CODE>srcEnd</CODE>.
   * <LI><CODE>srcEnd</CODE> is greater than the length of this string.
   * <LI><CODE>dstBegin</CODE> is negative.
   * <LI><CODE>dstBegin+(srcEnd-srcBegin)</CODE> is larger
   * than <CODE>dst.length</CODE><ul>.
   */
  public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
    if (srcBegin < 0 || srcBegin > lastChar)
      outOfBounds(srcBegin);
    if (srcEnd < 0 || srcEnd > (lastChar+1))
      outOfBounds(srcEnd);
    if (srcBegin < srcEnd)
      System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
  }

  /**
   * Sets the character at the given index position to the given character.
   * @param index     the location at which to set the character;
   *                  must be greater than or equal to 0, and less than the
   *                  length of <code>FastStringBuffer</code>
   * @param ch        the character to set the specified location to
   */
  public void setCharAt(int index, char ch) {
    if (index < 0 || index > lastChar)
      outOfBounds(index);
    value[index] = ch;
  }

  /**
   * Removes the character from the buffer at
   * <code>index</code>.  It adjusts <code>index</code>
   * so that a <code>nextChar()</code> loop finds the character
   * immediately following the one removed.
   *
   * @param index the location from which to remove the character
   */
  public void removeCharAt(int index) {
    if (index < 0 || index > lastChar)
      outOfBounds(index);
    int len = lastChar - index;
    //!  System.err.println("FastStringBuffer.removeCharAt(" + index + ") will move " + len + " chars");
    if (index < lastChar && len > 0)
      System.arraycopy(value, index+1, value, index, len);
    offset = index - 1;    // so nextChar() will see next sequential char
    --lastChar;
    //!  System.err.println("  results in " + this.toString());
  }

  /**
   * Removes the current character from the buffer,
   * where "current" is defined by <code>offset</code>. It is
   * intended to be used in a <code>firstChar/nextChar<code>
   * loop.  It adjusts <code>offset</code>
   * so that the next <code>nextChar()</code> method call functions properly.
   */
  public void removeChar() {
    removeCharAt(offset);
  }

  /**
   * Removes the specified number of characters from the buffer
   * at the specified <code>index</code>. It adjusts <code>index</code>
   * so that a <code>nextChar()</code> loop finds the character
   * immediately following the one removed.
   *
   * @param index the location from which to remove the characters
   * @param removeCount the number of characters to remove
   */
  public void removeCharsAt(int index, int removeCount) {
    if (index < 0 || index+removeCount > lastChar)
      outOfBounds(index);
    if (index < (lastChar+1)-removeCount)
      System.arraycopy(value, index+removeCount, value, index, lastChar+1-index-removeCount);
    offset = index - removeCount;    // so nextChar() will see next sequential char
    lastChar -= removeCount;
  }

  /**
   * Removes the specified number of characters from the current position
   * where "current" is defined as <code>value[offset]</code>.
   * It adjusts <code>offset</code> so that the next <code>nextChar()</code>
   * method call encounters the next character in the buffer.
   *
   * @param removeCount   the number of characters to remove
   */
  public void removeChars(int removeCount) {
    removeCharsAt(offset, removeCount);
  }

  /**
    * Replaces the character at the given position with the given character.
    * @param index     the location at which to replace the character
    * @param c         the character to replace the current one with
    */
  public void replaceCharAt(int index, char c) {
    if (index < 0 || index > lastChar)
      outOfBounds(index);
    value[index] = c;
  }

  /**
   * Appends the given object to <code>FastStringBuffer</code>.
   * @param obj The object to append.
   * @return
   */
  public FastStringBuffer append(Object obj) {
    return append(String.valueOf(obj));
  }

  /**
   * Appends the string representation of the specified character
   * to <code>FastStringBuffer</code> and increases the length of the buffer by 1.
   * @param c   The character to append.
   * @return
   */
  public final FastStringBuffer append(char c) {
    if ((lastChar+1) > maxCapacity)
      makeroom(1);
    value[++lastChar] = c;
    return this;
  }

  /**
   *  Appends the string representation of the specified characters
   *  to <code>FastStringBuffer</code> and increases the length of the buffer by 4.
   * @param c1  The character to append.
   * @param c2  The character to append.
   * @param c3  The character to append.
   * @param c4  The character to append.
   * @return
   */
  public final FastStringBuffer append(char c1, char c2, char c3, char c4) {
    if ((lastChar+4) > maxCapacity)
      makeroom(4);
    value[++lastChar] = c1;
    value[++lastChar] = c2;
    value[++lastChar] = c3;
    value[++lastChar] = c4;
    return this;
  }

  /**
   * Appends the string representation of the specified characters
   * to <code>FastStringBuffer</code> and ihe length of the buffer by 5.
   * @param c1  The character to append.
   * @param c2  The character to append.
   * @param c3  The character to append.
   * @param c4  The character to append.
   * @param c5  The character to append.
   * @return
   */
  public final FastStringBuffer append(char c1, char c2, char c3, char c4, char c5) {
    if ((lastChar+5) > maxCapacity)
      makeroom(5);
    value[++lastChar] = c1;
    value[++lastChar] = c2;
    value[++lastChar] = c3;
    value[++lastChar] = c4;
    value[++lastChar] = c5;
    return this;
  }

  /**
   * Appends the given number of repetitions of the given character
   * to <code>FastStringBuffer</code> and increases the length of the
   * buffer by <code>appendCount</code>.
   * @param c   The character to append.
   * @param appendCount   The number of times to append the character.
   * @return
   */
  public FastStringBuffer append(char c, int appendCount) {
    makeroom(appendCount);
    ++appendCount;
    while (--appendCount > 0)
      value[++lastChar] = c;
    return this;
  }

  /**
   * Appends the string representation of the specified
   * string to <code>FastStringBuffer</code>.
   * @param str the string to append
   * @return
   */
  public final FastStringBuffer append(String str) {
    if (str == null)
      str = String.valueOf(str);

    int len = str.length();
    if ((lastChar+len) > maxCapacity)
      makeroom(len);

    str.getChars(0, len, value, lastChar+1);
    lastChar += len;
    return this;
  }

  /**
   * Appends one <code>FastStringBuffer</code> to another one.
   * The original <code>FastStringBuffer</code> is increased by the length of the new buffer.
   * @param fsb The buffer to append.
   * @return
   */
  public FastStringBuffer append(FastStringBuffer fsb) {
    if (fsb == null)
      return this;

    int len = fsb.lastChar+1;
    makeroom(len);

    fsb.getChars(0, len, value, lastChar+1);
    lastChar += len;
    return this;
  }

  /**
   * Appends the string representation of the specified string to
   * <code>FastStringBuffer</code> and increases the length of the buffer by the
   * number of characters in the string.
   * @param str The string to append.
   * @return
   */
  public FastStringBuffer append(char[] str) {
    int len = str.length;
    makeroom(len);

    System.arraycopy(str, 0, value, lastChar+1, len);
    lastChar += len;
    return this;
  }

  /**
   * Appends the string representation of the specified string
   * at the given offset for the given count and increases
   * the length of the buffer by the length of the string.
   * @param str     The string to append.
   * @param offset  The offset at which to append the string.
   * @param len     The length of the string.
   * @return
   */
  public FastStringBuffer append(char[] str, int offset, int len) {
    makeroom(len);

    System.arraycopy(str, offset, value, lastChar+1, len);
    lastChar += len;
    return this;
  }

  /**
   * Inserts the string representation of the given object
   * into <CODE>FastStringBuffer</CODE> at the given offset
   * position and increases the length of the buffer
   * by the length of the object.
   *
   * @param offset the position at which to insert the object
   * @param obj the object to insert
   */
  public FastStringBuffer insert(int offset, Object obj) {
    return insert(offset, String.valueOf(obj));
  }

  /**
   * Inserts the string representation of the given string into
   * <code>FastStringBuffer</code> at the given offset and
   * increases the length of the buffer by the length of the string.
   *
   * @param offset the position at which to insert the string
   * @param str the string to insert
   * @return
   */
  public FastStringBuffer insert(int offset, String str) {
    if (offset < 0 || offset > lastChar)
      outOfBounds(offset);
    int len = str.length();
    makeroom(len);

    System.arraycopy(value, offset, value, offset + len, lastChar+1 - offset);
    str.getChars(0, len, value, offset);
    lastChar += len;
    return this;
  }

  /**
   * Inserts the string representation of the specified character string into
   * <code>FastStringBuffer</code> at the given offset position
   * and increases the length of the buffer by the length
   * of the string.
   *
   * @param offset  the position at which to insert the character string
   * @param str     the character string to insert
   * @return
   */
  public FastStringBuffer insert(int offset, char[] str) {
    if (offset < 0 || offset > lastChar)
      outOfBounds(offset);
    int len = str.length;
    makeroom(len);

    System.arraycopy(value, offset, value, offset + len, lastChar+1 - offset);
    System.arraycopy(str, 0, value, offset, len);
    lastChar += len;
    return this;
  }

  /**
   * Inserts the string representation of the given boolean into
   * <code>FastStringBuffer</code> at the given offset position and increases the
   * length of the buffer by the length of <code>b</code>.
   * @param offset    the position at which to insert the boolean
   * @param b         the boolean value to insert
   */
  public FastStringBuffer insert(int offset, boolean b) {
    return insert(offset, String.valueOf(b));
  }

  /**
   * Inserts the string representation of the given
   * character into <code>FastStringBuffer</code> at the given position
   * and increases the length of the buffer by 1.
   *
   * @param offset    The position at which to insert the character.
   * @param c         The character to insert.
   */
  public FastStringBuffer insert(int offset, char c) {
    makeroom(1);

    System.arraycopy(value, offset, value, offset + 1, lastChar+1 - offset);
    value[offset] = c;
    ++lastChar;
    return this;
  }

  // Finds the index in the given FastStringBuffer where the specified subString
  // begins.  -1 if not found.
  //
  /**
   * Finds the index in the given <code>FastStringBuffer</code> where
   * the specified substring begins.
   * @param subStr        the string to look for
   * @param fromIndex     the index to start the search from
   * @return              the index of the substring, or -1 if the string is not found
   */
  public int IndexOfSubString(FastStringBuffer subStr, int fromIndex) {
    return indexOf(subStr, fromIndex);
  }

   /**
    *  Returns the index within <code>FastStringBuffer</code> of the first
    *  occurrence of <code>subStr</code>, starting at the specified index.
    *  <p>
    *  There is no restriction on the value of <code>fromIndex</code>:
    *  <ul>
    *   <li>If it is negative, the entire string is searched.</li>
    *   <li>If is is 0, the entire string is searched. </li>
    *   <li>If it is greater than the length of the string, -1 is returned.</li>
    *   <li>If it is equal to the length of this string: -1 is returned.</li>
    *  </ul>
    * This method is just like <code>String.indexOf</code>
    * except that it uses <code>FastStringBuffer</code>.
    *
    * @param subStr     the substring to search for
    * @param fromIndex  the index to start the search from
    * @return
    */
  public int indexOf(FastStringBuffer subStr, int fromIndex) {
    char[] v1 = value;
    char[] v2 = subStr.value();
    int max = lastChar+1 - subStr.length();

    test:
    for (int i = ((fromIndex < 0) ? 0 : fromIndex); i <= max ; i++) {
      int n = subStr.length();
      int j = i;
      int k = 0;
      while (n-- != 0) {
        if (v1[j++] != v2[k++]) {
          continue test;
        }
      }
      return i;
    }
    return -1;
  }

   /**
    *  Returns the index within <code>FastStringBuffer</code> of the last
    *  occurrence of <code>subStr</code>. The returned index indicates the start of
    *  the substring, and it must be equal to or less than <code>fromIndex</code>.
    *  <p>
    *  There is no restriction on the value of fromIndex:
    *  <ul>
    *   <li>If it is negative, -1 is returned. </li>
    *   <li>If is is -1, -1 is returned. </li>
    *   <li>If it is greater than the length of the string,
    *       the entire string is searched. </li>
    *   <li>If it is equal to the length of this string: the entire
    *       string is searched.</li>
    *  </ul>This method is just like <code>String.lastIndexOf</code>
    * except that it uses <code>FastStringBuffer</code>
    *
    * @param subStr       the string to look for
    * @param fromIndex    the index to start the search from
    * @return             the index within <code>FastStringBuffer</code> of the last
    *                     occurrence of <code>subStr</code>
    */
  public int lastIndexOf(FastStringBuffer subStr, int fromIndex) {
    char[] v1 = value;
    char[] v2 = subStr.value();
    int max = lastChar+1 - subStr.length();

    test:
    for (int i = fromIndex; i >= 0 ; --i) {
      int n = subStr.length();
      int j = i;
      int k = 0;
      while (n-- != 0) {
        if (v1[j++] != v2[k++]) {
          continue test;
        }
      }
      return i;
    }
    return -1;
  }

  /**
   * Just like String.substring() but uses <code>FastStringBuffer</code>
   */

   /**
    *  Returns a new string that is a substring of this string.
    *  The substring begins at the specified <code>startPos</code>
    *  and extends to the character at <code>endPos - 1/code>.
    *  Thus the length of the substring is
    *  <code>startPos - endPos<code>.
    *
    * @param startPos   the beginning index, inclusive
    * @param endPos     the ending index, exclusive
    * @return           a new string that is a substring of this string
    */
  public FastStringBuffer substring(int startPos, int endPos) {
    FastStringBuffer fsb;
    if (startPos < 0)
      startPos = 0;
//!kna    if (endPos >= lastChar)
//!kna      endPos = (lastChar - 1);
    //!kna, for to fix Bug, we couldn't get last char before fixing...
    if (endPos > lastChar)
      endPos = lastChar;
    //!...kna
    if (startPos >= endPos)
      fsb = new FastStringBuffer();
    else fsb = new FastStringBuffer(value, startPos, endPos-startPos);
//System.err.println("fsb.subString(" + this + ", " + startPos + ", " + endPos + ") yielded:\r\n" + fsb);
    return fsb;
  }

  /**
   * Converts <code>FastStringBuffer</code> to a string.
   * @return
   */
  public String toString() {
    return new String(this.value, 0, lastChar+1);
  }

   /**
    * Given a <code>FastStringBuffer</code> where
    * <code>charAt()</code>, <code>nextChar()</code> or <code>priorChar()</code>
    * has just returned the backslash character (in other words, where
    * <code>value[offset] == '\\'</code>), this routine parses the
    * rest as a single character backslash value (for example,
    * "?") and advances the offset. It returns that character
    * and leaves the <code>FastStringBuffer</code>
    * pointing at the next character after the value.
    *
    * @return the single-character backslash value
    */
  public char parseBackSlash() {
    char c = nextChar();
    char cVal = displayToLiteral(c);  // if 'r', 'n', 't', etc, return '\r', '\n', or '\t', respectively
    if (cVal != NOTACHAR)
      return cVal;

    int radix;
    int maxChars;
    switch (c) {
      case 'U':
      case 'u':         //NORES
        radix = 16;
        maxChars = 4;
        break;
      case '0':
        radix = 8;
        maxChars = 3;
        c = peekNextChar();
        if (c == 'x' || c == 'X') {             //NORES
          radix = 16;
          maxChars = 2;
          ++offset;
        }
        else priorChar();
        break;

      default:
        return c;
    }

    FastStringBuffer s = new FastStringBuffer(maxChars);
    //!      System.err.println("FastStringBuffer.parseBackSlash: radix = " + radix + " and maxChars = " + maxChars);
    for (int i = 0; i < maxChars; ++i) {
      c = Character.toUpperCase(nextChar());
      if (!Character.isDigit(c)) {
        if (radix == 8 && c > '7' || radix == 16 && !(c >= 'A' &&  c <= 'F')) {
          priorChar();
          break;
        }
      }
      s.append(c);
    }
    //!      System.err.println("FastStringBuffer.parseBackSlash: " + s.toString() + " equals value " + Integer.parseInt(s.toString(), radix));
    return (char) Integer.parseInt(s.toString(), radix);
  }

  /**
   * Given a string that needs to be parsed as a literal string (including
   * backslash characters), and assuming <code>value[offset]</code> is currently pointing
   * at the starting delimiter of this string, this routine buffers
   * everything up to (but not including) another delimiter like the first.
   * It advances the offset past that delimiter so subsequent string
   * processing can continue.
   *
   * @param delimiter the char value that marks the end of the literal
   * @param allowDouble a value of <b>true</b> means two delimiters in a
   * row evaluate to a single occurance of that literal in the string
   * and therefore is not a delimiter
   *
   * @return a new <code>FastStringBuffer</code> containing the literal
   */
  public FastStringBuffer parseLiteral(char delimiter, boolean allowDouble) {
    FastStringBuffer rsb = new FastStringBuffer();
    for (char c = nextChar(); c != NOTACHAR; c = nextChar()) {

      // Delimiter encountered -- it allowDouble and see 2 in a row,
      // then treat it as an embedded literal and keep scanning
      if (c == delimiter) {
        if (allowDouble && peekNextChar() == c) {
          rsb.append(c);
          nextChar();   // skip 2nd delimiter
          continue;
        }
        break;
      }

      // A backslash gets special evaluation
      if (c == '\\')
        c = parseBackSlash();

      rsb.append(c);
    }
    return rsb;
  }

  /**
   * Given a string that needs to be parsed as a literal string (including
   * backslash characters), and assuming <code>value[offset]</code> is currently pointing
   * at the starting delimiter of this string, this routine buffers
   * everything up to (but not including) another delimiter like the first.
   * It advances the offset past that delimiter so subsequent string
   * processing can continue.
   *
   *  @return a new <code>FastStringBuffer</code> containing the literal
   */
  public FastStringBuffer parseLiteral() {
    return parseLiteral(currentChar(), false);
  }

  /**
   * Creates a copy of the given <code>FastStringBuffer</code>, but translate
   * any characters in the specified delimiter set into
   * Unicode escape sequences, For example,
   * "sourceString, "<code>,</code>" becomes "<code>\u002C</code>".
   * This allows the new <code>FastStringBuffer</code> to use the normal
   * <code>StringTokenizer</code> for parsing.
   *
   * @param sourceString the <code>String</code> to be scanned
   * and converted The <code>String</code> itself is not altered.
   *
   * @param delimiters a <code>String</code> consisting of the
   * delimiters you don't want to see
   * in the output <code>StringBuffer</code>. For example
   * <p><code>new String("\t\r\n,")</code>
   * <p>Wherever these occur in the <code>sourceString</code>,
   * they are converted to a Unicode escape sequence.
   *
   * @return a new <code>FastStringBuffer</code> that contains all the
   * characters of <code>sourceString</code>, but with
   * all delimiters expanded to Unicode escape sequences.
   */
  public static FastStringBuffer expandDelimiters(String sourceString, String delimiters) {

    int len = (sourceString == null) ? 0 : sourceString.length();
    FastStringBuffer result = new FastStringBuffer(len);
    for (int i = 0; i < len; ++i) {
      char c = sourceString.charAt(i);
      if (delimiters.indexOf(c) >= 0)
        result.append(charToUnicodeEscape(c));
      else
        result.append(c);
    }
    //!    if (sourceString != null)
    //!      System.err.println("expandDelimiters turned \"" + sourceString + "\" into \"" + result.toString() + "\"");
    return result;
  }

   /**
    * Turns any Unicode escape sequences that would result in one of the
    * given delimiter characters into the displayable form of that delimiter.
    * This method is the opposite of <code>expandDelimiters</code>.
    * <p>
    * <b>Note:</b> Do NOT pass any non-displayable delimiters into this
    * method, for example, '\r'.
    *
    * @param delimiters   the Unicode escape sequences
    * @return
    */
  public FastStringBuffer normalizeDelimiters(String delimiters) {
    FastStringBuffer result = new FastStringBuffer(lastChar+1);
    int oldoffset;
    for (char c = firstChar(); c != NOTACHAR; c = nextChar()) {
      if (c == '\\') {
        oldoffset = offset;
        char cVal = parseBackSlash();    // get char literal following backslash
        if (delimiters.indexOf(cVal) >= 0) {    // Is it in our delimiter list?
          char cLog = literalToDisplay(cVal);    // Yes, can we convert to logical? (e.g. '\r', etc.)
          if (cLog != NOTACHAR) {
            result.append('\\');      // Yes, form readable version of literal
            result.append(cLog);
            continue;
          }
          result.append(cVal);      // No readable form, just append literal itself
          continue;
        }
        offset = oldoffset;        // unicode sequence not a delimiter -- leave as raw unicode
      }
      result.append(c);
    }
    //!      System.err.println("normalizeDelimiters turned \"" + this.toString() + "\" into \"" + result.toString() + "\"");
    return result;
  }

  /**
   * Returns a <code>String</code> that contains a Unicode escape
   * sequence representing the given character.
   * For example <code>charToUnicodeEscape('1')</code> returns
   * <code>new String("\\u0031")</code>.
   *
   * @param ch the character to be converted to a unicode escape sequence
   * @return a new String containing the escape sequence
   */
  public static String charToUnicodeEscape(char ch) {
    char hexChars[] = new char[6];
    hexChars[0] = '\\';
    hexChars[1] = 'u';          //NORES
    hexChars[2] = Hex.chars[(ch >> 12) & 0xF];
    hexChars[3] = Hex.chars[(ch >>  8) & 0xF];
    hexChars[4] = Hex.chars[(ch >>  4) & 0xF];
    hexChars[5] = Hex.chars[ch & 0xF];
    return new String(hexChars, 0, 6);
  }

  private static char displayToLiteral(char c) {
    for (int i = 0; i < displayLiteral.length; ++i)
      if (c == displayLiteral[i])
        return logicalLiteral[i];
    return NOTACHAR;
  }

  private static char literalToDisplay(char c) {
    for (int i = 0; i < logicalLiteral.length; ++i)
      if (c == logicalLiteral[i])
        return displayLiteral[i];
    return NOTACHAR;
  }

  /**
   * Returns the first logical char value from the given
   * <code>String</code>.  This means it handles backslashes,
   * Unicode escape sequences, and so on.
   */
  public static char charFromString(String s) {
    //System.err.println("charFromString(" + s + ")");
    char c = (char) Character.UNASSIGNED;
    if (s != null && s.length() >= 0) {
      if (s.length() == 1)
        c = s.charAt(0);
      else {
        FastStringBuffer fsb = new FastStringBuffer(s);
        c = fsb.firstChar();
        if (c == '\\')
          c = fsb.parseBackSlash();
      }
    }
    //System.err.println(" returning <" + c + "> which is value: " + (int) c);

    return c;
  }


/**
 * Returns a <code>String</code> which best represents the given character.
 * This means that it expands it into a Unicode escape sequence if needed.
 * This method is the opposite of <code>charFromString()</code>.
 * @param c   the character to expand
 * @return    a String which best represents the given character
 */
  public static String stringFromChar(char c) {
    String s;
    //System.err.println("stringFromChar(" + (int) c + ")");
    char cLog = literalToDisplay(c);
    if (cLog != NOTACHAR)
      s = "\\" + cLog;
    // Anything less than SPACE or 2-bytes goes to unicode
    else if ((int) c < 32 /*|| (int) c > 256*/)
      s = charToUnicodeEscape(c);
    else
      s = new String(new char[] {c});

    //System.err.println(" returning <" + s + ">");
    return s;
  }

  /**
   *  Translates a <code>String</code> which is compatible with source code
   *  (including leading and trailing quotes, expanded backslash characters,
   *  and so on) into its actual <code>String</code> representation.
   *  For example, the literal "\n" becomes the real linefeed character.
   *  @param source    the String to translate
   */
  public static FastStringBuffer sourceToText(String source) {
//System.err.println("sourceToText(" + source + ")");
    FastStringBuffer text = new FastStringBuffer();
    FastStringBuffer input = new FastStringBuffer(source);

    // Strip optional leading and trailing quotes
    if (input.charAt(0) == '\"')
      input.removeCharAt(0);
    int len = input.length();
    if (len > 0 && input.charAt(len-1) == '\"')
      input.removeCharAt(len-1);

    for (char ch = input.firstChar(); ch != NOTACHAR; ch = input.nextChar()) {
      //System.err.println("sourceToText is " + ch);
      if (ch == '\\')
        ch = input.parseBackSlash();
//System.err.println("appending char <" + (int) ch + "> which is [" + ch + "]");
      //System.err.println("  appending char val " + (int) ch);
      text.append(ch);
    }
    return text;
  }

  static final String needingEscape = "\b\t\n\f\r\"\\";  //NORES

  /**
   * Converts a <code>String/code> into a form that compiles,
   * translating special characters into backslash-combination
   * equivalents and adding leading and trailing quotes.
   *
   * @param text
   * @param hasEscapes
   * @return
   */
  public static FastStringBuffer textToSource(String text, boolean hasEscapes) {
    return textToSource(text, hasEscapes, "    ");
  }

  private static final int SUGGESTED_MAX_SOURCE_COLS = 64;    // max columns before line break (if can find whitespace)
  private static final int ABSOLUTE_MAX_SOURCE_COLS = 100;    // max columns before line break -- will NOT exceed this

  /**
   *  Converts a <code>String/code> into a form that compiles, translating special
   *  characters into their backslash-combination equivalents, and adding
   *  leading and trailing quotes. Adds the specified number of indent spaces
   *  to the beginning of the string.
   *
   * @param text
   * @param hasEscapes
   * @param indentString
   * @return
   */
  public static FastStringBuffer textToSource(String text, boolean hasEscapes, String indentString) {
    FastStringBuffer source = new FastStringBuffer(text.length()*2+2);
    source.append("\"");
    //System.err.println(" length=" + text.length() + " hasEscapes=" + hasEscapes);
    int colCount = 0;

    for (int i = 0; i < text.length(); i++) {
      char ch = text.charAt(i);
      //System.err.println("char[" + i + "]=" + (int) ch );

      // If we are anticipating escape sequences, look ahead to see
      // whether a backslash is followed by one of the legal escape
      // prefixes and then by a digit -- that's enough to presume
      // the backslash does not get a 2nd backslash
      if (ch == '\\' && hasEscapes && i+2 < text.length()) {
        //System.err.println(" lookahead = <" + text.charAt(i+1) + ">  and  <" + text.charAt(i+2) + ">");
        char digit = text.charAt(i+2);
        switch (text.charAt(i+1))
        {
          case '0':
            if ((digit == 'x' || digit == 'X') && i+3 < text.length())          //NORES
              digit = text.charAt(i+3);
            // falls into
          case 'u':             //NORES
          case 'U':
            if (digit < '0' || digit > '9')
              break;
            // falls into
            //System.err.println("  looks like backslash escape");
            source.append(ch);
            continue;
        }
      }
      //System.err.println("  not escape");
      if (needingEscape.indexOf(ch) >= 0) {
        //System.err.println("  needing escape");
        source.append("\\");
        switch (ch) {
          case '\b': source.append('b'); break;         //NORES
          case '\t': source.append('t'); break;         //NORES
          case '\n': source.append('n'); break;         //NORES
          case '\f': source.append('f'); break;         //NORES
          case '\r': source.append('r'); break;         //NORES

          // 2 backslashes in a row echo through as 2 backslashes
          //!RC 5/20/97
          case '\\': source.append('\\');
                     if (hasEscapes && (i+1) < text.length() && text.charAt(i+1) == '\\')
                       source.append(text.charAt(++i));
                     break;

          default:  source.append(FastStringBuffer.stringFromChar(ch)); break;
        }
      }
      else
        source.append(FastStringBuffer.stringFromChar(ch));

      // Because the compiler can accept lines no longer than 1024, and because
      // really long lines look ugly, we try to break them when then exceed a
      // low threshhold if we see whitespace.  But in no case do we exceed the
      // absolute limit on length without breaking
      ++colCount;
      if (i < (text.length() - 1) && colCount > SUGGESTED_MAX_SOURCE_COLS) {
        boolean breakHere = Character.isWhitespace(ch);
        if ((breakHere && colCount > SUGGESTED_MAX_SOURCE_COLS) ||
            colCount > ABSOLUTE_MAX_SOURCE_COLS) {
          source.append("\" +\n");
          source.append(indentString);
          source.append("\"");
          colCount = 0;
        }
      }
    }
    source.append("\"");
    return source;
  }

}

