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
//------------------------------------------------------------------------------
// Copyright (c) 1996 - 2004 Borland Software Corporation.  All Rights Reserved.
//------------------------------------------------------------------------------

package com.borland.dbswing;

import java.awt.*;
import javax.swing.*;


/**
 * <p>This is a <code>JPanel</code> with either a horizontal or vertical custom layout that
 * groups a set of related buttons.  The <code>ButtonStrip</code> also takes care of some
 * platform-specific alterations: notably that the order of a horizontal group
 * of buttons is typically reversed on the Macintosh.  (Help, Cancel, OK
 * rather than OK, Cancel, Help.)</p>
 *
 *
 *
 */
public class ButtonStrip extends JPanel {
  private boolean isMacLAF = Platform.isMacLAF();
  private boolean isRightToLeft = isMacLAF;
  private static final int BUTTON_SPACING = 8;
  private static final int EDGE_SPACING = 5;
  private ButtonStripLayout layout = new ButtonStripLayout();
  private int inset;

  /**
   * <p>Creates a horizontal <code>ButtonStrip</code> with an empty 5-pixel border around it
   * that uses platform-specific rules for determining the order of button
   * layout.</p>
   *
   * <p>The convenience methods <code>createOkButton, createCancelButton,
   * createHelpButton, createYesButton, createNoButton, and createButton</code> may
   * include additional platform-specific behaviors as needed.  These should
   * be used in preference to creating common button captions and mnemonics
   * directly.</p>
   */
  public ButtonStrip() {
    this(true);
  }

  /**
   * <p>Creates a horizontal <code>ButtonStrip</code> that uses platform-specific rules for
   * determining the order of button layout.  This <code>ButtonStrip</code> may optionally
   * have a 5-pixel empty border around it.</p>
   *
   * @param useInsets <code>true</code> if the empty border is desired, <code>false</code>  otherwise.
   */
  public ButtonStrip(boolean useInsets) {
    setLayout(layout);
    inset = useInsets ? EDGE_SPACING : 0;
  }

  /**
   * <p>Creates a horizontal <code>ButtonStrip</code> that always uses the same order for
   * button layout.  This <code>ButtonStrip</code> may optionally have a 5-pixel empty
   * border around it.</p>
   *
   * @param useInsets <code>true</code> if the empty border is desired, <code>false</code>  otherwise.
   * @param rightToLeft <code>true</code> if the first button added should be the rightmost,
   * <code>false</code> otherwise.
   */
  public ButtonStrip(boolean useInsets, boolean rightToLeft) {
    setLayout(layout);
    inset = useInsets ? EDGE_SPACING : 0;
    isRightToLeft = rightToLeft;
  }

  /**
   * <p>Changes the orientation of the <code>ButtonStrip</code>.</p>
   *
   * @param orientation <code>SwingContants.HORIZONTAL</code> for horizontal layout,
   * <code>SwingConstants.VERTICAL</code> for a vertical layout.
   */
  public void setOrientation(int orientation) {
    layout.setOrientation(orientation);
  }

  /**
   * <p>Creates a generic button with the specified title.  Optionally adds the
   * button to the <code>ButtonStrip</code> that created it.</p>
   *
   * @param title The desired title for the newly created button.
   * @param addToStrip <code>true</code> if the button should be added to the <code>ButtonStrip,</code>
   * <code>false</code> otherwise.
   * @return The <code>JButton</code> objecct.
   */
  public JButton createButton(String title, boolean addToStrip) {
    return createButton(title, (char)0, addToStrip);
  }

  /**
   * <p>Creates a generic button with the specified title and mnemonic.
   * Optionally adds the button to the <code>ButtonStrip</code> that created it.</p>
   *
   * @param title The desired title for the newly created button.
   * @param mnemonic The desired mnemonic for the newly created button.
   * @param addToStrip <code>true</code> if the button should be added to the <code>ButtonStrip</code>,
   * <code>false</code> otherwise.
   * @return The <code>JButton</code> objecct.
   */
  public JButton createButton(String title, char mnemonic, boolean addToStrip) {
    JButton button = new JButton(title);
    if (mnemonic != 0)
      button.setMnemonic(mnemonic);
    if (addToStrip)
      add(button);
    return button;
  }

  /**
   * <p>Creates a help button, titled "Help" on all L&F implementations except
   * the Macintosh where the title is "?".  Optionally adds the button to the
   * <code>ButtonStrip</code> that created it.</p>
   *
   * @param addToStrip <code>true</code> if the button should be added to the <code>ButtonStrip</code>,
   * <code>false</code> otherwise.
   * @return The <code>JButton</code> objecct.
   */
  public JButton createHelpButton(boolean addToStrip) {
    if (isMacLAF) {
      JButton helpButton = createButton(Res._MacHelpButton, addToStrip);     

      // Since the minimum size reported by the button is useless, try to
      // come up with a likely minimum
      Insets insets = helpButton.getInsets();
      FontMetrics metrics = helpButton.getFontMetrics(helpButton.getFont());
      Dimension size = helpButton.getPreferredSize();
      size.width = insets.left + insets.right + 5 /* padding? */ +
          metrics.stringWidth(helpButton.getText());
      helpButton.setPreferredSize(size);
      return helpButton;
    }
    return createButton(Res._HelpBtn, addToStrip);     
  }

  /**
   * <p>Creates a "Yes" button.  Optionally adds the button to the <code>ButtonStrip</code>
   * that created it.</p>
   *
   * @param addToStrip <code>true</code> if the button should be added to the <code>ButtonStrip</code>,
   * <code>false</code> otherwise.
   * @return The <code>JButton</code> objecct.
   */
  public JButton createYesButton(boolean addToStrip) {
    return createButton(Res._YesButton,     
      Res._YesButtonMnemonic.charAt(0), addToStrip);     
  }

  /**
   * <p>Creates a "No" button.  Optionally adds the button to the <code>ButtonStrip</code>
   * that created it.</p>
   *
   * @param addToStrip <code>true</code> if the button should be added to the <code>ButtonStrip</code>,
   * <code>false</code> otherwise.
   * @return The <code>JButton</code> objecct.
   */
  public JButton createNoButton(boolean addToStrip) {
    return createButton(Res._NoButton,     
      Res._NoButtonMnemonic.charAt(0), addToStrip);     
  }

  /**
   * <p>Creates an "OK" button.  Optionally adds the button to the <code>ButtonStrip</code>
   * that created it.</p>
   *
   * @param addToStrip <code>true</code> if the button should be added to the <code>ButtonStrip</code>,
   * <code>false</code> otherwise.
   * @return The <code>JButton</code> objecct.
   */
  public JButton createOkButton(boolean addToStrip) {
    return createButton(Res._OKBtn, addToStrip);     
  }

  /**
   * <p>Creates a "Cancel" button.  Optionally adds the button to the <code>ButtonStrip</code>
   * that created it.</p>
   *
   * @param addToStrip <code>true</code> if the button should be added to the <code>ButtonStrip</code>,
   * <code>false</code> otherwise.
   * @return The <code>JButton</code> objecct.
   */
  public JButton createCancelButton(boolean addToStrip) {
    return createButton(Res._CancelBtn, addToStrip);     
  }

  //----------------------------------------------------------------------------
  //
  // Component overrides
  //
  //----------------------------------------------------------------------------

  public Insets getInsets() {
    return new Insets(inset, inset, inset, inset);
  }

  /**
   * ButtonStripLayout is a custom layout manager that acts similarly to
   * FlowLayout, but will reverse the order of component layout as needed.
   */
  private class ButtonStripLayout implements LayoutManager {
    private int orientation = SwingConstants.HORIZONTAL;

    private void setOrientation(int orientation) {
      this.orientation = orientation;
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public Dimension minimumLayoutSize(Container parent) {
      return preferredLayoutSize(parent);
    }

    public Dimension preferredLayoutSize(Container parent) {
      synchronized (parent.getTreeLock()) {
        Insets insets = parent.getInsets();
        Dimension size = new Dimension(insets.left + insets.right,
            insets.top + insets.bottom);
        int count = parent.getComponentCount();
        int maxHeight = 0;
        int maxWidth = 0;
        for (int index = 0; index < count; index++) {
          Component component = parent.getComponent(index);
          Dimension componentSize = component.getPreferredSize();
          if (orientation == SwingConstants.HORIZONTAL) {
            if (index > 0)
              size.width += BUTTON_SPACING;
            size.width += componentSize.width;
            maxHeight = Math.max(maxHeight, componentSize.height);
          }
          else {
            if (index > 0)
              size.height += BUTTON_SPACING;
            size.height += componentSize.height;
            maxWidth = Math.max(maxWidth, componentSize.width);
          }
        }
        size.height += maxHeight;
        size.width += maxWidth;
        return size;
      }
    }

    public void layoutContainer(Container parent) {
      synchronized (parent.getTreeLock()) {
	Insets insets = parent.getInsets();
	int count = parent.getComponentCount();
        Dimension parentSize = parent.getSize();

        if (orientation == SwingConstants.HORIZONTAL) {

          // Arrange components from right to left
          int y = insets.top;
          int x = parent.getWidth() - insets.right;
          for (int index = 0; index < count; index++) {
            int componentIndex = isRightToLeft ? index : count - index - 1;
            Component component = parent.getComponent(componentIndex);
            Dimension size = component.getPreferredSize();
            component.setBounds(x - size.width,
                (parentSize.height - size.height) / 2, size.width, size.height);
            x -= (size.width + BUTTON_SPACING);
          }
        }
        else {

          // Arrange components from top to bottom
          int y = insets.top;
          int width = parentSize.width - insets.left - insets.right;
          for (int index = 0; index < count; index++) {
            Component component = parent.getComponent(index);
            Dimension size = component.getPreferredSize();
            component.setBounds(insets.left, y, width, size.height);
            y += (size.height + BUTTON_SPACING);
          }
        }
      }
    }
  }
}

/**
 * All behavior that is platform specific should use a reference to this class.
 * This serves two purposes: centralizing common platform-specific tasks such
 * as detecting the presence of a particular platform, and making it possible
 * to search for every platform specific coding situation in the code base
 * by searching for "Platform."
 *
 *
 */
final class Platform {

  private static final boolean emulateMacLAF = "true".equals( 
      System.getProperty("borland.emulateMacLAF")); 

  /**
   * Detects whether or not the current look and feel is the Mac OS Adapative
   * look and feel.
   *
   * @return <code>True</code> if the Mac OS Adapative look and feel is active, <code>false</code>
   * otherwise.
   */
  public static boolean isMacLAF() {
    if (emulateMacLAF) {
      return true;
    }

    String lafName = UIManager.getLookAndFeel().getClass().getName();
    return (lafName.indexOf("MacLookAndFeel") != -1); 
  }

}
