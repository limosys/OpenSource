/**
 * Copyright (c) 1996-2004 Borland Software Corp. All Rights Reserved.
 *
 * This SOURCE CODE FILE, which has been provided by Borland as part
 * of a Borland product for use ONLY by licensed users of the product,
 * includes CONFIDENTIAL and PROPRIETARY information of Borland.
 *
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS
 * OF THE LICENSE STATEMENT AND LIMITED WARRANTY FURNISHED WITH
 * THE PRODUCT.
 *
 * IN PARTICULAR, YOU WILL INDEMNIFY AND HOLD BORLAND, ITS RELATED
 * COMPANIES AND ITS SUPPLIERS, HARMLESS FROM AND AGAINST ANY
 * CLAIMS OR LIABILITIES ARISING OUT OF THE USE, REPRODUCTION, OR
 * DISTRIBUTION OF YOUR PROGRAMS, INCLUDING ANY CLAIMS OR LIABILITIES
 * ARISING OUT OF OR RESULTING FROM THE USE, MODIFICATION, OR
 * DISTRIBUTION OF PROGRAMS OR FILES CREATED FROM, BASED ON, AND/OR
 * DERIVED FROM THIS SOURCE CODE FILE.
 */
//--------------------------------------------------------------------------------------------------
// Copyright (c) 1996 - 2004 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
package com.borland.dbswing;

import java.awt.*;
import javax.swing.*;

/**
 * <p>Can be used to prompt a user to select a font name, style, and size. Some of the useful features of <code>FontChooser</code> include:</p>
 *<ul>
 *<li>Customizable sample font text.</li>
 *<li>A user-definable list of font names.</li>
 *<li>A user-definable list of font sizes.</li>
 *<li>The ability to specify any font size.</li>
 *<li>The ability to use local machine fonts.</li>
 *<li>The OK button is disabled until all entry fields are valid.</li>
 *<li>The default button shifts automatically between OK and Cancel buttons.</li>
 *<li>Built-in mnemonic key support.</li>
 *<li>The strings of the dialog box are resourced providing international support.</li>
 *</ul>
 *
 * <p>You can customize the initial display of the font selection dialog via the following properties:</p>
 *<ul>
 *<li><code>title</code> - Sets the title of the dialog box.</li>
 *<li><code>sampleText</code> - Sets the font text sample.</li>
 *<li><code>availableFonts</code> - Sets the list of available fonts.</li>
 *<li><code>selectedFont</code> - Sets the selected font name, size, and style of the dialog box.</li>
 *<li><code>availableFontSizes</code> - Sets the list of available font sizes.</li>
 *<li><code>allowAnyFontSize</code> - Allows the user to enter any font size.</li>
 *<li><code>modal</code> - Sets whether the dialog box is modal or not.</li>
 *</ul>
 *
 * <p>To display the <code>FontChooser</code>, set the <code>frame</code> property to a parent <code>Frame</code> for the dialog box and then call the <code>showDialog()</code> method. </p>
 *
 * <p>When the dialog box appears, the user works with the dialog box to select a font and sets its attributes.
If the user makes a font selection in the dialog box and chooses the OK button, <code>showDialog()</code> saves
the user's selection as the <code>selectedFont</code> property. Your application can then inspect the
<code>selectedFont</code> property value to determine the user's selection. The <code>showDialog()</code> method returns <code>true</code> if the user selects a font and chooses the OK button. </p>
 *
 * <p><code>FontChooser</code> will dispose itself automatically if the user
 * closes the window or presses either the OK or Cancel button.</p>
 */
public class FontChooser implements java.io.Serializable
{
  /**
   * <p>Constructs a modal <code>FontChooser</code> without a parent frame and title. Calls the constructor of this class that takes a <code>Frame</code>, a <code>String</code>, and a boolean as parameters. Passes default values of <code>null</code>, an empty string (""), and <code>true</code> to the other constructor. </p>
   */
  public FontChooser() {
    this(null, "", true);  
  }

  /**
   * <p>Constructs a <code>FontChooser</code> with a specified parent frame and a specified text string on the title bar. Calls the constructor of this class that takes a <code>Frame</code>, a <code>String</code>, and a boolean as parameters. Passes the specified frame and title, along with a boolean value of <code>true</code> to the other constructor.  </p>
  * @param frame  The <code>Frame</code> for the dialog box.
  * @param title A text string that appears on the title bar.
   */
  public FontChooser(Frame frame, String title) {
    this(frame, title, true);
  }

  /**
   * <p>Constructs a <code>FontChooser</code> with a specified parent frame and a specified text string on the title bar. Calls the constructor of this class that takes a <code>Frame</code>, a <code>String</code>, and a boolean as parameters. Passes the specified frame and title, along with a boolean value of <code>true</code> to the other constructor. </p>
   *
   * @param frame  The <code>Frame</code> for the dialog box.
   * @param title A text string that appears on the title bar.
   * @param modal If <code>true</code>, the dialog is modal.
   */
  public FontChooser(Frame frame, String title, boolean modal) {
    setFrame(frame);
    setTitle(title);
//    setModal(modal);
  }

  /**
   * <p>Sets the <code>Frame</code> used for the dialog box. The <code>frame
   * property</code> must be set to a parent <code>Frame</code> before the dialog box is
   * displayed.</p>
   *
   * @param frame The <code>Frame</code> used for the dialog box.
   * @see #getFrame
   */
  public void setFrame(Frame frame) {
    this.frame = frame;
  }

  /**
   * <p>Sets the <code>Frame</code> used for the dialog box.
   *
   * @return The <code>Frame</code> used for the dialog box.
   * @see #setFrame

   */
  public Frame getFrame() {
    return frame;
  }

  /**
   * <p>Sets the string that appears as the title of the dialog box on the title bar.</p>
   *
   * @param title The string that appears as the title of the dialog box.
   * @see #getTitle
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * <p>Returns the string that appears as the title of the dialog box on the title bar.</p>
   *
   * @return The string that appears as the title of the dialog box.
   * @see #setTitle
   */
  public String getTitle() {
    return title;
  }

  /**
   * <p>Sets whether the dialog box is modal.</p>
   *
   * @param modal If <code>true</code>, the dialog box is modal.
   * @see #isModal
   * @deprecated Has no effect, FontChooser is always modal
   */
  public void setModal(boolean modal) {
    this.modal = true;
  }

  /**
   * <p>Returns whether the dialog box is modal.</p>
   *
   * @return If <code>true</code>, the dialog box is modal.
   * @see #setModal
   */
  public boolean isModal() {
    return modal;
  }

  /**
   * <p>Sets string that appears as the sample text string in
   * the dialog box allowing the user to see how choosing a font, font
   * size, and style options affect the look of text.
If this property is not set, a
default sample string appears in the dialog box that includes letters, numbers, and punctuation symbols. </p>
   *
   * @param sampleText The sample text string.
   * @see #getSampleText
   */
  public void setSampleText(String sampleText) {
    this.sampleText = sampleText;
  }

  /**
   * <p>Returns the string that appears as the sample text string in
   * the dialog box.</p>
   *
   * @return The sample text string.
   * @see #setSampleText
   */
  public String getSampleText() {
    return sampleText;
  }

  /**
   * <p>Sets the list of font names displayed in the dialog box.</p>
   *
   * @param availableFonts The array of font names.
   * @see #getAvailableFonts
   */
  public void setAvailableFonts(Font [] availableFonts) {
    this.availableFonts = availableFonts;
  }

  /**
   * <p>Returns the list of font names displayed in the dialog box.</p>
   *
   * @return The array of font names.
   * @see #setAvailableFonts
   */
  public Font [] getAvailableFonts() {
    return availableFonts;
  }

  /**
   * <p>Sets the selected font in the dialog box.</p>
   *
   * @param selectedFont The selected font.
   * @see #getSelectedFont
   */

  public void setSelectedFont(Font selectedFont) {
    this.selectedFont = selectedFont;
  }

  /**
   * <p>Returns the name of the font the user selected in the dialog box.</p>
   *
   * @return The selected font.
   * @see #setSelectedFont
   */
  public Font getSelectedFont() {
    return selectedFont;
  }

  /**
   * <p>Sets the list of font sizes displayed in the dialog box.</p>
   *
   * @param availableFontSizes The font sizes for the selected font.
   * @see #getAvailableFontSizes
   */
  public void setAvailableFontSizes(String [] availableFontSizes) {
    this.availableFontSizes = availableFontSizes;
  }

  /**
   * <p>Returns the list of font sizes displayed in the dialog box.</p>
   *
   * @return The font sizes for the selected font.
   * @see #setAvailableFontSizes
   */
  public String [] getAvailableFontSizes() {
    return availableFontSizes;
  }

  /**
   * <p>Determines whether the user can enter any font size in the dialog
   * box. If <code>true</code>, the Font Size combo box
is editable, allowing the user to enter any font size. Otherwise, only sizes in the drop-down list can be
selected. The default value is <code>true</code>. </p>
   *
   * @param allowAnyFontSize If <code>true</code>, the Font Size combo box is editable, allowing the user to enter any font size.
   * @see #isAllowAnyFontSize
   */
  public void setAllowAnyFontSize(boolean allowAnyFontSize) {
    this.allowAnyFontSize = allowAnyFontSize;
  }
  /**
   * <p>Returns whether the user can enter any font size in the dialog
   * box.</p>
   *
   * @return If <code>true</code>, the Font Size combo box is editable, allowing the user to enter any font size.
   * @see #setAllowAnyFontSize
   */

  public boolean isAllowAnyFontSize() {
    return allowAnyFontSize;
  }

  /**
   * <p>Calls the <code>showDialog()</code> method.  To determine if the user cancelled the dialog box, call <code>showDialog()</code>
   * instead of this method.</p>
   *
   * @see #showDialog
   */
  public void show() {
    showDialog();
  }

  /**
   * <p>Displays the font selection dialog box and returns <code>true</code> if the
   * user selected a font and chose OK. If the <code>frame</code> property is <code>null</code>, an
   * <code>IllegalStateException</code> is thrown.</p>
   *
   * @return If <code>true</code>, the OK button was pressed.
   */
  public boolean showDialog() {
    if (frame == null) {
      throw new IllegalStateException(Res._DlgNoFrame);     
    }

    FontChooserDialog dialog = new FontChooserDialog(frame, title == null ? Res._FontChooserTitle : title, modal);     
    dialog.setLocationRelativeTo(frame);

    dialog.setAllowAnyFontSize(allowAnyFontSize);

    if (sampleText != null) {
      dialog.setSampleText(sampleText);
    }

    if (availableFonts != null) {
      dialog.setAvailableFonts(availableFonts);
    }

    if (availableFontSizes != null) {
      dialog.setFontSizes(availableFontSizes);
    }

    // should be after availableFonts and fontSizes have been set, since it will try to locate the
    // selected font and size in the list of available fonts/sizes
    if (selectedFont != null) {
      dialog.setSelectedFont(selectedFont);
    }

    dialog.pack();

    dialog.show();

    if (dialog.isOKPressed()) {
      selectedFont = dialog.getSelectedFont();
      okPressed = true;
    }

    dialog.dispose();

    return okPressed;
  }

  /** dialog's parent frame */
  private Frame frame;

  /** dialog's title */
  private String title;

  /** dialog modality */
  private boolean modal = true;

  /** sample text to display in dialog */
  private String sampleText;

  /** whether to allow any size font */
  private boolean allowAnyFontSize = true;

  /** user-defined list of font names from which to choose */
  private Font [] availableFonts;

  /** selected font name */
  private Font selectedFont;

  /** user-defined list of font sizes from which to choose */
  private String [] availableFontSizes;

  /** whether the OK button was pressed to close the dialog */
  private boolean okPressed = false;

}


/**
 * <code>FontChooser</code>Dialog's fontNameComboBox renderer.  Given a font object to renderer,
 * renders its name in that font.
 */
class FontNameRenderer extends JLabel implements ListCellRenderer {
  public FontNameRenderer() {
    setOpaque(true);
  }

  public Component getListCellRendererComponent(JList list,
                                                Object value,
                                                int index,
                                                boolean isSelected,
                                                boolean cellHasFocus) {

    if (value instanceof Font) {
      setText(((Font) value).getFontName());
    }
    else {
      setText(value.toString());
    }

    if(isSelected) {
      setBackground(list.getSelectionBackground());
      setForeground(list.getSelectionForeground());
    } else {
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }

    return this;
  }
}
