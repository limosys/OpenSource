//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/ItemEditMaskRegionChar.java,v 7.0 2002/08/08 18:40:12 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import com.borland.dx.text.ItemEditMaskStr;
import com.borland.dx.text.ItemEditMaskRegion;
import com.borland.dx.text.ItemEditMask;

import java.lang.*;
import java.util.*;
import java.text.*;



/**
* The ItemEditMaskRegionChar interface exposes some basic questions about characters as well as getters and
* setters. It is implemented both at the ItemEditMaskRegion level and the ItemEditMaskStr level.
*/

/**
 * This interface is used internally by other com.borland classes.
 * You should never use this interface directly.
 */
public interface ItemEditMaskRegionChar
{
/**
* Will return true if and only if the character 'c' can be inserted at the specified place.
*
* @param charPosition The offset into the edit string
* @param c The character value to validate.
*
* @return True means the character can be stored here, false means it cannot.
*/
  public boolean isValid(int charPosition, char c);

/**
* Will return true only if the entire region is marked optional or if the specified
* character position is optional.
*
* @param charPosition The offset into the edit string
*
* @return True means either that the entire region is optional or that the specified element
* inside is optional.  False means the character is required before this field can post.
*
*/
  public boolean isOptional(int charPosition);


/**
* Will (convert if necessary, and) store the given character into the current
* edit string.  This method is expected to be overridden when a region cares to alter the
* character before the user sees it (such as passwords, upper casing, etc.)
*
* @param charPosition The offset into the current buffer where the character should go
* @param c The character to be converted/stored at that position
*
* @return The converted character is returned.  This is what should be stored in the edit buffer.
*/
  public char setCharAt(StringBuffer str, int charPosition, char c);


/**
* Will fetch the character at the specified offset in the given StringBuffer and return it.
* This is the opportunity for regions such as passwords to return their real char.
* This method is called ONLY when editing is complete and the buffer must deliver its contents to
* the client. It is not used to display characters.
*
* @param strbuffer The buffer to receive the character
* @param charPosition The offset into the current buffer where the character resides.
*
* @return The "real" at the given position character is returned.
*/
  public char getCharAt(StringBuffer strBuffer, int charPosition);


/**
* Returns true if and only if the given region or element is a literal character (meaning
* it cannot be edited or changed).
*
* @param charPosition The offset into the edit buffer where the character resides.
*
*  @return True means it is a literal -- do not edit this or even place the cursor on it.
*
*/
  public boolean isLiteral(int charPosition);

/**
* This method is called to delete the given character at the given position.
*
* @param str The buffer to be changed
* @param charPosition The offset into the buffer of the character
* @param blankChar The preferred character to use as a blank
*/
  public void deleteCharAt(StringBuffer str, int charPosition, char blankChar);

}


