//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/ItemEditMaskRegion.java,v 7.1 2002/10/07 21:24:02 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import com.borland.dx.text.SystemResourceBundle;
import com.borland.dx.text.ItemEditMaskStr;
import com.borland.dx.text.ItemEditMaskRegionChar;
import com.borland.dx.text.ItemEditMaskChar;
import com.borland.dx.text.ItemEditMask;
import com.borland.jb.util.Trace;
import com.borland.jb.util.FastStringBuffer;
import com.borland.jb.util.DiagnosticJLimo;
import java.lang.*;
import java.io.*;
import java.util.*;
import java.text.*;



//! /**
//! * The ItemEditMaskRegionChar interface exposes some basic questions about characters as well as getters and
//! * setters. It is implemented both at the ItemEditMaskRegion level and the ItemEditMaskStr level.
//! */
//!
//! public interface ItemEditMaskRegionChar
//! {
//! /**
//! * Will return true if and only if the character 'c' can be inserted at the specified place.
//! *
//! * @param charPosition The offset into the edit string
//! * @param c The character value to validate.
//! *
//! * @return True means the character can be stored here, false means it cannot.
//! */
//!   public boolean isValid(int charPosition, char c);
//!
//! /**
//! * Will return true only if the entire region is marked optional or if the specified
//! * character position is optional.
//! *
//! * @param charPosition The offset into the edit string
//! *
//! * @return True means either that the entire region is optional or that the specified element
//! * inside is optional.  False means the character is required before this field can post.
//! *
//! */
//!   public boolean isOptional(int charPosition);
//!
//!
//! /**
//! * Will (convert if necessary, and) store the given character into the current
//! * edit string.  This method is expected to be overridden when a region cares to alter the
//! * character before the user sees it (such as passwords, upper casing, etc.)
//! *
//! * @param charPosition The offset into the current buffer where the character should go
//! * @param c The character to be converted/stored at that position
//! *
//! * @return The converted character is returned.  This is what should be stored in the edit buffer.
//! */
//!   public char setCharAt(StringBuffer str, int charPosition, char c);
//!
//!
//! /**
//! * Will fetch the character at the specified offset in the given StringBuffer and return it.
//! * This is the opportunity for regions such as passwords to return their real char.
//! * This method is called ONLY when editing is complete and the buffer must deliver its contents to
//! * the client. It is not used to display characters.
//! *
//! * @param strbuffer The buffer to receive the character
//! * @param charPosition The offset into the current buffer where the character resides.
//! *
//! * @return The "real" at the given position character is returned.
//! */
//!   public char getCharAt(StringBuffer strBuffer, int charPosition);
//!
//!
//! /**
//! * Returns true if and only if the given region or element is a literal character (meaning
//! * it cannot be edited or changed).
//! *
//! * @param charPosition The offset into the edit buffer where the character resides.
//! *
//! *  @return True means it is a literal -- do not edit this or even place the cursor on it.
//! *
//! */
//!   public boolean isLiteral(int charPosition);
//! }


/**
 * The ItemEditMaskRegion class describes any collection of related characters within an edit mask.
 * For example, the edit mask "d/MMM/y" would have 5 ItemEditMaskRegions (day, literal, month, literal. year)
 */
/**
 * This interface is used internally by other com.borland classes.
 *  You should never use this interface directly.
 */
public class ItemEditMaskRegion implements ItemEditMaskRegionChar, Serializable
{
  private static final long serialVersionUID = 200L;

  transient ItemEditMaskStr ems;        // the ItemEditMask interface implementor itself
  boolean optional;           // are all fields in this subregion optional?
  int capacity;               // total number of characters this region can hold
  int minRequired;            // In variable length fields, the minimum required
  int offset;                 // char offset from start of edit string
  Vector charObjects;         // could be one for each char in region or one for whole region
  boolean rightToLeft;        // used to signfify which direction we fill data into region
  int charCount;              // used by Date/Time
  char c;                     // used by Date/Time

/**
* ItemEditMaskRegion is a class to control a set of adjacent characters all related in some fashion
* (like the characters in a month field).  It imposes semantics on the individual characters it owns
* over and above simple character-by-character editing (such as potential validity checking, autofill, etc.)
* Currently, this is used strictly by ItemEditMaskStr
*
* @param engine This is the controlling ItemEditMaskStr object containing all such regions.
* @param optional If this entire region is marked optional, this is set to true.  Otherwise
* each individual ItemEditMaskChar inside the region must answer for itself.
*
*/
  public ItemEditMaskRegion(ItemEditMaskStr ems, boolean optional) {
    DiagnosticJLimo.trace(Trace.FormatStr, "this = " + this + " and ems is " + ems);
    this.ems = ems;
    this.optional = optional;
    charObjects = new Vector(0);
    capacity = 0;
    rightToLeft = true;      // favors numerics
    charCount = 0;
    minRequired = 0;
    ItemEditMaskRegion emr = null;
    //
    // We always keep the offset from the start of the string where this region begins, since it
    // simplifies mapping characters to their relative ItemEditMaskCharObj's
    //
    offset = 0;
    if (ems.editRegions != null) {
      int iLen = ems.editRegions.size();
      if (iLen > 0) {
        emr = (ItemEditMaskRegion) ems.editRegions.elementAt(iLen - 1);
        if (emr != null)
          this.offset = emr.offset + emr.capacity;
      }
    }

//!    Class cls;
//!    cls = this.getClass();
//!    System.out.println("New " + cls.getName() + " is at offset " + offset);
//!    if (emr == null) {
//!      System.out.println(" because it is the first class in the vector");
//!    }
//!    else {
//!      cls = emr.getClass();
//!      System.out.println("Because the prior class " + cls.getName() + " had offset " + emr.offset + ", and capacity " + emr.capacity);
//!    }

  }    // end ItemEditMaskRegion constructor

/**
* Will return true if and only if the character 'c' can be inserted at the specified place.
* @param charPosition The offset into the edit buffer of the character to test.
* @param c The character value to validate.
*
* @return True means the character can be stored here, false means it cannot.
*/
  public boolean isValid(int charPosition, char c) {
    ItemEditMaskCharObj emo = emoFromPosition(charPosition);
//!    if (emo != null) {
//!      Class cls = emo.getClass();
//!      System.out.println("IsValid: \'" + c + "\' at pos " + charPosition + " using " + cls.getName() + " reports " + emo.isValid(c));
//!    }

    return (emo == null) ? false : emo.isValid(c);
  }

/**
* Will return true only if the entire region is marked optional or if the specified
* ItemEditMaskChar is marked optional.
*
* @param charPosition The offset into the edit buffer where the character resides.
*
* @return True means either that the entire region is optional or that the specified element
* inside is optional.  False means the character is required before this field can post.
*/

   public boolean isOptional(int charPosition) {
    boolean bResult = true;
    if (optional)  // whole region could be optional
      return bResult;
    //
    // If we can get a specific object to answer "yes" to this question, then it is so.
    //
    ItemEditMaskCharObj emo = emoFromPosition(charPosition);
//!    Class cls = this.getClass();
//!    if (emo != null)
//!     System.out.println("isOptional for " + cls.getName() + " at pos " + charPosition + " is " + emo.isOptional());
    if (emo != null && emo.isOptional())
      bResult = true;
    //
    // Now, here's an interesting situation.  Many date fields are variable in length (both numerically
    // as in 2 vs 10 as well as alphabetically as in May vs December. 'Capacity' is the longest this
    // field can be.  'MinRequired' is the shortest this field can be'.  Based on whether this field
    // aligns left or right (i.e. alpha vs digit), let us call the difference between the longest and
    // the shortest 'optional', and the shortest 'required'
    //
    else if (minRequired > 0) {
      int relPos = charPosition - offset;  // this gets position within our region
      if (relPos < 0)
  return true;
      if (relPos >= capacity)
  return true;
      if (rightToLeft)
  bResult = ((capacity - relPos) <= minRequired) ? false : true;
      else bResult = (relPos > (minRequired - 1)) ? true : false;

//!      cls = this.getClass();
//!      System.out.println("IsOptional: " + cls.getName() + " rightToLeft=" + rightToLeft + " relpos=" + relPos + " optional=" +bResult);
//!      System.out.println("  capacity=" + capacity + " minRequired=" + minRequired);
    }
    else bResult = false;

//!    System.out.println("  reporting back " + bResult);

    return bResult;
  }

/**
* This method will (convert if necessary, and) store the given character into the current
* display buffer.  This method is expected to be overridden when a region cares to alter the
* character before the user sees it..
*
* @param charPosition The offset into the current buffer where the character should go
* @param c The character to be converted/stored
*
* @return The converted character is returned.  This is what should be stored in the edit buffer.
*/

   public char setCharAt(StringBuffer str, int charPosition, char c) {
    str.setCharAt(charPosition, c);
    return c;
  }


/**
* This method will fetch the character at the specified offset in the displayString and store it into
* its equivalent offset in buffer[].  This is where regions such as passwords return their real char.
* This method is called ONLY when editing is complete and the buffer must deliver its contents to
* the client.
*
* @param str The buffer to receive the character
* @param charPosition The offset into the current buffer where the character should go
*
* @return The converted character is returned.  This is what should be stored in the edit buffer.
*/

   public char getCharAt(StringBuffer str, int charPosition) {
    char c = str.charAt(charPosition);
    return c;
  }


/**
* This method returns true if and only if the given region or element is a literal character (meaning
* it cannot be edited or changed.
*
* @param posInRegion The index of the itemEditMaskChar within this region.
* @return True means it is a literal -- do not edit this or even place the cursor on it.
*
*/
   public boolean isLiteral(int charPosition) {
//!    System.out.println("This region is not a literal");
    return false;  // Note: currently all literals live in an ItemEditMaskRegionLiteral which overrides
  }

/**
* This method is called to delete the given character at the given position.
*
* @param str The buffer to be changed
* @param charPosition The offset into the buffer of the character
* @param blankChar The preferred character to use as a blank
*/
   public void deleteCharAt(StringBuffer str, int charPosition, char blankChar) {
      setCharAt(str, charPosition, blankChar);  // use the virtual method for password regions
   }

   protected final ItemEditMaskCharObj emoFromPosition(int charPosition) {
//!    Class cls = this.getClass();
    int pos = charPosition - offset;

    if (charObjects == null) {
//!      System.out.println(cls.getName() + " at " + charPosition + " has no character objects");
      return null;
    }

    if (pos < 0) {
//!      System.out.println(cls.getName() + " at position " + charPosition + " has to small an offset of " + offset);
      return null;
   }
    if (pos >= capacity) {
//!      System.out.println(cls.getName() + " at position " + charPosition + " has too big an offset of " + offset);
      return null;
   }
    if (pos >= charObjects.size())
      pos = 0;

    if (pos >= charObjects.size()) {
//!      System.out.println(cls.getName() + " at position " + charPosition + "has no objects");
    }
    return (ItemEditMaskCharObj) charObjects.elementAt(pos);
}
//
// This private routine will cycle through the named set of resources and set 'capacity' to the
// longest and 'minRequired' to the shortest. This is used in cases like names of months where we
// have to insist on some amount of required fill in -- but only the shortest.
//
  protected final void extentsOfResource(String resourceName) {

    ResourceBundle resource = SystemResourceBundle.getLocaleElementsBundle(ems.locale);

    String[] elements = resource.getStringArray(resourceName);
    int iMax = elements.length;
    int iLen;
    capacity = 0;
    minRequired = 30000;

    for (int i = 0; i < iMax; ++i) {
//!      System.out.println("ExtentsOfResource: " + resourceName + " = \"" + elements[i] + "\"");
      iLen = elements[i].length();
      if (iLen == 0)
        continue;  // some resources have blanks
      capacity = capacity > iLen ? capacity : iLen;
      minRequired = minRequired < iLen ? minRequired : iLen;
    }
//!    Class cls = this.getClass();
//!    System.out.println("The " + cls.getName() + " class choose cap = " + capacity + ", and min = " + minRequired);
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeObject(ems instanceof Serializable ? ems : null);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Object data = s.readObject();
    if (data instanceof ItemEditMaskStr)
      ems = (ItemEditMaskStr)data;
  }

}  // end ItemEditMaskRegion base class

class ItemEditMaskRegionText extends ItemEditMaskRegion
{
  int caseConvert;
  boolean password;
  private char[] passwordStr;
//!  /*
//!   ItemEditMaskRegionText(ItemEditMaskStr ems, boolean optional) {
//!    super(ems, optional);
//!  }
//!  */

  ItemEditMaskRegionText(ItemEditMaskStr ems,
                         FastStringBuffer str,
                         int caseConvert,
                         boolean password,
                         boolean optional) {
    super(ems,false);
    this.caseConvert = caseConvert;
    this.password = password;
    passwordStr = null;
    //
    // If this is a passworded region, we will carry our own buffer for it
    //
    if (password) {
      int iLen = str.length();
      passwordStr = new char[iLen];
      for (int i = 0; i < iLen; ++i)
        passwordStr[i] = ems.blankChar;
    }

    capacity = str.length();
//!    System.out.println("TextRegion[" + ems.editRegions.size() + "] contains " + str.toString());
//!    System.out.println("  offset = "+ offset + " for " + capacity + " bytes");

    char c;
    for (c = str.firstChar(); c != FastStringBuffer.NOTACHAR; c = str.nextChar()) {
      switch (c)
      {
      //
      // Following are all the edit mask symbols.  Each occupies one space in the obj handler vector.
      // We construct the appropriate handler for it and add a blank to the empty template.
      //
        case '0':
          charObjects.addElement(new ItemEditMaskCharDigit(this, optional));
          break;

        case '9':
          charObjects.addElement(new ItemEditMaskCharDigit(this, true));
          break;

        case '#':
          charObjects.addElement(new ItemEditMaskCharDigitSign(this, true));
          break;

        case 'L':
          charObjects.addElement(new ItemEditMaskCharAlpha(this, optional));
          break;

        case 'l':
        case '?':
          charObjects.addElement(new ItemEditMaskCharAlpha(this, true));
          break;

        case 'A':
          charObjects.addElement(new ItemEditMaskCharAlphaNum(this, optional));
          break;

        case 'a':
          charObjects.addElement(new ItemEditMaskCharAlphaNum(this, true));
          break;

        // 'C' and 'c' follow the Delphi model rather than VB or Access
        case 'C':
        case '&':
          charObjects.addElement(new ItemEditMaskCharCharOrSpace(this, optional));
          break;

        case 'c':
          charObjects.addElement(new ItemEditMaskCharCharOrSpace(this, true));
          break;

        default:
          throw new IllegalArgumentException();

      }
    }
  }
  //
  // The ItemEditMaskRegionText is the only region which can take passwords.  We override the defaults
  // here to redirect the get/set into a private buffer
  //
  public char setCharAt(StringBuffer str, int charPosition, char c) {
    if (caseConvert < 0) {
      //!      System.out.println("lowercasing \'" + c + "\'");
      c = Character.toLowerCase(c);
    }
    else if (caseConvert > 0) {
      //!      System.out.println("uppercasing \'" + c + "\'");
      c = Character.toUpperCase(c);
    }
    if (password) {
      passwordStr[charPosition-offset] = c;
      c = '*';
    }
    str.setCharAt(charPosition, c);
    return c;
  }
  public char getCharAt(StringBuffer str, int charPosition) {
    char c;
    if (password)
      c = passwordStr[charPosition - offset];
    else c = str.charAt(charPosition);
    return c;
  }

  public void deleteCharAt(StringBuffer str, int charPosition, char blankChar) {
    if (password) {
      passwordStr[charPosition-offset] = blankChar;
    }
    str.setCharAt(charPosition, blankChar);
  }
}


class ItemEditMaskRegionLiteral extends ItemEditMaskRegion
{
  FastStringBuffer literal;
  ItemEditMaskRegionLiteral(ItemEditMaskStr ems, FastStringBuffer str) {
    super(ems, false);
    this.literal = new FastStringBuffer(str.value(), 0, str.length());
    capacity = literal.length();
    //!    System.out.println("LiteralRegion[" + ems.editRegions.size() + "] contains " + literal.toString());
    //!    System.out.println("  offset = "+ offset + " for " + capacity + " bytes");

  }
  public boolean isValid(int charPosition, char c) {
    return false;
  }
  public boolean isOptional(int charPosition) {
    return true;
  }
  public boolean isLiteral(int charPosition) {
    return true;
  }
}

class ItemEditMaskRegionNumeric extends ItemEditMaskRegion
{
  ItemEditMaskRegionNumeric(ItemEditMaskStr ems, FastStringBuffer str) {

    super(ems, false);
    boolean optional = false;

    char c;
    for (c = str.firstChar(); c != FastStringBuffer.NOTACHAR; c = str.nextChar()) {
      switch (c)
      {
        case '{':      // we allow {} optional sections to be handled inside this region
          optional = true;
          break;
        case '}':
          optional = false;
          break;

          // Following are all the edit mask symbols.  Each occupies one space in the obj handler vector.
          // We construct the appropriate handler for it and add a blank to the empty template.
          //
        case '0':
          charObjects.addElement(new ItemEditMaskCharDigit(this, optional));
          ++capacity;
          if (!optional)
            ++minRequired;
          break;

        case '#':
          charObjects.addElement(new ItemEditMaskCharDigit(this, true));
          ++capacity;
          break;

        default:
          throw new IllegalArgumentException();

      } // end switch
    }   // end for
  }     // end constructor
}       // end class def

//
// A couple general purpose subclasses to handle numeric and alpha entries where the character
// count directly affects the capacity
//

class ItemEditMaskRegionSign extends ItemEditMaskRegion
{
  boolean prefix;
  char c;
  char blankChar;
  ItemEditMaskRegionSign(ItemEditMaskStr ems, char c, boolean prefix, char blankChar) {
    super(ems, true);
    this.c = c;
    this.blankChar = blankChar;
    this.prefix = prefix;
    capacity = 1;
    //!    System.out.println("Just added sign region containing \'" + c + "\', prefix = " + prefix);
  }
  public boolean isValid(int charPosition, char c) {
    return (
            c == ' ' ||
            c == '+' ||
            c == '-' ||
            (c == '(' && prefix) ||
            (c == ')' && !prefix) ||
            c == this.c ||
            c == this.blankChar);
  }

  public boolean isOptional(int charPosition) {
    return true;
  }
  //
  // Here's a courtesy to help us out of parsing trouble with negative signs.  Decimal format sometimes
  // gets confused when you ask for (nnn) formatting and enter -nnn.  We solve this by mapping the
  // conventional minus sign to a '(' if that is the natural formatting character.
  //
  public char setCharAt(StringBuffer str, int charPosition, char c) {
    if (c == '-' || c == '(' || c == ')')
      c = this.c;
    else if (c == '+' || c == ' ')
      c = this.blankChar;

    str.setCharAt(charPosition, c);
    return c;
  }


}

class ItemEditMaskRegionDigit extends ItemEditMaskRegion
{
  ItemEditMaskRegionDigit(ItemEditMaskStr ems, int charCount, boolean optional) {
    super(ems, optional);
    this.charCount = charCount;
    capacity = 2 > charCount ? 2 : charCount;
    minRequired = 1;
    //!    System.out.println("DigitRegion[" + ems.editRegions.size() + "] contains " + charCount + "chars");
    charObjects.addElement(new ItemEditMaskCharDigit(this, optional));
  }
}

class ItemEditMaskRegionAlpha extends ItemEditMaskRegion
{
  ItemEditMaskRegionAlpha(ItemEditMaskStr ems, int charCount, boolean optional) {
    super(ems, optional);
    this.charCount = charCount;
    capacity = 2 > charCount ? 2 : charCount;
    charObjects.addElement(new ItemEditMaskCharAlpha(this, optional));
    rightToLeft = false;
  }
}

class ItemEditMaskRegionERA extends ItemEditMaskRegionAlpha
{
  ItemEditMaskRegionERA(ItemEditMaskStr ems, char c, boolean optional) {
    super(ems, 1, optional);
    this.c = c;
    this.charCount = 1;
    //
    // Find the localized spelling of the eras (AD/BC) and set the capacity of this field to be
    // the length of the max one.  Note there is a "; " separator between them, so factor that out.
    //
    ResourceBundle resource = SystemResourceBundle.getLocaleElementsBundle(ems.locale);

    String elements = resource.getStringArray("Eras")[0];
    capacity = elements.length();
    int i;
    for (i = 0; i < capacity; ++i)
      if (elements.charAt(i) == ';')
        break;
    if (i < capacity)
      capacity = i > capacity-2-i ? i : capacity-2-i;
    minRequired = capacity;

    charObjects.addElement(new ItemEditMaskCharAlpha(this, optional));
  }
}

class ItemEditMaskRegionYear extends ItemEditMaskRegionDigit
{
  ItemEditMaskRegionYear(ItemEditMaskStr ems, char c, int charCount, boolean optional) {
    super(ems, 2, optional);
    this.c = c;
    if (charCount <= 2) {
      capacity = 2;
      minRequired = 1;
    }
    else {
      capacity = 4;
      minRequired = 2;
    }
    this.c = c;
  }
}

class ItemEditMaskRegionMonthInYear extends ItemEditMaskRegion
{
  ItemEditMaskRegionMonthInYear(ItemEditMaskStr ems, char c, int charCount, boolean optional) {
    super(ems, optional);
    this.c = c;
    this.charCount = charCount;

    //
    // M and MM format and parse a numeric form of the month.
    // MMM formats and parses an abbreviation of the month.
    // MMMM formats and accepts only full month names
    //
    if (charCount <= 2) {
      capacity = 2;
      minRequired = 1;
      charObjects.addElement(new ItemEditMaskCharDigit(this, optional));
    }

    else {
      if (charCount == 3)
        extentsOfResource("MonthAbbreviations");
      else extentsOfResource("MonthNames");
      //!    System.out.println("MonthInYear adding alpha char obj");
      charObjects.addElement(new ItemEditMaskCharAlpha(this, optional));
      rightToLeft = false;
    }
  }
}

class ItemEditMaskRegionDayInMonth extends ItemEditMaskRegionDigit
{
  ItemEditMaskRegionDayInMonth(ItemEditMaskStr ems, char c, int charCount, boolean optional) {
    super(ems, charCount, optional);
    this.c = c;
  }
}

class ItemEditMaskRegionHour12 extends ItemEditMaskRegionDigit
{
  ItemEditMaskRegionHour12(ItemEditMaskStr ems, char c, int charCount, boolean optional) {
    super(ems, charCount, optional);
    this.c = c;
  }

}

class ItemEditMaskRegionHour23 extends ItemEditMaskRegionDigit
{
  ItemEditMaskRegionHour23(ItemEditMaskStr ems, char c, int charCount, boolean optional) {
    super(ems, charCount, optional);
    this.c = c;
  }
}

class ItemEditMaskRegionMinute extends ItemEditMaskRegionDigit
{
  ItemEditMaskRegionMinute(ItemEditMaskStr ems, char c, int charCount, boolean optional) {
    super(ems, charCount, optional);
    this.c = c;
  }
}

class ItemEditMaskRegionSecond extends ItemEditMaskRegionDigit
{
  ItemEditMaskRegionSecond(ItemEditMaskStr ems, char c, int charCount, boolean optional) {
    super(ems, charCount, optional);
    this.c = c;
  }
}

class ItemEditMaskRegionMillisecond extends ItemEditMaskRegionDigit
{
  ItemEditMaskRegionMillisecond(ItemEditMaskStr ems, char c, int charCount, boolean optional) {
    super(ems, charCount, optional);
    this.c = c;
    capacity = charCount;
  }
}

class ItemEditMaskRegionDayInWeek extends ItemEditMaskRegionAlpha
{
  ItemEditMaskRegionDayInWeek(ItemEditMaskStr ems, char c, int charCount, boolean optional) {
    super(ems, charCount, optional);
    this.c = c;
    //
    // E,EE,and EEE all produce the abbreviation of the day.
    // EEEE..E produces the full name (without padding)
    //
    if (charCount <= 3)
      extentsOfResource("DayAbbreviations");
    else extentsOfResource("DayNames");
    charObjects.addElement(new ItemEditMaskCharAlpha(this, optional));
  }

}

class ItemEditMaskRegionDayInYear extends ItemEditMaskRegionDigit
{
  ItemEditMaskRegionDayInYear(ItemEditMaskStr ems, char c, int charCount, boolean optional) {
    super(ems, charCount, optional);
    this.c = c;
  }
}

class ItemEditMaskRegionWeekInMonth extends ItemEditMaskRegionDigit
{
  ItemEditMaskRegionWeekInMonth(ItemEditMaskStr ems, char c, int charCount, boolean optional) {
    super(ems, charCount, optional);
    this.c = c;
  }

}

class ItemEditMaskRegionWeekInYear extends ItemEditMaskRegionDigit
{
  ItemEditMaskRegionWeekInYear(ItemEditMaskStr ems, char c, int charCount, boolean optional) {
    super(ems, charCount, optional);
    this.c = c;
  }

}


class ItemEditMaskRegionAMPM extends ItemEditMaskRegionAlpha
{
  ItemEditMaskRegionAMPM(ItemEditMaskStr ems, char c, boolean optional) {
    super(ems, 1, optional);
    extentsOfResource("AmPmMarkers");
    charObjects.addElement(new ItemEditMaskCharAlpha(this, optional));
    this.c = c;
  }

}

class ItemEditMaskRegionHour24 extends ItemEditMaskRegionDigit
{
  ItemEditMaskRegionHour24(ItemEditMaskStr ems, char c, int charCount, boolean optional) {
    super(ems, charCount, optional);
    this.c = c;
  }

}

class ItemEditMaskRegionHour11 extends ItemEditMaskRegionDigit
{
  ItemEditMaskRegionHour11(ItemEditMaskStr ems, char c, int charCount, boolean optional) {
    super(ems, charCount, optional);
    this.c = c;
  }

}

class ItemEditMaskRegionTimeZone extends ItemEditMaskRegionAlpha
{
  ItemEditMaskRegionTimeZone(ItemEditMaskStr ems, char c, int charCount, boolean optional) {
    super(ems, charCount, optional);
    this.c = c;
    if (charCount <= 3)
      capacity = 3;    // e.g. "PDT"
    else capacity = 30;    //! TODO: timezones aren't resourced in LocaleElements!
    minRequired = 3;
    charObjects.addElement(new ItemEditMaskCharAlpha(this, optional));
  }
}


/**
* ItemEditMaskRegionAny is a class which is patternless and imposes NO restrictions
*
* @param engine This is the controlling ItemEditMaskStr object containing all such regions.
* @param optional If this entire region is marked optional, this is set to true.  Otherwise
* each individual ItemEditMaskChar inside the region must answer for itself.
*
*/
class ItemEditMaskRegionAny extends ItemEditMaskRegion
{
  private boolean blockDelete;
  public ItemEditMaskRegionAny(ItemEditMaskStr ems){
    super(ems, true);
  }

  public ItemEditMaskRegionAny(ItemEditMaskStr ems, int charCount, boolean blockDelete) {
    this(ems);
    capacity = charCount;
    this.blockDelete = blockDelete;
  }

  public boolean isValid(int charPosition, char c) {
    return true;
  }
  public boolean isOptional(int charPosition) {
    return true;
  }
  // Patternless regions grow on demand
  public char setCharAt(StringBuffer str, int charPosition, char c) {
    DiagnosticJLimo.trace(Trace.FormatStr, "RegionAny storing " + c + " into " + str + " at pos " + charPosition);
    if (charPosition >= str.length())
      str.append(c);
    else
      str.setCharAt(charPosition, c);
    ems.setLastEditPosition(str);
    return c;
  }

  public void deleteCharAt(StringBuffer str, int charPosition, char blankChar) {
    if (blockDelete)
      super.deleteCharAt(str, charPosition, blankChar);
    else {
      FastStringBuffer fsb = new FastStringBuffer(str.toString());
      try {
        fsb.removeCharAt(charPosition);
      }
      catch (StringIndexOutOfBoundsException ex) {
      }
      synchronized (str) {
        str.setLength(0);
        str.append(fsb.toString());
      }
    }
    ems.setLastEditPosition(str);
  }

  //!  public char getCharAt(StringBuffer str, int charPosition) {}          <-- default works fine
  //!  public boolean isLiteral(int charPosition) {}                         <-- default works fine
}  // end ItemEditMaskRegionAny base class

