//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/ItemEditMaskChar.java,v 7.0 2002/08/08 18:40:12 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import com.borland.dx.text.ItemEditMaskRegion;
import com.borland.dx.text.ItemEditMask;

import java.util.*;
import java.awt.*;
import java.awt.Dimension;
import java.awt.Event;

/**
 * This interface is used internally by other com.borland classes.
 *  You should never use this interface directly.
 */
public interface ItemEditMaskChar
{
 //public boolean canChange();  // returns true if this part can be edited
 public boolean isOptional();  // returns true if the char in this position is optional
 public boolean isValid(char c);  // returns true if given char is valid
}

// Base class for all ItemEditMaskCharObj's
class ItemEditMaskCharObj implements ItemEditMaskChar
{
 protected ItemEditMaskRegion region;
 protected boolean optional;

 public ItemEditMaskCharObj(ItemEditMaskRegion region, boolean optional) {
   this.region = region;
   this.optional = optional;

//!   Class cls = this.getClass();
//!   System.out.println("New char obj " + cls.getName() + " created with optional = " + optional);
 }
 public boolean isOptional() {
   return optional;
 }
 public boolean isValid(char c) {
   return true;
 }
}

// ItemEditMaskCharLiteral: This class handles literals within the edit buffer
class ItemEditMaskCharLiteral extends ItemEditMaskCharObj
{
  public ItemEditMaskCharLiteral(ItemEditMaskRegion region) {
    super(region, false);
  }
//!  public boolean canChange() {
//!    return false;
//!  }
}


/*
Historic Note: This format is shared by Visual Basic, Access, and Delphi.  That is the reason for
               the choice.  We reserve the right to make extensions to this (supersetting the others)
         as time becomes available.

*/

// Class for '9' (Optional digits 0:9)
class ItemEditMaskCharDigit extends ItemEditMaskCharObj
{
  public ItemEditMaskCharDigit(ItemEditMaskRegion region,  boolean optional) {
    super(region, optional);
  }
  public boolean isValid(char c) {
    return Character.isDigit(c);
  }
}


// Class for '#' (Optional digits, +- allowed, blanks convert to spaces
class ItemEditMaskCharDigitSign extends ItemEditMaskCharDigit
{
  public ItemEditMaskCharDigitSign(ItemEditMaskRegion region,  boolean optional) {
    super(region, optional);                  // digits don't need convesion
  }

  public boolean isValid(char c) {
    if (super.isValid(c))
      return true;
    if (c == '+' || c == '-')
      return true;
    return Character.isWhitespace(c);
  }

}

// Class for '?' (A:Z, optional)
class ItemEditMaskCharAlpha extends ItemEditMaskCharObj
{
  public ItemEditMaskCharAlpha(ItemEditMaskRegion region,  boolean optional) {
    super(region, optional);
  }

  public boolean isValid(char c) {
//!    System.out.println("ItemEditMaskCharAlpha checking letter \'" + c + "\' shows " + Character.isLetter(c));
    return Character.isLetter(c);
  }
}


// Class for 'a' (letter or digit optional)
class ItemEditMaskCharAlphaNum extends ItemEditMaskCharObj
{
  public ItemEditMaskCharAlphaNum(ItemEditMaskRegion region,  boolean optional) {
    super(region, optional);
  }
  public boolean isValid(char c) {
    return Character.isLetterOrDigit(c);
  }
}

// Class for 'C' (char or space optional)
class ItemEditMaskCharCharOrSpace extends ItemEditMaskCharObj
{
  public ItemEditMaskCharCharOrSpace(ItemEditMaskRegion region,  boolean optional) {
    super(region, optional);
  }
  public boolean isValid() {
    return true;        //! TODO: find out what is a legal 'char'
  }
}
