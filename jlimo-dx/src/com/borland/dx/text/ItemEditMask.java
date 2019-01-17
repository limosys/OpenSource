//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/text/ItemEditMask.java,v 7.0 2002/08/08 18:40:11 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import com.borland.dx.text.ItemEditMaskStr;
import com.borland.dx.text.ItemEditMaskState;
import com.borland.dx.text.InvalidFormatException;
import com.borland.dx.dataset.Variant;

/**
* The ItemEditMask interface provides an open interface for character-by-character input
* validation.  Though this interface is not based on an ItemEditMask string in its design, there
* is an ItemEditMaskStr implementation which uses a "control string" to validate characters.
*/
public interface ItemEditMask
{

/**
* The initial method called when setting up for editing against the
* ItemEditMask interface.  It instantiates some private data which is owned
* by the edit control.
*
* @param value A Variant containing the data to be formatted into the edit
* buffer.  A 'null' value or a value.isNull() specifies that the initial edit
* string will be empty (except for embedded literals and underscore characters
* where characters should be entered). This 'value' parameter is not recorded,
* so it can fall out of scope.
*
* @return ItemEditMaskState An object allocated within the prepare() method but
* which should be owned by the control doing the editing.  It contains state
* information regarding the current edit string and cursor position.  A 'null'
* return signifies that the ItemEditMask interface should NOT be used (meaning
* there will be no character-by-character checking during editing).
*
* <P><B>Note:</B> The 'value' parameter is generally cast to the correct type
* before formatting is done, but the <B>type</B> of the variant will be used
* to deliver the final value if the variant passed to getFinalValue() has no
* assigned type information.
*/
public ItemEditMaskState prepare(Variant value);


/**
* Handle the given navigation request starting from the given cursor position.
*
*
* @param state is the ItemEditMaskState returned by prepare().  The control is
* expected to pass it back to the interface so that the edit string and
* cursor position can be determined.
*
* @param keyCode is one of the following: Event.HOME, Event.END, Event.LEFT,
* Event.RIGHT, Event.MOUSE_DOWN or MOUSE_UP.  In the case of the 2 mouse events,
* the state.cursorPos contains the desired mouse position.  This method should
* alter that position if it desires.
*
* @return A 'true' return means something changed in the cursor position and
* the control should look in state.cursorPos.  A 'false' return means nothing
* happened.
*
*/
public boolean move(ItemEditMaskState state, int keyCode);

/**
* Insert the given character at the position given by state.cursorPos.
*
* @param state is the ItemEditMaskState returned by prepare().
* @param c is the character to be inserted.  It is known not to be a navigation
* keystroke, but the implementor of this method will have to decide whether it
* is legal.
*
* @return A 'true' return means the insert succeeded and that the displayString
* in 'state' is now different.  A 'false' indicates the insert was refused (no
* error reporting or beeping is done inside this routine -- the control is
* expected to do that).
*/
public boolean insert(ItemEditMaskState state, char c);

/**
* Delete the given range of characters from the edit buffer.  This generally
* means that the characters will be replaced by the underscore character.
*
* @param state is the ItemEditMaskState returned by prepare()
* @param startPos is the starting position within the edit buffer (where 0 is
* the first character) to begin the delete.
*
* @param count Is the number of characters to delete.
*
*
* @return A "true" return means the deletion occurred and the edit string is
* now different.  A "false" means the delete could not take place.
*/
public boolean delete(ItemEditMaskState state, int startPos, int count);

/**
* Asks whether all the required fields in the edit buffer have been provided.
* This method does NOT perform validation.
*
* @param state is the ItemEditMaskState returned by prepare()
* @return A "true" return means all required fields have been filled in.  A
* "false" return means there are still some lacking.  In the "false" case,
* state.cursorPos will be set to point at the first required character which
* has been left empty.
*/
public boolean isComplete(ItemEditMaskState state);

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
public void getFinalValue(ItemEditMaskState state, Variant value) throws InvalidFormatException;


/**
* This method is used to fetch into the given variant the results
* from parsing the current edit buffer.
*
* @param state The ItemEditMaskState returned by prepare().
*
* @param value The Variant into which this method should store the value.
*
* @param variantType The data type to use to fill the 'value' Variant. This method
* will cast the normal type associated with this data into this specified type.
*
* @return A Variant containing the parsed data.  The type of the Variant will
* be set by the 'variantType' parameter.
*
* <P><B>Note:</B> This method will never return a null Variant, but it <B>will</B> throw
* an InvalidFormatException if the current edit buffer cannot be parsed.  This exception
* class contains the cursor position where the failure occurred
*
*/
public void getFinalValue(ItemEditMaskState state, Variant value, int variantType) throws InvalidFormatException;

}

