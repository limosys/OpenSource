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
package com.borland.dbswing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class FontChooserDialog
    extends JDialog implements ActionListener, ItemListener {
  private JComboBox fontNameComboBox = new JComboBox();
  private JdbLabel fontNameLabel = new JdbLabel();
  private JComboBox fontSizeComboBox = new JComboBox();
  private JdbLabel fontSizeLabel = new JdbLabel();
  private Border border1;
  private TitledBorder titledBorder1;
  private JTextArea sampleTextArea = new JTextArea() {
    public boolean isFocusTraversable() {
      return false;
    }
  };
  private JPanel fontNamePanel = new JPanel();
  private JPanel fontSizePanel = new JPanel();
  private GridLayout gridLayout2 = new GridLayout();
  private GridLayout gridLayout3 = new GridLayout();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel mainFontPanel = new JPanel();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JPanel innerButtonPanel = new JPanel();
  private RepeatButton okButton = new RepeatButton();
  private RepeatButton cancelButton = new RepeatButton();
  private GridLayout gridLayout1 = new GridLayout();
  private JPanel outerButtonPanel = new JPanel();

  private GraphicsEnvironment graphicsEnvironment;
  private boolean okPressed = false;

  /**
   * Constructs a <code>FontChooser</code>Dialog with a specified frame, a string that appears
   * as the title of the dialog box.  The third parameter is deprecated.  The
   * dialog is always modal.
   */
  public FontChooserDialog(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
    try {
      frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      jbInit();
      pack();
      fillFontNames();
      setSelectedFont(sampleTextArea.getFont());
      setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      fontSizeComboBox.setSelectedItem(Float.toString(sampleTextArea.getFont().
          getSize2D()));
    }
    catch (Exception ex) {
      DBExceptionHandler.handleException(ex);
    }
    frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }

  /**
   * Constructs a modal <code>FontChooser</code>Dialog with no title on the title bar.
   */
  public FontChooserDialog() {
    this(null, "", true); 
  }

  void jbInit() throws Exception {
    border1 = BorderFactory.createEtchedBorder();
    titledBorder1 = new TitledBorder(border1, Res._FontSample);     
    gridLayout1.setHgap(5);
    gridLayout1.setColumns(2);
    gridLayout2.setRows(2);
    gridLayout2.setColumns(1);
    gridLayout3.setRows(2);
    gridLayout3.setColumns(1);
    innerButtonPanel.setLayout(gridLayout1);
    fontNamePanel.setLayout(gridLayout2);
    fontSizePanel.setLayout(gridLayout3);
    cancelButton.setTextWithMnemonic(Res._CancelBtn);     
    cancelButton.setRepeat(false);
    cancelButton.setDefaultButton(true);
    cancelButton.addActionListener(this);
    okButton.setTextWithMnemonic(Res._OKBtn);     
    okButton.setRepeat(false);
    okButton.addActionListener(this);
    mainFontPanel.setLayout(gridBagLayout1);
    sampleTextArea.setEditable(false);
    sampleTextArea.setRows(3);
    sampleTextArea.setHighlighter(null);
    sampleTextArea.setBorder(null);
    sampleTextArea.setText(Res._FontSampleText);     
    sampleTextArea.setBackground(UIManager.getColor("TextArea.background")); 
    sampleTextArea.setForeground(UIManager.getColor("TextArea.foreground")); 
    sampleTextArea.setLineWrap(true);
    fontNameLabel.setTextWithMnemonic(Res._FontNameLabel);     
    fontNameLabel.setDisplayedMnemonic(Res._FontNameLabelMnemonic.charAt(0));     
    fontNameLabel.setLabelFor(fontNameComboBox);
    fontNameComboBox.addItemListener(this);
    fontNameComboBox.setLightWeightPopupEnabled(false);
    fontNameComboBox.setRenderer(new FontNameRenderer());
    fontSizeLabel.setTextWithMnemonic(Res._FontSizeLabel);     
    fontSizeLabel.setDisplayedMnemonic(Res._FontSizeLabelMnemonic.charAt(0));     
    fontSizeLabel.setLabelFor(fontSizeComboBox);
    fontSizeComboBox.setModel(new DefaultComboBoxModel(new String[] {
        "8.0", "10.0", "12.0", "14.0", "16.0", "18.0", "24.0", "36.0", "48.0"})); 
    fontSizeComboBox.setEditable(true);
    fontSizeComboBox.addItemListener(this);
    jScrollPane1.setBorder(BorderFactory.createCompoundBorder(titledBorder1,
        BorderFactory.createLoweredBevelBorder()));
    jScrollPane1.getViewport().add(sampleTextArea, null);
    mainFontPanel.add(fontNamePanel, new GridBagConstraints(0, 0, 3, 1, 1.0,
        0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        new Insets(0, 5, 5, 5), 0, 0));
    fontNamePanel.add(fontNameLabel);
    fontNamePanel.add(fontNameComboBox);
    mainFontPanel.add(fontSizePanel, new GridBagConstraints(3, 0, 1, 1, 0.0,
        0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,
        5, 5, 5), 0, 0));
    fontSizePanel.add(fontSizeLabel);
    fontSizePanel.add(fontSizeComboBox);
    outerButtonPanel.add(innerButtonPanel, null);
    innerButtonPanel.add(okButton, null);
    innerButtonPanel.add(cancelButton, null);
    getContentPane().add(mainFontPanel, BorderLayout.NORTH);
    getContentPane().add(jScrollPane1, BorderLayout.CENTER);
    getContentPane().add(outerButtonPanel, BorderLayout.SOUTH);

  }

  //
  // java.awt.event.ActionListener interface implementation
  //
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == cancelButton || e.getSource() == okButton) {
      okPressed = e.getSource() == okButton;
      dispose();
    }
  }

  //
  // java.awt.event.ItemListener interface implementation
  //
  public void itemStateChanged(ItemEvent e) {
    if (e.getSource() == fontSizeComboBox) {
      if (e.getID() != ItemEvent.DESELECTED) {
        try {
          float size = Float.parseFloat(fontSizeComboBox.getSelectedItem().
              toString());
          if (size > 0.0 && size < 300.0) {
            sampleTextArea.setFont(sampleTextArea.getFont().deriveFont(size));
            sampleTextArea.repaint();
          }
          else {
            fontSizeComboBox.setSelectedIndex(0);
          }
        }
        catch (NumberFormatException ex) {
          // if the user entered something other than a number in the
          // font size combobox (could happen if it was editable),
          // simply ignore it.
          fontSizeComboBox.setSelectedIndex(0);
        }
      }
    }
    else if (e.getSource() == fontNameComboBox) {
      if (e.getID() != ItemEvent.DESELECTED) {
        Font sizelessFont = (Font) fontNameComboBox.getSelectedItem();
        // workaround for Win32 JDK bug where font styles aren't always correctly used
        String fontName = sizelessFont.getFontName().toUpperCase();
        if (fontName.indexOf(Res._BoldFont.toUpperCase()) != -1 &&     
            !sizelessFont.isBold()) {
          sizelessFont = sizelessFont.deriveFont(Font.BOLD);
        }
        if (fontName.indexOf(Res._ItalicFont.toUpperCase()) != -1 &&     
            !sizelessFont.isItalic()) {
          if (sizelessFont.isBold()) {
            sizelessFont = sizelessFont.deriveFont(Font.ITALIC | Font.BOLD);
          }
          else {
            sizelessFont = sizelessFont.deriveFont(Font.ITALIC);
          }
        }
        sampleTextArea.setFont(sizelessFont.deriveFont(Float.parseFloat(
            fontSizeComboBox.getSelectedItem().toString())));
      }
    }
    updateOkButton();
  }

  void updateOkButton() {
    boolean okState = true;
    Object selectedItem = null;
    if ( (selectedItem = fontSizeComboBox.getSelectedItem()) != null) {
      if (selectedItem instanceof String) {
        if ( ( (String) selectedItem).length() == 0) {
          okState = false;
        }
      }
    }
    if (okState != okButton.isEnabled()) {
      okButton.setEnabled(okState);
    }
    if (okState) {
      cancelButton.setDefaultButton(false);
      okButton.setDefaultButton(true);
    }
    else {
      okButton.setDefaultButton(false);
      cancelButton.setDefaultButton(true);
    }
  }

  public void setAllowAnyFontSize(boolean allowAnyFontSize) {
    fontSizeComboBox.setEditable(allowAnyFontSize);
  }

  public boolean isAllowAnyFontSize() {
    return fontSizeComboBox.isEditable();
  }

  public void setSampleText(String text) {
    sampleTextArea.setText(text);
  }

  public String getSampleText() {
    return sampleTextArea.getText();
  }

  public void setAvailableFonts(Font[] availableFonts) {
    fontNameComboBox.setModel(new DefaultComboBoxModel(availableFonts));
  }

  public Font[] getAvailableFonts() {
    Font[] items = new Font[fontNameComboBox.getItemCount()];
    for (int itemNo = 0; itemNo < items.length; itemNo++) {
      items[itemNo] = (Font) fontNameComboBox.getItemAt(itemNo);
    }
    return items;
  }

  public void setFontSizes(String[] fontSizes) {
    fontSizeComboBox.setModel(new DefaultComboBoxModel(fontSizes));
  }

  public String[] getFontSizes() {
    String[] items = new String[fontSizeComboBox.getItemCount()];
    for (int itemNo = 0; itemNo < items.length; itemNo++) {
      items[itemNo] = fontSizeComboBox.getItemAt(itemNo).toString();
    }
    return items;
  }

  private void fillFontNames() {
    if (graphicsEnvironment == null) {
      graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    }
    fontNameComboBox.setModel(new DefaultComboBoxModel(graphicsEnvironment.
        getAllFonts()));
  }

  public void setSelectedFont(Font selectedFont) {
    String fontName = selectedFont.getFontName();
    DefaultComboBoxModel model = (DefaultComboBoxModel) fontNameComboBox.
        getModel();
    Font availFont;
    for (int index = 0; index < model.getSize(); index++) {
      availFont = (Font) model.getElementAt(index);
      if (availFont.equals(selectedFont) || 
          ( (availFont.getFontName().toUpperCase().indexOf(fontName.toUpperCase()) !=
          -1) &&
          (selectedFont.isBold() == (availFont.isBold() ||
          availFont.getFontName().toUpperCase().indexOf(Res._BoldFont.     
          toUpperCase()) != -1)) &&
          (selectedFont.isItalic() == (availFont.isItalic() ||
          availFont.getFontName().toUpperCase().indexOf(Res._ItalicFont.     
          toUpperCase()) != -1)))) {
        fontNameComboBox.setSelectedIndex(index);
        break;
      }
    }
    fontSizeComboBox.setSelectedItem(Float.toString(selectedFont.getSize2D()));
    sampleTextArea.setFont(selectedFont.deriveFont(selectedFont.getSize2D()));
  }

  public Font getSelectedFont() {
    return sampleTextArea.getFont();
  }

  public boolean isOKPressed() {
    return okPressed;
  }
}
