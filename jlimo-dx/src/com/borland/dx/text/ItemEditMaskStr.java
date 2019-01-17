//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/ItemEditMaskStr.java,v 7.1.2.1 2004/04/21 00:24:08 sshaughn Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import com.borland.dx.text.SystemResourceBundle;
import com.borland.dx.text.VariantFormatStr;
import com.borland.dx.text.ItemEditMaskState;
import com.borland.dx.text.ItemEditMaskRegionChar;
import com.borland.dx.text.ItemEditMaskRegion;
import com.borland.dx.text.ItemEditMaskChar;
import com.borland.dx.text.ItemEditMask;
import com.borland.dx.text.VariantFormatter;
import com.borland.dx.text.InvalidFormatException;
import com.borland.dx.text.TextFormat;
import com.borland.dx.text.BooleanFormat;
import com.borland.jb.util.Trace;
import com.borland.jb.util.FastStringBuffer;
import com.borland.jb.util.DiagnosticJLimo;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.sql.*;

import java.io.*;

//!import java.awt.*;
//!import java.awt.Dimension;
import java.awt.Event;


import com.borland.dx.dataset.Variant;

/**
 * The ItemEditMaskStr class implements the ItemEditMask interface using pattern
 * strings to control formatting, parsing, and edit interactions.
 */
public class ItemEditMaskStr implements ItemEditMask, ItemEditMaskRegionChar, Serializable
{
  VariantFormatter formatter; // Formatter interface object
  char     blankChar;         // the char that signifies a blank in the edit mask
  String editMask;            // the uncooked editmask
  String trueEditMask;        // true mask usable by JDK
  Vector editRegions;         // array of regions which can operate on each char
  Locale locale;              // locale (usually the current machine default)
  int variantType;            // as defined in Variant.getType()
  int formatterType;          // see VariantFormatter types
  char decimalSign;           // localized form of decimal point
  char thousandsSign;         // localized form of thousands separator
  int decimalAt;              // position in string where decimal point occurred
  int decimalDigits;          // decimal places in pattern
  char replaceBlanksWith;     // char used to replace the '_' we inject in blank slots
  char autoSkipLiteral;       // set whenever we passed over a literal automatically
  byte[] regionMap;           // we maintain a map from char position to region
  int startCursor;            // initial position for cursor (as determined by edit mask)
  boolean makeSymbolsLocal;   // set true if we need to localize decimalsign etc.
  int lastEditPos;            // last legal edit position (minus trailing literals)
  int signPrefixOffset;       // -1 or char offset where leading sign literal begins
  int signSuffixOffset;       // -1 or char offset where trailing sign literal begins
  boolean noPattern;
  boolean allowLeftShift;     // true allows left shift (ATM) mode of data entry
  /**
   * The constructor for a string-based ItemEditMask implementation.
   *
   * @param editMask Contains a String which will control the character-by-character editing
   * semantics when used by a text control.  If null or empty, will inherit the formatMask
   * from the 'formatter' parameter.
   *
   * @param formatter Contains the implementation object of the VariantFormatter interface.  This
   * class works closely with this object.  If this parameter is null, a default one will be
   * constructed from the other parameters.
   *
   * @param variantType Contains one of the values defined in Variant.  This defines the type
   * of data returned from the getValue() method.  If zero, it will default to that used by
   * 'formatter'.
   *
   * @param locale Contains the locale to use.  If null, will use the Locale from 'formatter'.  If
   * both are null, will use the current machine's default locale.
   *
   * <P><B>Note:</B> You do not need to construct an ItemEditMask for every text field, only those
   * you want to constrain input on a character-by-character basis.
   *
   * @see VariantFormatStr#VariantFormatStr
   * @see ItemEditMask
   *
   */
  public ItemEditMaskStr(String editMask, VariantFormatter formatter, int variantType, Locale locale) {
    blankChar = '_';    // we will empty slots with this character
    replaceBlanksWith = 0;  // we replace empty slots with this one (0 = remove)
    startCursor = -1;
    autoSkipLiteral = 0;
    trueEditMask = null;
    makeSymbolsLocal = false;
    regionMap = null;
    decimalAt = -1;
    lastEditPos = 0;
    signPrefixOffset = -1;
    signSuffixOffset = -1;
    noPattern = false;

//    Diagnostic.addTraceCategory(Trace.EditMaskStr);

    this.editMask = editMask;
    this.variantType = variantType;  // zero means undefined -- see EditMaskTypes
    this.formatterType = VariantFormatStr.formatTypeFromVariantType(variantType);
    //
    // First of all, if we have a formatter, we may have some defaults already
    // If not, let's manufacture one, since we'll be needing it.
    //
    if (editMask != null)    // strip out Borland specific extensions so JDK accepts masks
      trueEditMask = (formatterType == VariantFormatter.TEXT) ? editMask : VariantFormatStr.buildTrueFormatMask(editMask);

    if (formatter == null) {
      DiagnosticJLimo.trace(Trace.EditMaskStr, "Null formatter -- making our own with editMask: " + editMask);
      //!this.formatter = new VariantFormatStr(/*(formatterType == VariantFormatter.TEXT) ? editMask :*/ trueEditMask, variantType, locale);
      this.formatter = new VariantFormatStr(trueEditMask, variantType, locale);
      if (this.formatter == null)
        DiagnosticJLimo.trace(Trace.EditMaskStr, "failed");
    }
    else this.formatter = formatter;

    //
    // No locale means get it from the formatter (which defaulted it it had
    // none given to it)
    //
    this.locale = (locale == null) ? this.formatter.getLocale() : locale;

    //!  Diagnostic.trace(Trace.EditMaskStr, "Locale is: " + this.locale.getDisplayName());

    //
    // If there is no pattern string (odd practice), we can actually use
    // what the formatter is using.  Chances are that it was also given
    // a null string and selected a default for the locale.
    //
    this.editMask = (editMask == null || editMask.length() == 0)
                    ? this.formatter.getPattern()
                    : editMask;
    DiagnosticJLimo.trace(Trace.EditMaskStr, "ItemEditMaskStr: using pattern: " + this.editMask);

    //! Diagnostic.trace(Trace.EditMaskStr, "ItemEditMaskStr constructor for " + Variant.typeName(this.variantType) + " using pattern \"" + this.editMask + "\"");

    //
    // Determine this locale's decimal point, etc.
    //
    localizeSymbols();

    //
    // Strip out any Borland extensions to the edit mask so JDK can accept them
    //
    trueEditMask = VariantFormatStr.buildTrueFormatMask(this.editMask);

    //! TODO <rac> Won't this screw up formatting if someone else uses this formatter?  See if need it.
    //! this.formatter.setPattern(trueEditMask);

    //
    // Get the default data type we will be dealing with in getFinalValue either as passed in or from
    // the formatter with which we are associated.
    //
    this.variantType = (variantType < Variant.NULL_TYPES) ? this.formatter.getVariantType() : variantType;

    //
    // It is convenient to carry around a small enum value telling us what kind of formatting/parsing we
    // are doing (numeric vs date/time vs text)
    //
    formatterType = VariantFormatStr.formatTypeFromVariantType(variantType);

    // We store a list of all regions in this vector
    editRegions = new Vector(0);

    // Special case added primarily for strings.  If there is no pattern by this point, set a flag
    // which will condition all editing to allow ANYTHING to be typed
    if (this.editMask == null || this.editMask.length() == 0) {
      DiagnosticJLimo.trace(Trace.EditMaskStr, "null editmask making RegionAny");
      noPattern = true;
      editRegions.addElement(new ItemEditMaskRegionAny(this));
    }

    switch (formatterType) {
      case VariantFormatter.DECIMAL:
        //! TODO <rac> right now shares double formatting
      case VariantFormatter.NUMERIC:
        //! TODO: JDK doesn't provide way to pass locale to decimalFormat -- so use locale default
        //! TODO numFormat = (DecimalFormat) NumberFormat.getDefault();
        //! TODO <rac>numFormat.setPattern(trueEditMask, true);
        makeSymbolsLocal = true;  // we want to localize decimal point etc.
        buildNumericRegions();
        break;

      case VariantFormatter.DATETIME:
        //! TODO <rac>timeFormatData = TimeFormat.getTimeFormatData(locale);
        //! TODO <rac>SimpleTimeFormat timeFormat = new SimpleTimeFormat(trueEditMask, timeFormatData);
        buildDateTimeRegions();
        break;

      case VariantFormatter.TEXT:
        //! TODO <rac>textFormat = new TextFormat(trueEditMask);
        // Note: we do NOT do the following line.  We always force '_' to be the fill character
        // during edit mask editing.
        //      blankChar = this.formatter.getSpecialCharacter(Formatter.FILLCHARACTER);

        // However, we do let the user determine what the empty slots become after parsing
        Character cObj = (Character) this.formatter.getSpecialObject(VariantFormatter.REPLACECHARACTER);
        char c = (cObj == null) ? 0 : cObj.charValue();
        replaceBlanksWith = (c == 0 || c == TextFormat.NOT_A_CHAR) ? 0 : c;

        //!RC 9/22/97 to allow string patterns to specify visible "blank"
        cObj = (Character) this.formatter.getSpecialObject(VariantFormatter.FILLCHARACTER);
        c = (cObj == null) ? '_' : cObj.charValue();
        blankChar = (c == 0 || c == TextFormat.NOT_A_CHAR) ? '_' : c;
//!System.err.println("blankchar is <" + blankChar + ">");

        makeSymbolsLocal = true;  // we want to localize decimal point etc.
        buildTextRegions();
        break;

      case VariantFormatter.BOOLEAN:
        buildBooleanRegions();
        break;

      default:
          DiagnosticJLimo.trace(Trace.EditMaskStr, "Didn't decode " + formatterType);
        return;      // Note: early return on error -- don't put vital code below
    }

    //
    // Now go build a 1:1 mapping showing which character position in the edit buffer maps to which
    // ItemEditMask region.
    //
    buildRegionMap();
  }

  public ItemEditMaskStr(String editMask, VariantFormatter formatter, int variantType) {
    this(editMask, formatter, variantType, null);
  }

  // =============== ItemEditMask interface implementations follow ====================================
  //

  /**
   * Prepare() is the first step every control has to perform to begin editing a value.  It allocates
   * necessary objects and returns an ItemEditMaskState which the control must own.
   *
   * @param value Contains the value already in the field which is to editted.  It will be cast to the
   * appropriate type as specified in our constructor.  A null or unassigned value will get an empty
   * edit mask (with '-' chars where input should go).
   *
   * @return An ItemEditMaskState which contains all the state information (like the edit buffer and
   * the cursor position.  This keeps control-instance data out of the ItemEditMask object.
   *
   */
  public ItemEditMaskState prepare(Variant value) {

    if (startCursor < 0)
      startCursor = 0;
    allowLeftShift = false;   // don't allow ATM mode till user starts typing

    DiagnosticJLimo.trace(Trace.EditMaskStr, "ItemEditMaskStr.prepare(" + editMask + ", " + startCursor + ")");
//!System.err.println("ItemEditMaskStr.prepare(" + value + ")");

    ItemEditMaskState state =
       (noPattern ? new ItemEditMaskState(0, 0)
                  : new ItemEditMaskState(regionMap.length, (startCursor < 0) ? regionMap.length-1 : startCursor));
    ItemEditMaskStrData ems = new ItemEditMaskStrData(state);    // note: self-attaches to state in constructor
    //
    // Get the cursor off a literal if it is on one
    //
    state.cursorPos = moveCursorOffLiteral(state.cursorPos);
    //
    // A null or unassigned variant always gets a default empty template
    //
    if (value == null || value.isNull()) {
      buildEmptyEditString(state);
    }
    //
    // Otherwise, we format the variant into ItemEditMaskState.displayString
    //
    else {
      buildVariantEditString(state, value);
    }

    return state;
  }

  /**
   *
   * Called from the edit control to move the cursor.  This method will skip over literals and such.
   *
   * @param state Is the object returned from prepare() containing the edit buffer and cursor position.
   *
   * @param keyCode contains the keystroke navigation character.  Legal values include: Event.LEFT, Event.RIGHT,
   * Event.HOME and Event.END.  Random mouse clicks are handled by the state.cursorPos being set by the edit
   * control and one of the Event.Mouse events.
   *
   * @return True means the key was handled and the cursor (inside 'state') is now in a new position.  The
   * edit control is responsible for moving the actual cursor onscreen to match.
   */
  public boolean move(ItemEditMaskState state, int eventCode) {
    allowLeftShift = false;
    return internalMove(state, eventCode);
  }

  /**
   *
   * Called by the edit control to insert the given character at the position in state.cursorPos.
   *
   * @param state Is the object returned from prepare() containing the edit buffer and cursor position.
   *
   * @return True means the character was inserted (it may have changed case, etc. so the entire string
   * will require repainting.
   *
   *
   */
  public boolean insert(ItemEditMaskState state, char c) {

    int ic = (int) c;
    DiagnosticJLimo.trace(Trace.EditMaskStr, "Insert(" + c + ") whose value is " + ic);
    //
    // We must make sure the cursorPos is reasonable.  If it is past the
    // end of the string, shift everything left to make room.
    //
    //!  Diagnostic.trace(Trace.EditMaskStr, "Being asked to insert '" + c + "' at cursor " + state.cursorPos);
    if (state.cursorPos < 0)
      state.cursorPos = 0;

    //
    // If the user types the decimal separator against a numeric edit mask, we decimal align.  This
    // allows the users to enter something like 1.23 starting ANYWHERE in the numeric string and get
    // a reasonable result.
    //
    if (decimalAt > 0 && c == decimalSign)
      return decimalAlign(state);

    //Diagnostic.println(" allow = " + allowLeftShift + ", pos = " + state.cursorPos + ", last = " + lastEditPos);
    if (noPattern == false && allowLeftShift && state.cursorPos >= lastEditPos) {
      DiagnosticJLimo.trace(Trace.EditMaskStr, "We are now shifting left...");
      shiftLeft(state);
      state.cursorPos = lastEditPos;  //! TODO old way  -->regionMap.length-1;
    }

    // Allow left shifting only when typing on the extreme right
    else if (state.cursorPos >= (lastEditPos) &&
             (formatterType == VariantFormatter.DECIMAL || formatterType == VariantFormatter.NUMERIC ||
              (formatterType == VariantFormatter.TEXT && !isRegionComplete(state, regionMap[state.cursorPos])))) {
      //Diagnostic.println("enabling left shift");
      allowLeftShift = true;
    }

    // If this is a sign character, do all the necessary fixups
    if (handleSign(state, state.cursorPos, c, true))
      return true;


    if (isValid(state.cursorPos, c)) {
      DiagnosticJLimo.trace(Trace.EditMaskStr, "Storing the character...");
      setCharAt(state.displayString, state.cursorPos, c);

      DiagnosticJLimo.trace(Trace.EditMaskStr, "Advancing cursor...");
      internalMove(state, KeyEvent.VK_RIGHT);
      return true;
    }

    //
    // If we are not allowed to put this character at this position,
    // perhaps it is some literal separator.  If so, position the cursor
    // immediately after it. Skip over other non matching literals, and
    // accept only those which are only one character long.
    //
    else if (autoSkipLiteral == c) {
      DiagnosticJLimo.trace(Trace.EditMaskStr, "Swallowing an auto-skipped literal");
      autoSkipLiteral = 0;
      return true;
    }
    else if (autoSkipLiteral != c) {
      int i;
      ItemEditMaskRegionLiteral eml;
      DiagnosticJLimo.trace(Trace.EditMaskStr, "Looking to see if can skip to this literal");
      //!TODO for (i = state.cursorPos; i <= lastEditPos /* TODO!regionMap.length*/; ++i) {
      for (i = state.cursorPos; i <= lastEditPos ; ++i) {
        if (isLiteral(i)) {
          DiagnosticJLimo.trace(Trace.EditMaskStr, "  considering literal at position " + i);
//!          eml = (ItemEditMaskRegionLiteral) editRegions.elementAt(regionMap[i]);
          eml = (ItemEditMaskRegionLiteral) getRegionFromPosition(i);
          if (eml.capacity != 1) {
            DiagnosticJLimo.trace(Trace.EditMaskStr, "  skipping, literal too long");
            continue;
          }
          //if (c == state.displayString.charAt(i))
          if (c == eml.literal.charAt(0)) {
            state.cursorPos = i;
            DiagnosticJLimo.trace(Trace.EditMaskStr, "  yes -- moving to position " + i);
            internalMove(state, KeyEvent.VK_RIGHT); //! /*Event.RIGHT*/);
            return true;
          }
          else {
            ic = (int) eml.literal.charAt(0);
            DiagnosticJLimo.trace(Trace.EditMaskStr, " hmmm... skipped literal was \"" + eml.literal.toString() + " which is value " + ic);
          }
        }
      }
    }
    //
    // Here if character was illegal -- we might beep or something
    //
    DiagnosticJLimo.trace(Trace.EditMaskStr, "Insert failed: char = \'" + c + "\' and autoskiplit = \'" + autoSkipLiteral + "\'");
    return false;
  }

  /**
   * Delete the given range of characters from the edit buffer.  This generally
   * means that the characters will be replaced by the underscore character.
   *
   * @param state is the ItemEditMaskState returned by prepare()
   *
   * @param startPos is the starting position within the edit buffer (where 0 is
   * the first character) to begin the delete.
   *
   * @param count Is the number of characters to delete
   *
   *
   * @return A "true" return means the deletion occurred and the edit string is
   * now different.  A "false" means the delete could not take place.
   */

  public boolean delete(ItemEditMaskState state, int startPos, int count) {

    boolean bResult = false;
    DiagnosticJLimo.trace(Trace.EditMaskStr, "Delete: from " + startPos + " for " + count + " chars");
    if (startPos < 0)
      startPos = 0;
    for (int pos = startPos; pos <= lastEditPos && count > 0; pos++, --count) {
      if (isLiteral(pos))
        continue;

      DiagnosticJLimo.trace(Trace.EditMaskStr, "  deleting char as pos " + pos);

      // Deleting a sign character needs to fixup the "other" sign char
      if (!handleSign(state, pos, blankChar, false))
        deleteCharAt(state.displayString, pos, blankChar);
      setLastEditPosition(state.displayString);
      bResult = true;
    }

    return bResult;
  }

  /**
   * Asks whether all the required fields in the edit buffer have been provided.
   * This method does NOT perform validation.
   *
   * @param state is the ItemEditMaskState returned by prepare()
   *
   * @return A "true" return means all required fields have been filled in.  A
   * "false" return means there are still some lacking.  In the "false" case,
   * state.cursorPos will be set to point at the first required character which
   * has been left empty.
   */
  public boolean isComplete(ItemEditMaskState state) {

    DiagnosticJLimo.trace(Trace.EditMaskStr, "ItemEditMaskStr.isComplete()");

    int i;
    for (i = 0; i < editRegions.size(); ++i)
      if (!isRegionComplete(state, i)) {
        //!System.err.println("isComplete() -- not complete in region " + i);
        return false;
      }
    //!System.err.println("isComplete() -- true");
    return true;
  }

  /**
   * This method is used to fetch into the given variant the results
   * from parsing the current edit buffer.
   *
   * @param state The ItemEditMaskState returned by prepare().
   *
   * @param value The Variant into which this method should store the value.
   *
   * <P><B>Note:</B> This method will never return a null Variant, but it <B>will</B> throw
   * an InvalidFormatException if the current edit buffer cannot be parsed.  This exception
   * class contains the cursor position where the failure occurred
   *
   */
  public void getFinalValue(ItemEditMaskState state, Variant value) throws InvalidFormatException {
    DiagnosticJLimo.trace(Trace.EditMaskStr, "ItemEditMaskStr.getFinalValue() short form");
    getVariantFromString(state, value, variantType);
  }

  /**
   * This method is used to fetch into the given variant the results
   * from parsing the current edit buffer but with control over the result type.
   *
   * @param state The ItemEditMaskState returned by prepare().
   *
   * @param value The Variant into which this method should store the value.
   *
   * @param variantType The data type to use to fill the 'value' Variant. This method
   * will cast the normal type associated with this data into this specified type.
   *
   * <P><B>Note:</B> This method will never return a null Variant, but it <B>will</B> throw
   * an InvalidFormatException if the current edit buffer cannot be parsed.  This exception
   * class contains the cursor position where the failure occurred
   *
   */
  public void getFinalValue(ItemEditMaskState state, Variant value, int variantType) throws InvalidFormatException {
    DiagnosticJLimo.trace(Trace.EditMaskStr, "ItemEditMaskStr.getFinalValue() long form");
    getVariantFromString(state, value, variantType);
  }

  // ======================== ItemEditMaskRegionChar interface implementations follow ==========================

  //
  // The concept here is that every character in the edit buffer can map directly to its controlling
  // region via a parallel mapping array.  Once we get the region, we can call into the basic
  // EditCharObj's associated with each character position within the region.
  //
  public boolean isValid(int charPosition, char c) {
    if (noPattern)
      return true;
    if (charPosition < 0 || charPosition > lastEditPos)
      return false;
    ItemEditMaskRegion emr = getRegionFromPosition(charPosition);
    return emr.isValid(charPosition, c);
  }

  public boolean isOptional(int charPosition) {
    if (noPattern)
      return true;
    boolean optional = false;
    if (charPosition < 0 || charPosition >= lastEditPos)
      optional = true;
    else {
      ItemEditMaskRegion emr = getRegionFromPosition(charPosition);
      optional = emr.isOptional(charPosition);
    }
    DiagnosticJLimo.trace(Trace.EditMaskStr, "  EditMaskStr reporting back " + optional);
    return optional;
  }

  public char setCharAt(StringBuffer str, int charPosition, char c) {
    if (charPosition < 0 || charPosition > lastEditPos) {
      DiagnosticJLimo.trace(Trace.EditMaskStr, "setCharAt: pos = " + charPosition + " and end = " + lastEditPos);
      return c;
    }
    ItemEditMaskRegion emr = getRegionFromPosition(charPosition);
    DiagnosticJLimo.trace(Trace.EditMaskStr, "setCharAt using region " + emr.getClass().getName());
    return emr.setCharAt(str, charPosition, c);
  }

  public char getCharAt(StringBuffer str, int charPosition) {
    ItemEditMaskRegion emr = getRegionFromPosition(charPosition);
    return emr.getCharAt(str, charPosition);

  }

  public boolean isLiteral(int charPosition) {
    ItemEditMaskRegion emr = getRegionFromPosition(charPosition);
    return emr.isLiteral(charPosition);
  }

  public void deleteCharAt(StringBuffer str, int charPosition, char blankChar) {
    if (charPosition < 0 || charPosition > lastEditPos)
      return;
    ItemEditMaskRegion emr = getRegionFromPosition(charPosition);
    emr.deleteCharAt(str, charPosition, blankChar);
  }

  public boolean isPassword(int charPosition) {
    ItemEditMaskRegion emr = getRegionFromPosition(charPosition);
    if (emr instanceof ItemEditMaskRegionText) {
      ItemEditMaskRegionText emt = (ItemEditMaskRegionText) emr;
      return emt.password;
    }
    return false;
  }

  // If you've already determined that the given character position is
  // for a literal, this will return what that literal should be
  public char literalAt(int charPosition) {
    ItemEditMaskRegion emr = getRegionFromPosition(charPosition);
    if (emr instanceof ItemEditMaskRegionLiteral) {
      return ((ItemEditMaskRegionLiteral)emr).literal.charAt(charPosition - emr.offset);
    }
    return blankChar;

  }

  //
  // ------------------------- Internal methods ------------------------------------
  //
  //

  /**
  * Every character in the edit buffer is associated with a controlling region which answers questions
  * about characters in that region.  This method returns the region for any character position (0-based).
  * Note that when no edit pattern exists, a region has been allocated at [0] for this.
  */
  private ItemEditMaskRegion getRegionFromPosition(int charPosition) {
    int pos = (noPattern ? 0 : regionMap[charPosition]);
    return (ItemEditMaskRegion) editRegions.elementAt(pos);
  }

  /**
  * Since patternless regions can grow the buffer, this is not abstracted from regionMap.length alone
  */
  private int getBufferLength(StringBuffer editString) {
    return (noPattern ? editString.length() : regionMap.length);
  }

  int setLastEditPosition(StringBuffer editString) {
    if (noPattern)
      lastEditPos = editString.length();
    return lastEditPos;
  }

  /**
   * Handles typing into the leading or trailing sign character.
   * Will keep the "other" sign char up to date
   */
  private boolean handleSign(ItemEditMaskState state, int charPos, char c, boolean inserting) {
    DiagnosticJLimo.trace(Trace.EditMaskStr, "handleSign at pos " + charPos);
    ItemEditMaskRegion emr = getRegionFromPosition(charPos);
    DiagnosticJLimo.trace(Trace.EditMaskStr, "  region is of class " + emr.getClass().getName());
    if (!(emr instanceof ItemEditMaskRegionSign))
      return false;

    DiagnosticJLimo.trace(Trace.EditMaskStr, "Inserting \'" + c + "\' at position " + state.cursorPos + " is a sign region");

    // If not valid, advance cursor off the sign character so user can just start typing digits
    if (!isValid(charPos, c)) {
      DiagnosticJLimo.trace(Trace.EditMaskStr, " not valid");
      if (inserting)
        internalMove(state, KeyEvent.VK_RIGHT);
      return false;
    }
    DiagnosticJLimo.trace(Trace.EditMaskStr, " valid, putting char <" + c + ">");

    // Put the typed character into this sign region
    setCharAt(state.displayString, charPos, c);

    //
    // Courtesy -- maintain the "other" sign (i.e. if this is a leading paren
    // find and fix the trailing paren)
    //
    int i;
    int len = getBufferLength(state.displayString);
    ItemEditMaskRegion otherEmr;
    for (i = 0; i <len; ++i) {
      otherEmr = getRegionFromPosition(i);
      if (otherEmr == emr) continue;
      if (!(otherEmr instanceof ItemEditMaskRegionSign)) continue;

      DiagnosticJLimo.trace(Trace.EditMaskStr, "found other sign at pos " + i + ", making char to be <" + c + ">");
      ItemEditMaskRegionSign ems = (ItemEditMaskRegionSign) otherEmr;
      setCharAt(state.displayString, i, c); //(c == ' ' || c == blankChar) ? blankChar : ems.c);
      break;
    }
    if (inserting)
      internalMove(state, KeyEvent.VK_RIGHT);
    return true;
  }

  private final String DateTimeFormat(java.util.Date dateVal, String pattern) {

    formatter.setPattern(pattern);
    Variant v = new Variant();

    //!RC Bug fix 6532: be sensitive to dataType so we don't lose precision
    //!RC Was this before 6/16/97 --> v.setDate(new java.sql.Date(dateVal.getTime()));
    switch (variantType) {
      case Variant.TIME:        v.setTime(dateVal.getTime());                      break;
      case Variant.TIMESTAMP:   v.setTimestamp(dateVal.getTime());                 break;
      case Variant.DATE:        // falls into
      default:                  v.setDate(new java.sql.Date(dateVal.getTime()));   break;
    }

//!System.err.println("    new java.sql.Date = " + new java.sql.Date(dateVal.getTime()));
//!System.err.println("    variant.setDate = " + v);
    //!  Class cls = dateVal.getClass();
    //!  Diagnostic.trace(Trace.EditMaskStr, "DateTimeFormat -- set class to " + cls.getName());

    String s = formatter.format(v);
    DiagnosticJLimo.trace(Trace.EditMaskStr, "DateTimeFormat(" + dateVal.toString() + ", " + pattern + ") yields: " + s);
//!System.err.println("  DateTimeFormat(" + dateVal.toString() + ", " + pattern + ") yields: " + s);
    return s;
  }

  private final boolean DateTimeParse(String dateString,
                                      String pattern,
                                      Variant value,
                                      int variantType) {

    DiagnosticJLimo.trace(Trace.EditMaskStr, "DateTimeParse: parsing \"" + dateString + " \" with \"" + pattern + "\"");
//!System.err.println("DateTimeParse: parsing \"" + dateString + " \" with \"" + pattern + "\"");
    formatter.setPattern(pattern);
    try {
      formatter.parse(dateString, value, variantType);
    }
    catch (InvalidFormatException e) {
//e.printStackTrace(System.err);
      DiagnosticJLimo.printStackTrace(e);
      return false;
    }

    DiagnosticJLimo.trace(Trace.EditMaskStr, "DateTimeParse: returning " + value);
    return true;
  }

  private final String NumericFormat(double doubleValue, String pattern) {
    formatter.setPattern(pattern);
    Variant v = new Variant();
    v.setDouble(doubleValue);
    return (formatter.format(v));
  }

  private final Number NumericParse(String numString, String pattern, ItemEditMaskState state) {
    Variant v = new Variant();
    v.setDouble(0.0);// to coerce the final type
    DiagnosticJLimo.trace(Trace.EditMaskStr, "NumericParse: parsing " + numString + " with " + pattern);
//!System.err.println("NumericParse: parsing " + numString + " with " + pattern);
    formatter.setPattern(pattern);
    try {
      formatter.parse(numString, v, Variant.DOUBLE);
    }
    catch (InvalidFormatException e) {
//e.printStackTrace(System.err);
      v = null;
    }
    if (v != null) {
      DiagnosticJLimo.trace(Trace.EditMaskStr, "  got a variant back of type " + v.getType());
      return new Double(v.getDouble());
    }
    return null;
  }

  private final String TextFormat(String text, char fillChar) {
    Character oldFill = null;
    if (fillChar != VariantFormatter.NOTACHAR)
      oldFill = (Character) formatter.setSpecialObject(VariantFormatter.FILLCHARACTER, new Character(fillChar));
    Variant v = new Variant();
    v.setString(text);
    String s = formatter.format(v);
    if (fillChar != VariantFormatter.NOTACHAR)
      formatter.setSpecialObject(VariantFormatter.FILLCHARACTER, oldFill);
    return s;
  }

  private final String TextParse(String textStr, char fillChar, char replaceChar) {

    Object oldFill = null;
    Object oldRepl = null;
    Variant v = new Variant();

    if (fillChar != VariantFormatter.NOTACHAR)
      oldFill = formatter.setSpecialObject(VariantFormatter.FILLCHARACTER, (Object) new Character(fillChar));
    if (replaceChar != VariantFormatter.NOTACHAR)
      oldRepl = formatter.setSpecialObject(VariantFormatter.REPLACECHARACTER, (Object) new Character(replaceChar));
    try {
      formatter.parse(textStr, v, Variant.STRING);
    }
    catch (InvalidFormatException e) {
      v = null;
    }
    if (fillChar != VariantFormatter.NOTACHAR)
      formatter.setSpecialObject(VariantFormatter.FILLCHARACTER, oldFill);
    if (replaceChar != VariantFormatter.NOTACHAR)
      formatter.setSpecialObject(VariantFormatter.REPLACECHARACTER, oldRepl);
    return (v == null) ? null : v.getString();
  }

  private final String BooleanFormat(Boolean bool) {
    try {
      Variant v = new Variant();
      if (bool != null)
        v.setBoolean(bool.booleanValue());
      else
        v.setAssignedNull();

      String s = formatter.format(v);
      return s;
    }
    catch (Exception ex) {
      ex.printStackTrace(System.err);
      return null;
    }
  }

  private final Boolean BooleanParse(String text) throws InvalidFormatException {
    Variant v = new Variant();
//!System.err.println("BooleanParse calling formatter.parse");
    formatter.parse(text, v, Variant.BOOLEAN);
//!System.err.println("BooleanParse: formatter returned " + v);
    return (v == null || v.isNull()) ? null : new Boolean(v.getBoolean());
  }
  //
  // This routine will determine whether the given region (identified by the character position) has the minimum
  // number of required characters.  Sometimes the user left or right justifies arbitrarily and may not match
  // the same spots we marked as required.  As a side-effect, it sets state.cursorPos to the first missing
  // position.  A true return means the region is complete.
  //
  private boolean isRegionComplete(ItemEditMaskState state, int nRegion) {
    DiagnosticJLimo.trace(Trace.EditMaskStr, "isRegionComplete(" + nRegion + ")");
//!System.err.println("isRegionComplete(" + nRegion + ")");

    StringBuffer s = state.displayString;
    ItemEditMaskRegion emr = (ItemEditMaskRegion) editRegions.elementAt(nRegion);
    if (emr instanceof ItemEditMaskRegionLiteral) {
      DiagnosticJLimo.trace(Trace.EditMaskStr, " literal -- return true");
      return true;
    }
    if (emr.optional) {    // quick out for fully optional regions
      DiagnosticJLimo.trace(Trace.EditMaskStr, " optional -- return true");
      return true;
    }

    int nBlanks = 0;    // count of blank characters which are marked 'required'
    int i;
    //!  Class cls = emr.getClass();
    //!  Diagnostic.trace(Trace.EditMaskStr, "Checking region completeness for " + cls.getName());

//!System.err.println("offset= " + emr.offset + ", capacity=" + emr.capacity + ", minReq=" + emr.minRequired);

    for (i = emr.offset; i < emr.offset + emr.capacity; ++i) {
      if (s.charAt(i) != blankChar)
        continue;
      ++nBlanks;
      if (emr.isOptional(i))
        continue;
      if (emr.minRequired == 0) {  // minRequired==0 means the region is fixed size and knows for each char position
        DiagnosticJLimo.trace(Trace.EditMaskStr, "  minRequired 0 -- setting cursorPos to " + i);
        state.cursorPos = i;  // meaning ANY missing characters for this kind of region means incomplete
//!System.err.println("bailing out");
        return false;
      }
    }
    DiagnosticJLimo.trace(Trace.EditMaskStr, "Number of required characters left blank = " + nBlanks);
//!System.err.println("nBlanks = " + nBlanks);
    if (nBlanks == 0)    // if no blanks found in required slots, we're home free
      return true;
    //
    // Last hope -- if we have some variable length field (as many of the dates are), we can be considered
    // complete as long as we have filled in at least 'minRequired'
    //
    if ((emr.capacity - nBlanks) >= emr.minRequired)
      return true;

    //!  Diagnostic.trace(Trace.EditMaskStr, cls.getName() + " is not complete -- nMissing=" + nBlanks + ", capacity=" + emr.capacity + ", minReq=" + emr.minRequired);

//$$$    state.cursorPos = (emr.rightToLeft) ? emr.offset + emr.capacity - emr.minRequired : emr.offset + emr.minRequired;
    if (emr.rightToLeft) {
      state.cursorPos = emr.offset + emr.capacity - emr.minRequired;
    }
    else {
      state.cursorPos = emr.offset + (emr.minRequired - nBlanks);
        //!RC bug 5738: computation for cursor position on left-to-right field was flawed.
    }
    DiagnosticJLimo.trace(Trace.EditMaskStr, "  offset = " + emr.offset + ", capacity = " + emr.capacity + ", minReq = " + emr.minRequired);
    DiagnosticJLimo.trace(Trace.EditMaskStr, " setting cursorPos to " + state.cursorPos);
    return false;
  }

  // This routine will shift the entire contents of the edit buffer left by one position.  It will stop
  // when it hits an illegal situation (e.g. moving a letter into a digit field).  It will also stop at
  // the first character in the string (i.e. it doesn't drop characters off the left) .It starts at the
  // state.cursorPos.  If that cursorPos is at a valid character, it will turn that character into
  // a blankChar.  Note: we do not shift blank characters, meaning the first blank character we see
  // as a potential source stops the shift.
  //
  protected boolean shiftLeft(ItemEditMaskState state) {
    StringBuffer s = state.displayString;
    int iLen = s.length();
    int posSrc;
    int posDst;
    int pos;

    if (noPattern)    // quick out if no pattern
      return false;
    //
    // To allow a 1-pass algorithm, we will build a mapping array.  For any element in this array
    // which is nonzero, it means that character position can take the character found at in the
    // edit buffer at position changeMap[i].  In other words, the value in this map tells where
    // the source character is located.  The index into this map tells where the destination character
    // is located.
    //
    short[] changeMap = new short[iLen];
    for (int i = 0; i < iLen; ++i)
      changeMap[i] = 0;      // note: can never have a source at pos 0

    pos = state.cursorPos;
    boolean anyChanges = false;
    if (pos > lastEditPos)
      pos = lastEditPos;
    for (; pos > 0; --pos) {
      //
      // Each time through this loop, we have to step over literals to find a valid source position
      //
      for (posSrc = pos; posSrc > 0; --posSrc) {
        if (!isLiteral(posSrc))
          break;
      }

      if (posSrc <= 0)       // no valid source position, we can't shift anymore
        break;

      if (s.charAt(posSrc) == blankChar)  // we never shift blanks, so stop the loop
        break;

      //
      // Do the same thing to find the next valid destination
      //
      for (posDst = posSrc-1; posDst >= 0; --posDst) {
        if (!isLiteral(posDst))
          break;
      }

      if (posDst < 0)    // no (more) valid destinations
        break;

      //
      // Here's the test -- will the destination position accept the source's character?
      //
      if (isValid(posDst, getCharAt(s, posSrc))) {  // use getCharAt to propogate password letters
        changeMap[posDst] = (short) posSrc;
        anyChanges = true;
      }
      else return false;  // if cannot be moved, don't do any moving

      //pos = posDst+1;  // this will cause top of loop to resume at current posDst
      pos = posSrc;      // resume loop at first char to the left of the one we just considered for moving
    }

    //
    // Nows, we have a map showing where swappable characters lie.  We can move through this list
    // left to right to actually move the characters
    //

    if (anyChanges) {
      char c;
      for (int i = 0; i < iLen; ++i) {
        if (changeMap[i] == 0)
          continue;
        c = getCharAt(s, changeMap[i]);        // char pos of char to be moved
        setCharAt(state.displayString, i, c);
      }
    }
    //
    // Finally, zap the original starting position to blankChar
    //
    pos = state.cursorPos;
    if (pos > lastEditPos)
      pos = lastEditPos;
    s.setCharAt(pos, blankChar);

    return anyChanges;
  }

  //
  // This method will load a few necessary symbols (like decimal point, etc.) from the given
  // locale
  //
  private final void localizeSymbols() {
    try {
      ResourceBundle resource = SystemResourceBundle.getLocaleElementsBundle(locale);

      String[] numberElements = resource.getStringArray("NumberElements");
      decimalSign = numberElements[0].charAt(0);
      thousandsSign = numberElements[1].charAt(0);
      DiagnosticJLimo.trace(Trace.EditMaskStr, "Decimal pt = \'" + decimalSign + "\', and thousands sep = \'" + thousandsSign + "\'");

    }
    catch (MissingResourceException e) {  // should NEVER get here, but if we do, use US symbols
      decimalSign = '.';
      thousandsSign = ',';
    }
  }

  //
  // This method will get the current cursor OFF a literal, since that is not an editable character
  //
  private final int moveCursorOffLiteral(int cursorPos) {
    int iLen = lastEditPos; //!TODO old way -->regionMap.length;
    int forwardPos;
    int backwardPos;

    DiagnosticJLimo.trace(Trace.EditMaskStr, "MoveCursorOffLiteral, pos = " + cursorPos + ", and len = " + iLen);

    // Algorithm: search forward and backward simultaneously, looking for a nonliteral region.

    if (cursorPos >= iLen)
      cursorPos = iLen;    //! TODO old way  -->>  - 1;
    if (cursorPos < 0)
      cursorPos = 0;

    if (!isLiteral(cursorPos))
      return cursorPos;

    for (forwardPos = cursorPos+1, backwardPos = cursorPos - 1; ; ++forwardPos, --backwardPos) {
      if (forwardPos >= iLen && backwardPos < 0)
        break;
      if (forwardPos < iLen) {
        if (!isLiteral(forwardPos))
          return forwardPos;
          else DiagnosticJLimo.trace(Trace.EditMaskStr, "pos " + forwardPos + " is a literal");
      }
      if (backwardPos > 0) {
        if (!isLiteral(backwardPos))
          return backwardPos;
          else DiagnosticJLimo.trace(Trace.EditMaskStr, "pos " + backwardPos + " is a literal");
      }
    }
    return cursorPos;  // we give up -- everything is a literal
  }

  //
  // This method will return the total number of character positions associated with all the regions
  // up to the given region number.
  //
  private final int charPositionsToHere(int iEditRegion) {
    ItemEditMaskRegion emr;
    int nChars = 0;
    if (iEditRegion >= editRegions.size())
      iEditRegion = editRegions.size() - 1;
    if (iEditRegion >= 0) {
      emr = (ItemEditMaskRegion) editRegions.elementAt(iEditRegion);
      nChars = emr.offset + emr.capacity;
    }
    DiagnosticJLimo.trace(Trace.EditMaskStr, "Cursor position at " + nChars);
    return nChars;
  }

  //
  // This method will parse the editMask and construct a set of regions for date/time fields
  //
  private final void buildDateTimeRegions() {
    char c;
    char nextC;
    FastStringBuffer editChars = new FastStringBuffer(editMask);        // sets currentChar offset to zero for 'for' loop
    FastStringBuffer workBuffer = new FastStringBuffer(editChars.length()); // empty but big enough never to grow
    boolean optional = false;
    int charCount = 0;
    int litCount = 0;

    for (c = editChars.firstChar(); c != FastStringBuffer.NOTACHAR; c = editChars.nextChar()) {
      DiagnosticJLimo.trace(Trace.EditMaskStr, "buildDateTimeRegions processing \'" + c + "\' at pos " + editChars.getOffset());
      //
      // We are here only for parsing the 1st subfield -- the editMask itself.  'c' contains the next char.
      //
      switch (c)
      {
        case '{':
          optional = true;
          continue;
        case '}':
          optional = false;
          continue;
        case '^':
          startCursor = charPositionsToHere(editRegions.size()) + litCount - 1;
          DiagnosticJLimo.trace(Trace.EditMaskStr, "Starting cursor is " + startCursor);
          continue;
          //
          // All date/time characters make their own edit region
          //
        case 'G':
        case 'y':
        case 'N':
        case 'd':
        case 'h':
        case 'H':
        case 'm':
        case 'M':
        case 's':
        case 'S':
        case 'E':
        case 'D':
        case 'F':
        case 'w':
        case 'W':
        case 'a':
        case 'k':
        case 'K':
        case 'z':
          if (litCount > 0) {
            editRegions.addElement(new ItemEditMaskRegionLiteral(this, workBuffer));
            litCount = 0;
            workBuffer.empty();
          }
          charCount = 1;
          while ((nextC = editChars.nextChar()) == c)
            ++charCount;
          DiagnosticJLimo.trace(Trace.EditMaskStr, " counted " + charCount + " of the letter " + c);
          if (nextC != FastStringBuffer.NOTACHAR)
            editChars.priorChar();
          switch (c)
          {
            case 'G':
              editRegions.addElement(new ItemEditMaskRegionERA(this, c, optional));
              break;
            case 'y':
              editRegions.addElement(new ItemEditMaskRegionYear(this, c, charCount, optional));
              break;
            case 'N':
              editRegions.addElement(new ItemEditMaskRegionMonthInYear(this, c, charCount, optional));
              break;
            case 'd':
              editRegions.addElement(new ItemEditMaskRegionDayInMonth(this, c, charCount, optional));
              break;
            case 'h':
              editRegions.addElement(new ItemEditMaskRegionHour12(this, c, charCount, optional));
              break;
            case 'H':
              editRegions.addElement(new ItemEditMaskRegionHour23(this, c, charCount, optional));
              break;
            case 'm':
              editRegions.addElement(new ItemEditMaskRegionMinute(this, c, charCount, optional));
              break;
            case 'M':
              editRegions.addElement(new ItemEditMaskRegionMonthInYear(this, c, charCount, optional));
              break;
            case 's':
              editRegions.addElement(new ItemEditMaskRegionSecond(this, c, charCount, optional));
              break;
            case 'S':
              editRegions.addElement(new ItemEditMaskRegionMillisecond(this, c, charCount, optional));
              break;
            case 'E':
              editRegions.addElement(new ItemEditMaskRegionDayInWeek(this, c, charCount, true));
              break;
            case 'D':
              editRegions.addElement(new ItemEditMaskRegionDayInYear(this, c, charCount, optional));
              break;
            case 'F':
              editRegions.addElement(new ItemEditMaskRegionWeekInMonth(this, c, charCount, optional));
              break;
            case 'w':
              editRegions.addElement(new ItemEditMaskRegionWeekInYear(this, c, charCount, optional));
              break;
            case 'W':
              editRegions.addElement(new ItemEditMaskRegionWeekInMonth(this, c, charCount, optional));
              break;
            case 'a':
              editRegions.addElement(new ItemEditMaskRegionAMPM(this, c, optional));
              break;
            case 'k':
              editRegions.addElement(new ItemEditMaskRegionHour24(this, c, charCount, optional));
              break;
            case 'K':
              editRegions.addElement(new ItemEditMaskRegionHour11(this, c, charCount, optional));
              break;
            case 'z':
              editRegions.addElement(new ItemEditMaskRegionTimeZone(this, c, charCount, optional));
              break;
            default:
                DiagnosticJLimo.trace(Trace.EditMaskStr, "Missing case in 2nd switch of BuildDateTimeRegions");
          }
          continue;

        case '\'':
          workBuffer.empty();
          DiagnosticJLimo.trace(Trace.EditMaskStr, "See single quote, pos is " + editChars.getOffset());
          FastStringBuffer fsb = editChars.parseLiteral(c, true);   // allow double quote
          DiagnosticJLimo.trace(Trace.EditMaskStr, "End single quote, pos is " + editChars.getOffset());
          workBuffer.append(fsb.value(), 0, fsb.length());
          DiagnosticJLimo.trace(Trace.EditMaskStr, "adding literal region: " + workBuffer);
          editRegions.addElement(new ItemEditMaskRegionLiteral(this, workBuffer));
          workBuffer.empty();
          litCount = 0;
          continue;

        case '\\':
          workBuffer.append(editChars.parseBackSlash());
          ++litCount;
          continue;

        default:
          workBuffer.append(c);
          ++litCount;
          continue;
      }
    }
    if (litCount > 0)
      editRegions.addElement(new ItemEditMaskRegionLiteral(this, workBuffer));
  }

  //
  // This method will parse the editMask and construct a set of regions for numeric fields
  //
  private final void buildNumericRegions() {
    char c;
    FastStringBuffer editChars = new FastStringBuffer(editMask);        // sets currentChar offset to zero for 'for' loop
    FastStringBuffer workBuffer = new FastStringBuffer(editChars.length()); // empty but big enough never to grow
    int charCount = 0;
    int litCount = 0;
    int cnt;
    String s;

    decimalDigits = 0;
    decimalAt = -1;

    DecimalFormat df = (DecimalFormat) this.formatter.getFormatObj();

    //!  if (df == null)
    //!    Diagnostic.trace(Trace.EditMaskStr, "No decimal formatter");
    //!  else {
    //!    Diagnostic.trace(Trace.EditMaskStr, "Negative prefix = \"" + df.getNegativePrefix());
    //!    Diagnostic.trace(Trace.EditMaskStr, "Negative suffix = \"" + df.getNegativeSuffix());
    //!  }

    //
    // Note: to allow negative numbers, we have to create a region capable of accepting one.
    // But, NOTE! NOTE! NOTE! -- we are assuming the first character of the negative prefix
    // is enough.  The decimalFormat object will combine the negative sign and any leading
    // literal prefix into one string, and we want to distinguish literals from edit slots
    //
    s = df.getNegativePrefix();
    if (df != null && s.length() > 0) {
      signPrefixOffset = 0;
      editRegions.addElement(new ItemEditMaskRegionSign(this, s.charAt(0), true, blankChar));
    }
    for (c = editChars.firstChar(); c != FastStringBuffer.NOTACHAR; c = editChars.nextChar()) {
      //
      // We are here only for parsing the 1st subfield -- the editMask itself.  'c' contains the next char.
      //
      DiagnosticJLimo.trace(Trace.EditMaskStr, "buildNumericRegion: char = \'" + c + "\'");
      switch (c)
      {
        case ';':      // the 2nd semicolon-separated mask is tossed out currently
          editChars.lastChar();
          break;

        case '^':
          startCursor = charPositionsToHere(editRegions.size()) + charCount + litCount - 1;
          DiagnosticJLimo.trace(Trace.EditMaskStr, "Starting cursor is " + startCursor);
          continue;
          //
          // All characters understood by the "EditRegionFreeform" are collected into a single string
          //
        case '0':
        case '#':
        case '{':
        case '}':
          if (litCount > 0) {
            editRegions.addElement(new ItemEditMaskRegionLiteral(this, workBuffer));
            litCount = 0;
            workBuffer.empty();
          }
          workBuffer.append(c);
          ++charCount;
          continue;
          //
          // EVERYTHING else is considered a literal in the numeric syntax
          //
        default:
          if (charCount > 0) {
            editRegions.addElement(new ItemEditMaskRegionNumeric(this, workBuffer));
            workBuffer.empty();
            if (decimalAt >= 0)
              decimalDigits += charCount;
            charCount = 0;
          }
          //!  Diagnostic.trace(Trace.EditMaskStr, "Adding \'" + c + "\' to literal string");

          // Note: all patterns use US decimal points and thousands separators, but we have to substitute
          // what the user expects to see.  When we go to parse the final string, we will have to reverse
          // this process, since DecimalFormat is doing the same thing at its end.
          //
          c = localizeChar(c, true);  // change the literal to its locale equivalent

          if (c == decimalSign)
            decimalAt = charPositionsToHere(editRegions.size()) + charCount + litCount;

          workBuffer.append(c);
          ++litCount;
          continue;
      }
    }
    if (litCount > 0)
      editRegions.addElement(new ItemEditMaskRegionLiteral(this, workBuffer));
    if (charCount > 0)
      editRegions.addElement(new ItemEditMaskRegionNumeric(this, workBuffer));
    if (decimalAt >= 0)
      decimalDigits += charCount;
    //
    // We also allow negative suffixes (think of ($nnn.00)
    // We also assume only the final character is sufficient, since again, DecimalFormat lumps
    // any literals together with this.  If we find what DecimalFormat calls a negative prefix,
    // and the last region we added to our list was a literal, we have to subtract out the literal
    // part.  If nothing remains, then there really is NO negative suffix -- only a literal.
    //
    s = df.getNegativeSuffix();
    DiagnosticJLimo.trace(Trace.EditMaskStr, "End of BuildNumericRegion: negative suffix is " + s);
    if (df != null && s.length() > 0) {
        DiagnosticJLimo.trace(Trace.EditMaskStr, "Found suffix " + s.length() + " chars long, previous literal was " + litCount + " chars long");
      if (s.length() > litCount) {
        DiagnosticJLimo.trace(Trace.EditMaskStr, "So we are adding a suffix negative sign region");
        signSuffixOffset = editRegions.size();
        editRegions.addElement(new ItemEditMaskRegionSign(this, s.charAt(s.length()-1), false, blankChar));
      }
        else DiagnosticJLimo.trace(Trace.EditMaskStr, "So we are NOT adding a suffix sign");
    }
  }

  //
  // This routine is a workhorse that tries to move the cursor in the indicated direction.  It has to be
  // careful not to position itself on a literal
  //
  private final boolean internalMove(ItemEditMaskState state, int keyCode) {

    DiagnosticJLimo.trace(Trace.EditMaskStr, "internalMove: now at pos " + state.cursorPos);

    setLastEditPosition(state.displayString); // this allows for growing strings

    int pos = state.cursorPos;
    int incr = 1;
    //! TODO <rac> trying new way ... int iLen = state.displayString.length();
    autoSkipLiteral = 0;

    switch (keyCode)
    {
      case KeyEvent.VK_HOME:  //Event.HOME:
        pos = 0;
        break;
      case KeyEvent.VK_END: //Event.END:
        //pos = iLen - 1;
        pos = lastEditPos;
        incr = -1;
        break;
      case KeyEvent.VK_LEFT:  //Event.LEFT:
        incr = -1;
        // falls into RIGHT
      case KeyEvent.VK_RIGHT: //Event.RIGHT:
        pos += incr;
        break;
      case Event.MOUSE_DOWN:
      case MouseEvent.MOUSE_CLICKED:
        break;
      default:
//!System.err.println("default -- out");
        return false;
    }

    DiagnosticJLimo.trace(Trace.EditMaskStr, " initial move took us to pos " + pos);
    //
    // We want to scan over any literals.  Note that if we find any single character literals
    // along the way which are skipped, we keep track of it.  This is an optimization so that if
    // the next thing the user types IS that literal, we can ignore it.
    //
//!System.err.println("pos = " + pos + ", lastPos = " + lastEditPos + ", incr = " + incr);
    if (pos > lastEditPos)
      pos = lastEditPos;

    for (; (pos >= 0 && pos <= lastEditPos); pos += incr) {  // scan over literals
//!System.err.println("testing pos " + pos);
      if (pos < lastEditPos && isLiteral(pos)) {
//        ItemEditMaskRegionLiteral emr = (ItemEditMaskRegionLiteral) editRegions.elementAt(regionMap[pos]);
        ItemEditMaskRegionLiteral emr = (ItemEditMaskRegionLiteral) getRegionFromPosition(pos);
        if (emr != null && emr.capacity == 1) {
          autoSkipLiteral = emr.literal.charAt(0);
          DiagnosticJLimo.trace(Trace.EditMaskStr, "Detected autoskip literal \'" + autoSkipLiteral + "\'");
        }
        else autoSkipLiteral = 0;
        continue;
      }
      DiagnosticJLimo.trace(Trace.EditMaskStr, " final move took us to pos " + pos);
      state.cursorPos = pos;
      return true;
    }

    // If run off end with literals and were checking random position,
    // run back in the opposite direction
    if (keyCode == MouseEvent.MOUSE_CLICKED || keyCode == Event.MOUSE_DOWN) {
      pos = state.cursorPos;
      incr = -1;
      if (pos > lastEditPos)
        pos = lastEditPos;

      for (; (pos >= 0 && pos <= lastEditPos); pos += incr) {  // scan over literals
        if (pos < lastEditPos && isLiteral(pos)) {
          continue;
        }
        DiagnosticJLimo.trace(Trace.EditMaskStr, " final move took us to pos " + pos);
        state.cursorPos = pos;
        autoSkipLiteral = 0;
        return true;
      }
    }



    return false;
  }

  //
  // This method will parse the format string used for text.  It will construct a vector of ItemEditMaskRegions
  // to deal with everything it finds there.
  //
  private final void buildTextRegions() {

    //
    // Algorithm:
    // We will scan the editMask char-by-char, converting INTL symbols on the fly, pulling out directives
    // (such as '!', '<', and '>') and build an ItemEditMaskCharObj handler class for every special character
    // we find.  Important note: there is a one-to-one correspondence between the characters in the
    // StrinBuffer used for editing, emptyMask, and the editObjs[] array.
    //
    char c;
    FastStringBuffer editChars = new FastStringBuffer(editMask);  // sets currentChar offset to zero for 'for' loop
    FastStringBuffer workBuffer = new FastStringBuffer(editChars.length());    // empty but big enough never to grow
    FastStringBuffer litBuffer;
    int charCount = 0;
    int litCount = 0;
    int cnt;
    boolean password = false;
    int caseConvert = 0;
    boolean optional = false;

    DiagnosticJLimo.trace(Trace.EditMaskStr, "BuildTextRegions using the pattern \"" + editChars.toString() + "\"");

    for (c = editChars.firstChar(); c != FastStringBuffer.NOTACHAR; c = editChars.nextChar()) {
      DiagnosticJLimo.trace(Trace.EditMaskStr, "BuildTextRegion: char is '" + c + "'");
      //
      // We are here only for parsing the 1st subfield -- the editMask itself.  'c' contains the next char.
      //
      DiagnosticJLimo.trace(Trace.EditMaskStr, "BuildTextRegion fetched '" + c + "'");

      if (c == ';')  // let TextFormat deal with all the stuff after the semicolon
        break;

      switch (c)
      {
        case '{':
        case '}':
          if (litCount > 0)
            editRegions.addElement(new ItemEditMaskRegionLiteral(this, workBuffer));
          if (charCount > 0)
            editRegions.addElement(new ItemEditMaskRegionText(this, workBuffer, caseConvert, password, optional));
          optional = (c == '{') ? true : false;
          litCount = 0;
          charCount = 0;
          workBuffer.empty();
          continue;

          //
          // All directives condition variables for the next section, but they NEVER go into the
          // region as character positions
        case '^':
        case '<':
        case '>':
        case '!':
        case '*':
          if (litCount > 0) {
            editRegions.addElement(new ItemEditMaskRegionLiteral(this, workBuffer));
            litCount = 0;
          }
          if (charCount > 0) {
            editRegions.addElement(new ItemEditMaskRegionText(this, workBuffer, caseConvert, password, optional));
            charCount = 0;
          }
          workBuffer.empty();
          switch (c)
          {
            case '^':
              startCursor = charPositionsToHere(editRegions.size()) + litCount + charCount - 1;
              DiagnosticJLimo.trace(Trace.EditMaskStr, "Text Starting cursor is " + startCursor);
              DiagnosticJLimo.trace(Trace.EditMaskStr, "editRegionSize = " + editRegions.size() + ", charCount = " + charCount);
              break;
            case '<':
              caseConvert = -1;
              break;
            case '>':
              caseConvert = 1;
              break;
            case '!':
              //! May need this one day at this level --> rightToLeft = true;
              break;
            case '*':
              password = !password;
              break;
          }
          continue;

          //
          // All characters which actually represent corresponding positions for characters in
          // the edit buffer are collected together.  The ItemEditMaskRegionText region will know how
          // to deal with each separately.
          //
        case '0':
        case '9':
        case '#':
        case 'L':
        case 'l':
        case '?':
        case 'A':
        case 'a':
        case 'C':
        case 'c':
        case '&':
          if (litCount > 0) {
            editRegions.addElement(new ItemEditMaskRegionLiteral(this, workBuffer));
            litCount = 0;
            workBuffer.empty();
          }
          workBuffer.append(c);
          ++charCount;
          continue;

          //
          // Here if not one of reserved letters -- take it as a literal, which can take the form of
          // <backslash>char
          // 'text'
          // text    <-- not recommended, but allowed for backward compatibility with this mask type
        default:
          if (charCount > 0) {
            editRegions.addElement(new ItemEditMaskRegionText(this, workBuffer, caseConvert, password, optional));
            workBuffer.empty();
            charCount = 0;
          }
          switch (c)
          {
            //
            // Backslash collects one char literal and only STARTS collecting literals (in case we have a
            // string of literals back to back, we can save regions by collecting them in one).
            //
            case '\\':
              workBuffer.append(editChars.parseBackSlash());
              ++litCount;
              break;
              //
              // Single quote starts literal -- swallow everything till next single quote
              //
            case '\'':          // single quote hangs in loop till 2nd quote
              litBuffer = editChars.parseLiteral(c, true);    // allow double quote
              workBuffer.append(litBuffer.value(), 0, litBuffer.length());
              litCount += litBuffer.length();
              continue;
              //
              // Anything we don't understand becomes a literal
              //
            default:
              c = localizeChar(c, true);    // make US symbols localized
              workBuffer.append(c);
                DiagnosticJLimo.trace(Trace.EditMaskStr, "  it goes into pos " + litCount + "in the literal buffer");
              litCount++;
              continue;
          }

      }
    }

    if (litCount > 0)
      editRegions.addElement(new ItemEditMaskRegionLiteral(this, workBuffer));
    if (charCount > 0)
      editRegions.addElement(new ItemEditMaskRegionText(this, workBuffer, caseConvert, password, optional));
  }

  /**
   * Boolean regions impose NO restrictions whatsoever currently.
   * We leave it to the parsing to determine whether the user got it right
   */
  //!RC TODO: in the future, consider auto-completion
  void buildBooleanRegions() {
    BooleanFormat bf = (BooleanFormat) this.formatter.getFormatObj();

    int len1 = bf.getTrueString().length();
    int len2 = bf.getFalseString().length();
    int len3 = bf.getNullString().length();
    if (len2 > len1)
      len1 = len2;
    if (len3 > len1)
      len1 = len3;


    editRegions.addElement(new ItemEditMaskRegionAny(this, len1, true));
  }

  //
  // This method will construct am "empty" string in the ItemEditMaskState.displayString.  "Empty" means
  // all literals are filled in, and everything else is filled with the '_' character.
  //
  private final char[] buildEmptyEditCharArray() {


    int i;
    int j;
    ItemEditMaskRegion emr;
    ItemEditMaskRegionLiteral eml;
    int buflen = (noPattern ? 0 : regionMap.length);
    char [] buffer = new char[buflen];
    int fillPos = 0;

    DiagnosticJLimo.trace(Trace.EditMaskStr, "buildEmptyEditCharArray()");

    for (i = 0; i < buflen; ++i) {
      DiagnosticJLimo.trace(Trace.EditMaskStr, "i = " + i + ", Fillpos = " + fillPos + ", and buffer length is " + buffer.length);
      //emr = (ItemEditMaskRegion) editRegions.elementAt(regionMap[i]);
      emr = getRegionFromPosition(i);
      DiagnosticJLimo.trace(Trace.EditMaskStr, "  next region is " + emr.getClass().getName());
      if (emr.isLiteral(fillPos)) {
        eml = (ItemEditMaskRegionLiteral) emr;
        j = eml.capacity;
          DiagnosticJLimo.trace(Trace.EditMaskStr, "Filling " + j + " chars from literal " + eml.literal.toString());
        eml.literal.getChars(0, j, buffer, fillPos);
        fillPos += j;
      }
      else {
        for (j = 0; j < emr.capacity; ++j)
          buffer[fillPos++] = blankChar;
      }
      i = fillPos - 1;
    }

    DiagnosticJLimo.trace(Trace.EditMaskStr, "buildEmptyEditCharArray() returning: " + new String(buffer));
    return buffer;

  }

  //
  // This method will construct am "epty" string in the ItemEditMaskState.displayString.  "Empty" means
  // all literals are filled in, and everything else is filled with the '_' character.
  //
  private final void buildEmptyEditString(ItemEditMaskState state) {
    char buffer[] = buildEmptyEditCharArray();
    state.displayString.setLength(0);  // make sure string is same size as region map
    state.displayString.append(buffer);  // slap in our new empty string
  }

  //
  // This method will format a date/time into a buffer with '_' characters filling out all the
  // unfilled character positions.  This is harder than it sounds, since the JDK formatting logic
  // will not produce predictable-length subfields (i.e. asking for 'y' could produce '96' and the
  // user might have slammed fields together without separators.
  //
  private final char[] buildDateTimeEditString(java.util.Date dateValue) {
//!System.err.println("buildDataTimeEditString(" + dateValue + ")");
    ItemEditMaskRegion emr;
    int i;
    int iLen;
    FastStringBuffer fs;

    DiagnosticJLimo.trace(Trace.EditMaskStr, "buildDateTimeEditString(" + dateValue + ")");

    char[] buffer = buildEmptyEditCharArray();  // fill in '_' chars and literals

    DiagnosticJLimo.trace(Trace.EditMaskStr, "empty edit string = " + new String(buffer));

    //
    // The algorithm may seem odd (and slightly expensive), but the only reliable way we can
    // format the various components of the date time buffer is to format EACH PIECE SEPARATELY,
    // and manually inject it into our buffer.  This is how we get '_' character filling to the
    // left or the right of the value showing what is the full capacity of the field.
    //
    for (i = 0; i < regionMap.length; ++i) {
//      emr = (ItemEditMaskRegion) editRegions.elementAt(regionMap[i]);
            emr = getRegionFromPosition(i);
       DiagnosticJLimo.trace(Trace.EditMaskStr, "next region is " + emr.getClass().getName());

      if (!(emr instanceof ItemEditMaskRegionLiteral)) {
        fs = new FastStringBuffer(emr.c, emr.capacity > emr.charCount ? emr.capacity : emr.charCount);
        //if (fs.length() != emr.charCount || fs.length() == 0)
        //  Diagnostic.fail("Warning: buildDateTimeEditString partial pattern is too short: " + fs.length());

        String str = DateTimeFormat(dateValue, fs.toString());
        iLen = str.length();
        iLen = iLen < emr.capacity ? iLen : emr.capacity;
        DiagnosticJLimo.trace(Trace.EditMaskStr, "  injecting " + str);
        str.getChars(0, iLen, buffer, emr.rightToLeft ? i + emr.capacity - iLen : i);
      }
      i += (emr.capacity-1);
    }
    return buffer;
  }

  /**
   * Constructs a format mask guaranteed to make all the character positions
   * show.  This solves problems of zero suppression when there are leading
   * or trailing literals.  If there are no leading or trailing sign suffixes
   * we can hand repair it by zero padding.  However, if there are literals
   * before or after the number part, only the formatter can handle that, and
   * we ask it for not suppressing zeroes.
   */
//!/*
//!  private String buildNumericEditMask(double value) {
//!System.err.println("signPrefix at " + signPrefixOffset + " and suffix at " + signSuffixOffset);
//!    if (signSuffixOffset < 0)
//!      return trueEditMask;
//!
//!    FastStringBuffer fsb = new FastStringBuffer(trueEditMask);
//!    for (int i = 0; i < fsb.length(); ++i) {
//!      if (fsb.charAt(i) == '#')
//!        fsb.setCharAt(i, '0');
//!    }
//!    return fsb.toString();
//!  }
//!*/
  /**
   * Builds the initial string we will use to start editing an existing value
   * This method has to align the formatted characters with their appropriate
   * edit regions
   */
  private void buildNumericEditString(ItemEditMaskState state, double doubleValue) {

    //String mask = buildNumericEditMask(doubleValue);
    boolean negative = (doubleValue < 0.0);
    double absValue = (negative) ? -doubleValue : doubleValue;
//!System.err.println(" building editMask value using pattern " + trueEditMask + " for value " + absValue + ", negative = " + negative);

    String s = NumericFormat(absValue, trueEditMask);
    DiagnosticJLimo.trace(Trace.EditMaskStr, " absolute formatted as: <" + s + ">");

    // If formatted string is missing a required decimal point, append one.
    // This behavior was discovered empirically 5/22/97 with BigDecimals using pattern "#0.##"
    if (decimalAt >= 0 && s.indexOf(decimalSign) < 0) {
      s = s + String.valueOf(decimalSign);
//!System.err.println("added decimal point: " + s);
    }

    // The final editString MUST be the same length as our set of edit regions
//!System.err.println("allocating displayString of length " + regionMap.length);

    FastStringBuffer editString = new FastStringBuffer(blankChar, regionMap.length);
//!System.err.println(" absolute formatted as: <" + s + ">");
//!System.err.println(s);
//!System.err.println(trueEditMask);
//!System.err.println("0123456789012345678901234567890123456789".substring(0, trueEditMask.length()));

    // Now, working outward from the decimal point, fill in missing digits
    // If we don't have a decimal point, assume one on the right hand edge
    int origDecimalAt = decimalAt;
    if (origDecimalAt < 0) {
      origDecimalAt = regionMap.length-1;

      // Walk backwards over any literals or sign regions
      while (origDecimalAt >= 0 && !(getRegionFromPosition(origDecimalAt) instanceof ItemEditMaskRegionNumeric)) {
//!System.err.println("pos " + origDecimalAt + " is region " + getRegionFromPosition(origDecimalAt));
        --origDecimalAt;
      }
    }
    int thisDecimalAt = s.indexOf(decimalSign);
    if (thisDecimalAt < 0) {
      thisDecimalAt = s.length()-1;
      while (thisDecimalAt >= 0 && !Character.isDigit(s.charAt(thisDecimalAt)))
        --thisDecimalAt;
    }

//!System.err.println("logical decimal at " + origDecimalAt + " in regions and pos " + thisDecimalAt + " in string");

    // Walk left from decimal, copying across from string
    for (int i = thisDecimalAt, j = origDecimalAt; j >= 0; --i,--j) {
      ItemEditMaskRegion emr = getRegionFromPosition(j);
      if (emr instanceof ItemEditMaskRegionNumeric) {
//!System.err.println(" numeric at " + j + ", i is " + i);
        editString.setCharAt(j, (i >= 0 && Character.isDigit(s.charAt(i))) ? s.charAt(i) : blankChar);
      }
      else if (emr instanceof ItemEditMaskRegionSign) {
//!System.err.println(" sign at " + j + ", sign char would be " + ((ItemEditMaskRegionSign)emr).c);
        editString.setCharAt(j, (negative) ? ((ItemEditMaskRegionSign)emr).c : blankChar);
      }
      else if (emr.isLiteral(j)) {
//!System.err.println(" literal at " + j);
        editString.setCharAt(j, literalAt(j));
      }
//!System.err.println("editString[" + j + "] assigned <" + editString.charAt(j) + ">");
    }

    for (int i = thisDecimalAt+1, j = origDecimalAt+1; j < editString.length(); ++i, ++j) {
      ItemEditMaskRegion emr = getRegionFromPosition(j);
      if (emr instanceof ItemEditMaskRegionNumeric) {
//!System.err.println(" numeric at " + j);
        editString.setCharAt(j, (i < s.length() && Character.isDigit(s.charAt(i))) ? s.charAt(i) : blankChar);
      }
      else if (emr instanceof ItemEditMaskRegionSign) {
//!System.err.println(" sign at " + j + ", sign char would be " + ((ItemEditMaskRegionSign)emr).c);
        editString.setCharAt(j, (negative) ? ((ItemEditMaskRegionSign)emr).c : blankChar);
      }
      else if (emr.isLiteral(j)){
//!System.err.println(" literal at " + j);
        editString.setCharAt(j, literalAt(j));
      }
//!System.err.println("editString[" + j + "] assigned <" + editString.charAt(j) + ">");
    }

//!System.err.println("numeric edit string will be \"" + editString + "\"");
    state.displayString.append(editString.toString());


//!/*
//!    // Make sure we have the right number of decimal digits (or we will screw up navigation)
//!    if (decimalDigits > 0) {
//!      int decimalPt = s.indexOf(decimalSign);
//!      int digits = decimalDigits - (s.length() - decimalPt - 1);
//!System.err.println("need to add " + digits + " digits");
//!      for (; digits > 0; --digits)
//!        s = s + '0';
//!System.err.println("added decimal digits to make it complete: " + s);
//!      }
//!
//!    int iLen = regionMap.length - s.length();
//!
//!    if (iLen < 0) {
//!System.err.println("EditMask value is larger than mask!: " + s);
//!      s = s.substring(s.length() - regionMap.length, s.length());
//!System.err.println("shortened to: " + s);
//!    }
//!
//!    FastStringBuffer editString = new FastStringBuffer();
//!
//!    // Start the string off with either a blank or the leading sign (if negative)
//!    if (signPrefixOffset >= 0) {
//!      if (doubleValue >= 0.0)
//!        editString.append(blankChar);
//!      else {
//!        ItemEditMaskRegion emr = editRegions.elementAt(signPrefixOffset);
//!        if (emr instanceof ItemEditMaskRegionSign)
//!          editString.append(((ItemEditMaskRegionSign)emr).c);
//!      }
//!    }
//!    //  if (iLen > 0)
//!    //    Diagnostic.trace(Trace.EditMaskStr, " filling with blank");
//!
//!//    if (signSuffixOffset >= 0)
//!//      --iLen;
//!
//!if (iLen > 0) System.err.println(" appending " + iLen + " blanks");
//!
//!    while (iLen-- > 0)
//!      editString.append(blankChar);
//!
//!    editString.append(s);
//!    if (signSuffixOffset >= 0 && doubleValue >= 0.0)
//!      editString.append(blankChar);
//!    state.displayString.append(editString.toString());
//!*/
  }

  //
  // This method will fill the displayString using the given Variant.
  // It tries all known permutations to get things into the right
  // format.
  //
  private final void buildVariantEditString(ItemEditMaskState state, Variant value) {
    Integer intObj;
    Double doubleObj;
    Long longObj;
//!TODO    Numeric numObj;
    FieldPosition fs;
    String resultStr;
    StringBuffer resultStrBuf;
    int inputVariantType = value.getType();// Note: regardless of the default variant type the user has asked for
    // for getValue(), this is the INPUT phase, so we have to use what was given
    state.displayString.setLength(0);
    int iLen;

    switch (formatterType)
    {
      //
      // We have to handle all 3 kinds of edit masks.  Try to coerce any possible
      // data type to the form we need (double).
      // First we deal with the Numeric mask.
      //
      case VariantFormatter.DECIMAL:
        //! TODO <rac> shares double now
      case VariantFormatter.NUMERIC:
        double doubleValue;
        switch (inputVariantType)
        {
          case Variant.FLOAT:
          case Variant.DOUBLE:
            doubleValue = value.getAsDouble();
            break;

          case Variant.BYTE:
          case Variant.SHORT:
          case Variant.INT:
            intObj = new Integer(value.getAsInt());
            doubleValue = intObj.doubleValue();
            break;

          case Variant.LONG:
            longObj = new Long((int) value.getLong());
            doubleValue = longObj.doubleValue();
            break;

          case Variant.BIGDECIMAL:
            //! TODO <rac> There is no support yet for BCD stuff -- convert to double till then
            BigDecimal bigDecimalObj = value.getBigDecimal();
//            state.numericScale = bigDecimalObj.getScale(); //! JDK beta 3.2
            doubleValue = bigDecimalObj.doubleValue();
            break;
            //! What should we do if we cannot interpret the variant value?
            //! For now, we give them an empty string to edit
          default:
            buildEmptyEditString(state);
            return;
        }
        buildNumericEditString(state, doubleValue);
//!System.err.println("displayString will be " + state.displayString);
        break;
        //
        // Now deal with the data and time mask
        //
      case VariantFormatter.DATETIME:
        java.util.Date dateValue;
        switch (inputVariantType)
        {
          case Variant.LONG:
            dateValue = new java.util.Date(value.getLong());
            break;

          case Variant.DATE:
            //! TODO <rac> this cast okay? java.util.Date dateObj = value.getDate();
            dateValue = value.getDate();
            break;

          case Variant.TIME:
            Time timeObj = value.getTime();
            dateValue = new java.util.Date(timeObj.getTime());
            break;

          case Variant.TIMESTAMP:
            Timestamp timestampObj = value.getTimestamp();
            dateValue = new java.util.Date(timestampObj.getTime());
            break;

            //! What should we do if we cannot interpret the variant value?
            //! For now, we give them an empty string to edit
          default:
            buildEmptyEditString(state);
            return;
        }

        char[] buffer = buildDateTimeEditString(dateValue);
        state.displayString.setLength(0);
        state.displayString.append(buffer);
        break;
        //
        // Finally, deal with text masks
        //
      case VariantFormatter.TEXT:
        resultStr = (value.isNull()) ? new String("") : value.toString();
//!System.err.println("building initial edit string from " + resultStr);
        String res = TextFormat(resultStr, blankChar);
//!System.err.println("returns: \"" + res + "\"");
        state.displayString.append(res);
        while (state.displayString.length() < getBufferLength(state.displayString))
          state.displayString.append(blankChar);
//!System.err.println("displayString = " + state.displayString);
        break;

      case VariantFormatter.BOOLEAN:
        resultStr = BooleanFormat(new Boolean(value.getBoolean()));
        state.displayString.append(resultStr);

        // The boolean value may have formatted to something shorter
        // than our allowable edit string.  We must pad with blanks.
        iLen = regionMap.length - resultStr.length();
        while (iLen-- > 0)
          state.displayString.append(blankChar);

        break;
    }
//!    /* //!RC 5/8/97 This test is wrong -- strings can be longer than the number of regions!
//!    Diagnostic.trace(Trace.EditMaskStr, "BuildVariantEditString built: " + state.displayString.toString());
//!    if (state.displayString.length() != editRegions.size())
//!      Diagnostic.fail("Warning: buildVariantEditString(): edit string is " + state.displayString.length()
//!                      + " chars and region map is " + editRegions.size());
//!    */
  }

  //
  // This routine is reponsible for changing certain symbols (like the decimal point and thousands separator)
  // to/from their localized versions.  The reason for this is this: the JDK resourced patterns use US symbols
  // but substitute the localized ones in the parse and the format.  We have to model this behavior at our end
  // as well or international users will see US formatting symbols during editing.
  //
  private char localizeChar(char c, boolean toLocale) {
    if (!makeSymbolsLocal)
      return c;

    if (toLocale) {    // when mapping from US to locale
      if (c == '.')
        c = decimalSign;
      else if (c == ',')
        c = thousandsSign;
    }
    else {      // when mapping from locale back to US
      if (c == decimalSign)
        c = '.';
      else if (c == thousandsSign)
        c = ',';
    }
    return c;
  }

  //
  // Get the "final" version of the edit buffer into a FastString.  This is where things like passwords
  // show their real values -- it will not be shown to the control.
  //
  private final FastStringBuffer getFinalEditString(ItemEditMaskState state) {
    StringBuffer s = state.displayString;
    char c;
    int iLen = s.length();

    DiagnosticJLimo.trace(Trace.EditMaskStr, "getFinalEditString = \"" + s + "\" and length is " + iLen);

    FastStringBuffer fb = new FastStringBuffer(iLen);
    for (int i = 0; i < iLen; ++i) {
      c = getCharAt(s, i);  // Note: call chain is ItemEditMaskStr->ItemEditMaskRegion->EditMaskCharObj
      //
      // Note: the JDK convention is to have US decimal and thousand separator symbols in US form and
      // then to convert internally when they parse or format.  We have to model that behavior.  There
      // is compensating behavior in the construction of these in BuildNumericRegions and BuildTextRegions
      //

//!RC Bug 3637 -- don't translate the characters back -- leave them localized for parser
//      c = localizeChar(c, false);    // map localized symbols back to US

      fb.append(c);
    }
    return fb;
  }

  //
  // This method will take the display string in the current ItemEditMaskState and strip it of
  // all "unnecessary" characters -- such as unfilled blank characters, literals which
  // are stippable, etc.
  //
  private final FastStringBuffer stripBlanksAndLiterals(ItemEditMaskState state) {

    FastStringBuffer dst = getFinalEditString(state);
    char c;
    ItemEditMaskRegionLiteral emr;
    char value[] = dst.value();  // work directly in buffer for speed
    int iLen = value.length;
    int i;
    int j;
//!    //
//!    // First pass will strip out all literals by turning them into blanks.  This technique
//!    // is used so we don't have to repair the region map as characters are removed.
//!    //
//!    /*
//!    for (i = 0; i < iLen; ++i) {
//!    c = value[i];
//!    switch (formatterType)
//!    {
//!    //
//!    // Note: numerics do NOT strip literals, since the default DecimalFormat appears to do that
//!    // for us.  We do, however, strip thousands separators, since it occasionally seems to confuse
//!    // DecimalFormat
//!    //
//!    case VariantFormatter.NUMERIC:
//!    case VariantFormatter.Currency:
//!    if (c == thousandsSign)
//!    value[i] = ',';
//!    else if (c == decimalSign)
//!    value[i] = '.';
//!    break;
//!    }
//!
//!    }
//!
//!    //
//!    // Second pass will simply remove all blank characters from the buffer unconditionally
//!    //
//!*/

    for (c = dst.firstChar(); c != FastStringBuffer.NOTACHAR; c = dst.nextChar()) {
      if (c == blankChar)
        dst.removeChar();
    }
    return dst;
  }

  //
  // Returns true iff this string is logically empty (meaning there is something
  // other than blank in any nonliteral position).
  private boolean isEmptyString(ItemEditMaskState state) {
//!System.err.println("isEmptyString: length is " + state.displayString.toString().length());
    for (int pos = 0; pos <= lastEditPos; ++pos) {
      if (!isLiteral(pos) && getCharAt(state.displayString, pos) != blankChar)
        return false;
    }
    return true;

  }

  //
  // This method will "parse" state.displayString into a Variant.
  // It tries all known permutations to get things into the right
  // format. A 'true' return signals success.  A 'false" return signfies an
  // illegal value (and state.cursorPos is set to the problem).  A null string
  // will turn the Variant into an AssignedNull.  A string which will not parse
  // will return a false and WILL NOT TOUCH the Variant in any way.
  //
  private final void getVariantFromString(ItemEditMaskState state,
                                          Variant value,
                                          int variantType) throws InvalidFormatException {
    FastStringBuffer str;
    String s;

    //!System.err.println("GetVariantFromString: string is \"" + state.displayString + "\" and type wanted is " + variantType);

    DiagnosticJLimo.trace(Trace.EditMaskStr, "GetVariantFromString: string is \"" + state.displayString + "\" and type wanted is " + variantType);
    if (variantType <= Variant.NULL_TYPES)
      variantType = this.variantType;

    DiagnosticJLimo.trace(Trace.EditMaskStr, "getVariantFromString into type " + Variant.typeName(variantType));

    if (!isComplete(state))      // don't bother parsing if missing characters
      throw VariantFormatStr.makeInvalidFormatException(editMask, state.cursorPos);
    DiagnosticJLimo.trace(Trace.EditMaskStr, "done with isComplete() call");
    //
    // A null string becomes an assigned null variant -- don't even try to parse it
    //
    if (state.displayString.length() == 0 || isEmptyString(state)) {
    DiagnosticJLimo.trace(Trace.EditMaskStr, "empty buffer, assigning null");
      value.setNull(Variant.ASSIGNED_NULL);
      return;
    }

    switch (formatterType)
    {
      //
      // We have to handle all 4 kinds of edit masks.  Try to coerce any possible
      // data type to the form we need (double).
      // First we deal with the Numeric mask.
      //
      case VariantFormatter.DECIMAL:
        //! TODO <rac> shares numeric for now
      case VariantFormatter.NUMERIC:
        str = stripBlanksAndLiterals(state);
        Number resultNum = NumericParse(str.toString(), trueEditMask, state);
        if (resultNum == null)
          throw VariantFormatStr.makeInvalidFormatException(editMask, state.cursorPos);
        VariantFormatStr.doubleToVariant(resultNum.doubleValue(), value, variantType, formatter.getScale());
        break;
        //
        // Now deal with the data and time mask
        //
      case VariantFormatter.DATETIME:
        str = stripBlanksAndLiterals(state);
        boolean success = DateTimeParse(str.toString(), trueEditMask, value, variantType);

        if (!success) {
          DiagnosticJLimo.trace(Trace.EditMaskStr, "getVariantFromString throwing exception");
          throw VariantFormatStr.makeInvalidFormatException(editMask, state.cursorPos);
        }
        //!Diagnostic.trace(Trace.EditMaskStr, "getVariantFromString() calling longDateToVariant with type "
        //!              + dateResult.getClass().getName());

        //!VariantFormatStr.longDateToVariant(dateResult, value, variantType);
        break;
        //
        // Finally, deal with text masks.  Note; built into the text parsing logic is the ability to turn
        // blankChars into a character of our choice.  Let the pattern's default 'replaceChar' replace any
        // occurance of 'blankChar'
        //
      case VariantFormatter.TEXT:
        s = getFinalEditString(state).toString();
//!        String resultStr = TextParse(s, blankChar, replaceBlanksWith /*VariantFormatter.NOTACHAR*/);
        String resultStr = TextParse(s, blankChar, replaceBlanksWith);
        //!System.err.println(" parsed into: " + resultStr);
        VariantFormatStr.stringToVariant(resultStr, value, variantType, formatter.getScale());
        break;

      case VariantFormatter.BOOLEAN:
        s = getFinalEditString(state).toString();
        Boolean bool = BooleanParse(s);
        VariantFormatStr.booleanToVariant(bool, value, variantType);
        break;

      default:
        DiagnosticJLimo.trace(Trace.EditMaskStr, "getVariantFromString: unknown type " + variantType + ", assigning null");
        value.setNull(Variant.ASSIGNED_NULL);  // should never get here, but return blank variant if so
    }
    DiagnosticJLimo.trace(Trace.EditMaskStr, "returning from getVariantFromString");
  }

  //
  // This method will construct a mapping array which maps every character position in the potential
  // edit string into its appropriate ItemEditMaskRegion.  In other words editRegions.elementAt(map[i]) will
  // return the ItemEditMaskRegion object controlling the character at the i'th position (starting from 0)
  // in the edit buffer.
  //
  private void buildRegionMap() {
    // Special case worth mentioning.  Text formatting is allowed to get through without any pattern,
    // In this case, since we cannot predict the length in advance, we do make a regionMap[]
    if (noPattern)
      return;

    ItemEditMaskRegion emr;
    int i;

    DiagnosticJLimo.trace(Trace.EditMaskStr, "BuildRegionMap: there are " + editRegions.size() + " regions");

    // First step -- determine how large the edit mask will be based on the capacities computed for each
    // region.
    int capacity = 0;
    int j;
    int fillPos = 0;
    for (i = 0; i < editRegions.size(); ++i) {
      emr = (ItemEditMaskRegion) editRegions.elementAt(i);
      DiagnosticJLimo.trace(Trace.EditMaskStr, "  region " + i + "has " + emr.capacity + " slots");
      capacity += emr.capacity;
    }

    DiagnosticJLimo.trace(Trace.EditMaskStr, "will require total of " + capacity + " slots");

    regionMap = new byte[capacity];
    //
    // Now go through the map and fill in the map with the region indexes.  Note that this
    // means the edit string will have to be regionMap.length characters long.
    //
    for (i = 0; i < editRegions.size(); ++i) {
      emr = (ItemEditMaskRegion) editRegions.elementAt(i);
      DiagnosticJLimo.trace(Trace.EditMaskStr, "Region " + i + " has " + emr.capacity + " characters");
      if (emr.capacity == 0)    // it is possible for regions to become zero length (!, <, >, etc alone)
        continue;
      for (j = 0; j < emr.capacity; ++j) {
        //!      int k = i+j;
        //!      Diagnostic.trace(Trace.EditMaskStr, "map[" + k + "] maps to region " + i + " for " + emr.capacity + "chars");
        regionMap[fillPos++] = (byte) i;
      }
    }

    //
    // Now compute the last edittable position -- so we can wrap when we hit it
    //
    for (lastEditPos = regionMap.length-1; lastEditPos > 0; --lastEditPos)
      if (!isLiteral(lastEditPos))
        break;

    //! Debugging
    //!  Diagnostic.trace(Trace.EditMaskStr, "EditRegions.size() = " + editRegions.size() + " and capacity = " + capacity);
    //!  Diagnostic.trace(Trace.EditMaskStr, "The following are logical views of the region map:");
    //! for (i = 0; i < capacity; ++i) {
    //!  emr = (ItemEditMaskRegion) editRegions.elementAt(regionMap[i]);
    //!    Class cls = emr.getClass();
    //!    Diagnostic.trace(Trace.EditMaskStr, "Char " + i + " is of class " + cls.getName());
    //!}

  }

  //
  // This method will force decimal alignment.  That means it if the cursor is to the left of the decimal point,
  // it will pull down all numeric characters to just short of the decimal point and position the cursor
  // after the decimal point.  .
  // If the cursor is to the right of the decimal point, it will move all numerics between after the decimal point
  // to the other side.  In either case, we are left sitting to the right of the decimal point.
  //
  private final boolean decimalAlign(ItemEditMaskState state) {
    StringBuffer s = state.displayString;
    int iLen = s.length();
    int posSrc;
    int posDst;
    int i;
    ItemEditMaskRegion emn;
    boolean anyChanges = false;

    DiagnosticJLimo.check(noPattern == false, "cannot decimalAlign when patternless");
    if (state.cursorPos == decimalAt+1)  // if sitting after decimal point, don't bother
      return false;

    else {
      //
      // To allow a 1-pass algorithm, we will build a mapping array.  For any element in this array
      // which is nonzero, it means that character position can take the character found at in the
      // edit buffer at position changeMap[i].  In other words, the value in this map tells where
      // the source character is located.  The index into this map tells where the destination character
      // is located.
      //
      short[] changeMap = new short[iLen];
      FastStringBuffer fsb = new FastStringBuffer(s.toString());  // keep unmodified copy for moving

      for (i = 0; i < iLen; ++i)
        changeMap[i] = 0;      // note: can never have a source at pos 0

      posSrc = (state.cursorPos == lastEditPos) ? lastEditPos : state.cursorPos - 1;
      while (posSrc > 0 && isLiteral(posSrc))  // beware of having skipped over thousands separator
        --posSrc;
      posDst = decimalAt - 1;
      DiagnosticJLimo.trace(Trace.EditMaskStr, "Decimal align moving chars from pos " + posSrc + " down to pos " + posDst);

      for (; posSrc >= 0 && posDst >= 0; --posSrc, --posDst) {
        //
        // Each time through this loop, we have to step over literals to find a valid source position
        //
        if (s.charAt(posSrc) == blankChar)  // we never shift blanks, so stop the loop
          break;

        // Special case -- crossing the decimal point retards the Dst by one
        // to account for the decimal point itself not moving
        if (posSrc == decimalAt) {
          ++posDst;
          continue;
        }

        // We can't copy non-numeric regions, but be smart here.
        // If the source is non-numeric and the destination is
        // numeric, we have to retard the destination so we get
        // it on the next loop.  Ditto for dst vs src.
        boolean srcIsNumeric, dstIsNumeric;
        emn = getRegionFromPosition(posSrc);
        srcIsNumeric = (emn instanceof ItemEditMaskRegionNumeric);

        emn = getRegionFromPosition(posDst);
        dstIsNumeric = (emn instanceof ItemEditMaskRegionNumeric);

        if (!srcIsNumeric) {
          if (dstIsNumeric)
            ++posDst;
          continue;
        }

        if (!dstIsNumeric) {
          ++posSrc;
          continue;
        }

//!/*        emn = getRegionFromPosition(posSrc);
//!        if (!(emn instanceof ItemEditMaskRegionNumeric))
//!          continue;  //!break;
//!
//!        emn = getRegionFromPosition(posDst);
//!        if (!(emn instanceof ItemEditMaskRegionNumeric)) {
//!          continue;  //!break;
//!        }
//!*/
        //
        // Here's the test -- will the destination position accept the source's character?
        //
        DiagnosticJLimo.trace(Trace.EditMaskStr, "Numeric at " + posSrc + " wants to move to " + posDst);
        if (isValid(posDst, getCharAt(s, posSrc))) {
          changeMap[posDst] = (short) posSrc;
          DiagnosticJLimo.trace(Trace.EditMaskStr, "Will move \'" + getCharAt(s,posSrc) + "\' from pos " + posSrc + " to pos " + posDst);
          anyChanges = true;
        }
        else break;
      }

      //
      // Nows, we have a map showing where swappable characters lie.  We can move through this list
      // left to right to actually move the characters
      //

      if (anyChanges) {
        char c;


        for (i = 0; i < iLen; ++i) {
          // Blank out any chars within the region being moved
          if ((i >= posSrc && i < decimalAt) || (i > decimalAt && i <= posSrc)) {
            DiagnosticJLimo.trace(Trace.EditMaskStr, "pos " + i + " is between " + posSrc + " and " + decimalAt);
            emn = getRegionFromPosition(i);
            if (emn instanceof ItemEditMaskRegionNumeric) {
              DiagnosticJLimo.trace(Trace.EditMaskStr, "blanking it");
              setCharAt(state.displayString, i, blankChar);
            }
          }

          if (changeMap[i] == 0)
            continue;
          //c = getCharAt(s, changeMap[i]);        // char pos of char to be moved
          c = fsb.charAt(changeMap[i]);            //!RC 6/30/97 Changed to keep source in separate place to prevent overwrite

          setCharAt(state.displayString, i, c);
          DiagnosticJLimo.trace(Trace.EditMaskStr, "blanking pos " + changeMap[i]);
          if ((posSrc < posDst && changeMap[i] <= posDst) || (posSrc > posDst && changeMap[i] >= posSrc))
            setCharAt(state.displayString, changeMap[i], blankChar);
        }
      }
    }
    //
    // Here if finished sliding characters or were to the right of the decimal anyway
    //
    state.cursorPos = decimalAt;
//!    anyChanges |= internalMove(state, KeyEvent.VK_RIGHT /*Event.RIGHT*/);
    anyChanges |= internalMove(state, KeyEvent.VK_RIGHT);
    return anyChanges;
  }

//!/*  private final boolean decimalAlign(ItemEditMaskState state) {
//!    StringBuffer s = state.displayString;
//!    int iLen = s.length();
//!    int posSrc;
//!    int posDst;
//!    int i;
//!    ItemEditMaskRegion emn;
//!    boolean anyChanges = false;
//!
//!    Diagnostic.check(noPattern == false, "cannot decimalAlign when patternless");
//!    if (state.cursorPos == decimalAt+1)  // if sitting after decimal point, don't bother
//!      return false;
//!
//!    else {
//!      //
//!      // To allow a 1-pass algorithm, we will build a mapping array.  For any element in this array
//!      // which is nonzero, it means that character position can take the character found at in the
//!      // edit buffer at position changeMap[i].  In other words, the value in this map tells where
//!      // the source character is located.  The index into this map tells where the destination character
//!      // is located.
//!      //
//!      short[] changeMap = new short[iLen];
//!      for (i = 0; i < iLen; ++i)
//!        changeMap[i] = 0;      // note: can never have a source at pos 0
//!
//!      posSrc = (state.cursorPos == lastEditPos) ? lastEditPos : state.cursorPos - 1;
//!      while (posSrc > 0 && isLiteral(posSrc))  // beware of having skipped over thousands separator
//!        --posSrc;
//!      posDst = decimalAt - 1;
//!      Diagnostic.trace(Trace.EditMaskStr, "Decimal align moving chars from pos " + posSrc + " down to pos " + posDst);
//!      for (; posSrc >= 0 && posDst >= 0; --posSrc, --posDst) {
//!        //
//!        // Each time through this loop, we have to step over literals to find a valid source position
//!        //
//!        if (s.charAt(posSrc) == blankChar)  // we never shift blanks, so stop the loop
//!          break;
//!
//!        emn = getRegionFromPosition(posSrc);
//!        if (!(emn instanceof ItemEditMaskRegionNumeric))
//!          continue;  //!break;
//!        emn = getRegionFromPosition(posDst);
//!        if (!(emn instanceof ItemEditMaskRegionNumeric))
//!          continue;  //!break;
//!        //
//!        // Here's the test -- will the destination position accept the source's character?
//!        //
//!        Diagnostic.trace(Trace.EditMaskStr, "Numeric at " + posSrc + " wants to move to " + posDst);
//!        if (isValid(posDst, getCharAt(s, posSrc))) {
//!          changeMap[posDst] = (short) posSrc;
//!          Diagnostic.trace(Trace.EditMaskStr, "Will move \'" + getCharAt(s,posSrc) + "\' from pos " + posSrc + " to pos " + posDst);
//!          anyChanges = true;
//!        }
//!        else break;
//!      }
//!
//!      //
//!      // Nows, we have a map showing where swappable characters lie.  We can move through this list
//!      // left to right to actually move the characters
//!      //
//!
//!      if (anyChanges) {
//!        char c;
//!        for (i = 0; i < iLen; ++i) {
//!          if (changeMap[i] == 0)
//!            continue;
//!          c = getCharAt(s, changeMap[i]);        // char pos of char to be moved
//!          setCharAt(state.displayString, i, c);
//!          Diagnostic.trace(Trace.EditMaskStr, "blanking pos " + changeMap[i]);
//!          setCharAt(state.displayString, changeMap[i], blankChar);
//!        }
//!      }
//!    }
//!    //
//!    // Here if finished sliding characters or were to the right of the decimal anyway
//!    //
//!    state.cursorPos = decimalAt;
//!    anyChanges |= internalMove(state, KeyEvent.VK_RIGHT);
//!    return anyChanges;
//!  }
//!*/
}  // end of ItemEditMaskStr class

//
// The ItemEditMaskState class which is shared between a control and the
// edit mask interface allows for a "private" opaque Object to be
// stored in it.  This allows different implementations of ItemEditMasker
// to store things meaningful to them in this list.
//
class ItemEditMaskStrData {
  ItemEditMaskState state;      // so we can link back to ItemEditMaskState

  ItemEditMaskStrData(ItemEditMaskState state) {
    this.state = state;
    state.privateObject = this;     // self attaches to ItemEditMaskState
  }
}
