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

import java.io.*;
import java.lang.reflect.Method;
import java.text.BreakIterator;
import java.util.*;
import java.util.List;

import java.awt.*;
import java.awt.image.MemoryImageSource;
import javax.swing.*;

import com.borland.dx.dataset.DataSetAware;
import com.borland.dx.text.Alignment;
import com.borland.dx.dataset.DataSet;
import java.lang.reflect.InvocationTargetException;

/**
 * <p>A collection of utility methods for dbSwing.</p>
 */
public class DBUtilities
{
  // Holds the mnemonic character parsed by extractMnemonicText
  private static char mnemonicChar = 0;
  // Used to know when to refresh mnemonicChar cache
  private static String lastMnemonicText = null;

  // the mnemonic char prefix symbol
  private static final char ampSymbol = '&';

  /**
   * <p>Returns text without its embedded mnemonic symbol.
   * An ampersand character (&) within the text implies the character
   * following the ampersand is the mnemonic character.
   * A backslash before the ampersand causes the ampersand not to be treated
   * as the mnemonic character indicator.  Also, only the first occurence
   * of a mnemonic is removed from the <code>String</code>.</p>
   *
   * <p>This method also has the side effect of caching the mnemonic character
   * for subsequent retrieval by <code>extractMnemonicChar().</code>  When parsing
   * a string containing a mnemonic character, call this method first
   * followed by <code>extractMnemonicChar()</code> for improved efficiency.</p>
   *
   * @param text The text string that includes the mnemonic symbol.
   * @return The text string with the mnemonic symbol removed.
   * @see #extractMnemonicChar
   */
  public static String excludeMnemonicSymbol(String text) {
    if (text == null) {
      return null;
    }
    lastMnemonicText = text;
    int ampPos = 0;
    mnemonicChar = 0;
    while ((ampPos = text.indexOf(ampSymbol, ampPos)) != -1 &&
           ampPos > 0 && text.charAt(ampPos - 1) == '\\') {
      ampPos = text.indexOf(ampSymbol, ampPos + 1);
      if (ampPos == -1) {
        break;
      }
    }
    if (ampPos != -1 && ampPos < text.length() - 1) {
      mnemonicChar = text.charAt(ampPos + 1);
      if (ampPos == 0) {
        text = text.substring(1);
      }
      else {
        text = text.substring(0, ampPos) + text.substring(ampPos + 1);
      }
    }
    return text;
  }

  /**
   * <p>Returns the mnemonic character embedded in text.</p>
   *
   * @param text The text string that includes the mnemonic symbol.
   * @return The mnemonic symbol.
   * @see #excludeMnemonicSymbol
   */
  public static char extractMnemonicChar(String text) {
    if (text == null) {
      return 0;
    }
    if (text != lastMnemonicText &&
        !text.equals(lastMnemonicText)) {
      excludeMnemonicSymbol(text);
    }
    return mnemonicChar;
  }

  public static void updateCurrentDataSet(Component component, DataSet dataSet) {
    Container container = (Container) SwingUtilities.getRoot(component);
    if (container != null) {
      ArrayList toolBarList = findComponents(container, JdbNavToolBar.class);
      for (int i = 0; i < toolBarList.size(); i++) {
        JdbNavToolBar toolBar = (JdbNavToolBar) toolBarList.get(i);
        if (toolBar.isAutoDetect()) {
          toolBar.updateCurrentDataSet(dataSet);
        }
        else if (toolBar.getDataSetAwareComponents() != null) {
          for (int j = 0; j < toolBar.getDataSetAwareComponents().length; j++) {
            if (component == toolBar.getDataSetAwareComponents()[j]) {
              if (component instanceof DataSetAware) {
                toolBar.updateCurrentDataSet(((DataSetAware)component).getDataSet());
                break;
              }
            }
          }
        }
      }
      ArrayList statusLabelList = findComponents(container, JdbStatusLabel.class);
      for (int i = 0; i < statusLabelList.size(); i++) {
        JdbStatusLabel statusLabel = (JdbStatusLabel) statusLabelList.get(i);
        if (statusLabel.isAutoDetect()) {
          statusLabel.updateCurrentDataSet(dataSet);
        }
        else if (statusLabel.getDataSetAwareComponents() != null) {
          for (int j = 0; j < statusLabel.getDataSetAwareComponents().length; j++) {
            if (component == statusLabel.getDataSetAwareComponents()[j]) {
              if (component instanceof DataSetAware) {
                statusLabel.updateCurrentDataSet(((DataSetAware) component).
                    getDataSet());
                break;
              }
            }
          }
        }
      }
    }
  }

  private static ArrayList findComponents(Container container, Class clazz) {
    ArrayList list = new ArrayList();
    int componentCount = container.getComponentCount();
    for (int i = 0; i < componentCount; i++) {
      Component component = container.getComponent(i);
      if (clazz.isAssignableFrom(component.getClass())) {
        list.add(component);
      }
      else if (component instanceof Container) {
        list.addAll(findComponents((Container)component, clazz));
      }
    }
    return list;
  }

  /**
   * <p>Determines whether or not the mnemonic character prefix symbol is in the text.</p>
  * @param text The text string to be examined to see if the mnemonic character is in it.
   * @return If <code>true</code>, the mnemonic character is in the specified text.
   */
  public static boolean containsMnemonic(String text) {
    return text != null && text.indexOf(ampSymbol) != -1;
  }

  /**
   * <p>Finds <code>DataSetAware</code> components in the same root container as the
   * specified component.</p>
   *
   * @param component The specified component.
   * @return The <code>DataSetAware</code> components.
   */
  public static DataSetAware [] findDataAwareComponents(Component component) {
    return findDataAwareChildren((Container) SwingUtilities.getRoot(component));
  }

  /**
   * <p>Finds <code>DataSetAware</code> components in the same root container as the
   * specified container.</p>
   *
   * @param container The specified container.
   * @return The <code>DataSetAware</code> components.
   */
  public static DataSetAware [] findDataAwareChildren(Container container) {
    if (container != null) {
      HashSet componentCollection = new HashSet();
      getDataAwareChildComponents(container, componentCollection);
      DataSetAware[] components = new DataSetAware[componentCollection.size()];
      Iterator iterator = componentCollection.iterator();
      for (int index = 0; iterator.hasNext(); index++) {
        components[index] = (DataSetAware) iterator.next();
      }
      return components;
    }
    return new DataSetAware[0];
  }

  // recursively searches container, adding data-aware components to componentCollection.
  // assumes no DataSetAware components have child components which are data-aware
  private static void getDataAwareChildComponents(Container container, HashSet componentCollection) {
    Component [] children = container.getComponents();
    for (int childNo = 0; childNo < children.length; childNo++) {
      Component child = children[childNo];
      if (child instanceof DataSetAware) {
        componentCollection.add(child);
      }
      else if (child instanceof Container) {
        getDataAwareChildComponents((Container) child, componentCollection);
      }
    }
  }

  /**
   * <p>Maps JBCL-style alignments to Swing alignments.  JBCL-style alignments combine the
   * horizontal and vertical alignments into a single value, whereas Swing keeps
   * them separate.  Set <code>horizontal</code> to <code>true</code> to extract the equivalent SwingConstants
   * <code>horizontal</code> alignment value from <code>jbclAlignment</code>.  Set <code>horizontal</code> to  <code>false</code> to extract
   * the vertical value.</p>
   *
   * @param jbclAlignment  One of the alignment variables: <code>Alignment.LEFT, Alignment.RIGHT, Alignment.TOP</code>, or
     <code>Alignment.BOTTOM. </code>
   * @param horizontal Whether the alignment is a horizontal or vertical alignment. If horizontal is <code>true</code>, the alignment is horizontal (<code>Alignment.LEFT</code> or <code>Alignment.RIGHT</code>); if it is <code>false</code>, the alignment
     is vertical (<code>Alignment.TOP</code>, or <code>Alignment.BOTTOM</code>).
   * @return The Swing-style alignment.
   */
  public static int convertJBCLToSwingAlignment(int jbclAlignment, boolean horizontal) {
    if (horizontal) {
      switch (jbclAlignment & Alignment.HORIZONTAL) {
        case Alignment.LEFT:
          return SwingConstants.LEFT;
        case Alignment.RIGHT:
          return SwingConstants.RIGHT;
        default:
          return SwingConstants.CENTER;
      }
    }
    else {
      switch (jbclAlignment & Alignment.VERTICAL) {
        case Alignment.TOP:
          return SwingConstants.TOP;
        case Alignment.BOTTOM:
          return SwingConstants.BOTTOM;
        default:
          return SwingConstants.CENTER;
      }
    }
  }

  /**
   * <p>Returns the parent frame of the specified component. It returns <code>null</code> if a frame for the component can't be found. </p>
   *
   * @param component The <code>Component</code> you want to find the parent frame for.
   * @return The parent frame.
   */
  public static Frame getFrame(Component component) {
    if (component instanceof Frame) {
      return (Frame) component;
    }
    for (Container frame = component.getParent(); frame != null; frame = frame.getParent()) {
      if (frame instanceof Frame) {
        return (Frame) frame;
      }
    }
    return null;
  }

  /**
   * <p>Returns the parent dialog of the specified component, or <code>null</code> if one
   * can't be found.</p>
   *
   * @param component The <code>Component</code> you want to find the parent dialog for.
   * @return The parent dialog.
   */
  public static Dialog getDialog(Component component) {
    if (component instanceof Dialog) {
      return (Dialog) component;
    }
    for (Container dialog = component.getParent(); dialog != null; dialog = dialog.getParent()) {
      if (dialog instanceof Dialog) {
        return (Dialog) dialog;
      }
    }
    return null;
  }

  public static void invokeOnSwingThread(Runnable doRun) {
     if (SwingUtilities.isEventDispatchThread()) {
       doRun.run();
     }
     else {
       SwingUtilities.invokeLater(doRun);
     }
   }


   public static void invokeAndWaitOnSwingThread(Runnable doRun) throws
       InvocationTargetException, InterruptedException {
     if (SwingUtilities.isEventDispatchThread()) {
       doRun.run();
     }
     else {
       SwingUtilities.invokeAndWait(doRun);
     }

 }  /**
   * <p>Returns the specified <code>String</code> parameter as an array of <code>Strings</code>, where no array element
   * has a width greater than 60 characters. The default locale is used. </p>
   * @param text The specified <code>String</code>.
   * @return The array of <code>Strings.</code>
   */
  public static String [] getWrappedText(String text) {
    return getWrappedText(text, 60);
  }

  /**
   * <p>Returns the specified <code>String</code> parameter as an array of <code>Strings</code>, where no array element
   * has a width greater than the <code>width</code> parameter. The default locale is used. </p>
   *
   * @param text The specified <code>String</code>.
   * @param width The maximum width of each array element.
   * @return The array of <code>Strings.</code>
  */
  public static String [] getWrappedText(String text, int width) {
    return getWrappedText(text, width, Locale.getDefault());
  }

  /**
   * <p>Returns the specified <code>String</code> parameter as an array of <code>Strings</code>, where no array element
   * has a width greater than the <code>width</code> parameter. The specified locale is used. </p>
   *
   * @param text The specified <code>String</code>.
   * @param width The maximum width of each array element.
   * @param locale  The locale to use.
   * @return The array of <code>Strings.</code>
  */
  public static String [] getWrappedText(String text, int width, Locale locale) {
    Vector words = new Vector();

    try {
      // We put a try/catch here to work around an IBM JDK bug where
      // the next line sometimes causes an NPE, sometimes a StackOverflow
      BreakIterator textBoundary = BreakIterator.getWordInstance(locale);
      textBoundary.setText(text);
      int start = textBoundary.first();
      for (int end = textBoundary.next(); end != BreakIterator.DONE; start = end, end = textBoundary.next()) {
        words.addElement(text.substring(start, end));
      }
    }
    catch(Throwable t) {
      return new String [] {text};
    }

    String [] lines = new String[words.size()];
    int [] length = new int[words.size()];
    int lineCount = 0;

    int l = 0;
    int curLine = 0;
    int iWidth = 0;

    for (Enumeration enumerator = words.elements(); enumerator.hasMoreElements();) {
      String w = (String)enumerator.nextElement();
      int wl = w.length();
      int newLength = length[curLine] + wl;
      boolean isCR = (w.charAt(0) == '\r');
      boolean isLF = (w.charAt(0) == '\n');

      if (isCR)
        continue;
      if (!isLF && newLength <= width) {
        if (lines[curLine] == null) {
          lines[curLine] = w;
          length[curLine] = wl;
          l = 0;
          lineCount++;
          if (newLength > iWidth)
            iWidth = newLength;
        }
        else {
          lines[curLine] = lines[curLine]+ w;
          length[curLine] = newLength;
          if (newLength > iWidth)
            iWidth = newLength;
        }
      }
      else {
        if (isLF) {
          wl = 0;
          w  = w.substring(1);
        }
        if (wl > iWidth)
          iWidth = wl;
        if (length[curLine] == 0) {
          // First line and it's already too long! As there is no way to have it fit,
          // just stick it like that
          lines[curLine] = w;
          length[curLine] = wl;
          lineCount++;
          curLine++;
        }
        else {
          // Reject to the next line
          lineCount++;
          curLine++;
          lines[curLine] = w;
          length[curLine] = wl;
        }
        l = 0;
      }
    }
    List list = new ArrayList();
    for (int i = 0; i < lines.length; i++) {
      if (lines[i] != null) {
        list.add(lines[i]);
      }
    }
    return (String[]) list.toArray(new String[list.size()]);
  }


/**
 * <p>Returns the specified input stream as a byte array. Returns <code>null</code> on an error reading the input stream. </p>
 *
 * @param inputStream The input stream.
 * @return The byte array.
 */
  public static byte [] getByteArrayFromStream(InputStream inputStream) {
    try {
      int totalBytes = inputStream.available();
      if (totalBytes > 0) {
        byte [] bytes = new byte[totalBytes];
        int bytesRead = inputStream.read(bytes);
        if (bytesRead > 0) {
          return bytes;
        }
      }
    }
    catch (IOException e) {
      DBExceptionHandler.handleException(e);
    }
    return null;
  }

/**
 * <p>Determines if the specified <code>inputStream</code> is a GIF or JPG file.</p>
 *
 * @param inputStream The input stream.
 * @return If <code>true</code>, image is a GIF file.
*/

  public static boolean isGIForJPGFile(InputStream inputStream) {
    try {
      inputStream.mark(4);
      int c1 = inputStream.read();
      int c2 = inputStream.read();
      int c3 = inputStream.read();
      int c4 = inputStream.read();
      inputStream.reset();
      if ((c1 == 'G' && c2 == 'I' && c3 == 'F' && c4 == '8') ||  
          (c1 == 0xFF && c2 == 0xD8 && c3 == 0xFF && (c4 == 0xE0 || c4 == 0xEE))) {
        return true;
      }
    } catch (IOException e) {
    }
    return false;
  }

/**
 * <p>Determines if the specified <code>inputStream</code> is a BMP file.</p>
 * @param inputStream The input stream.
 * @return If <code>true</code>, input stream contains BMP file.
*/
  public static boolean isBMPFile(InputStream inputStream) {
    try {
      inputStream.mark(6);
      int c1 = inputStream.read();
      int c2 = inputStream.read();
      int c3 = inputStream.read();
      int c4 = inputStream.read();
      int c5 = inputStream.read();
      int c6 = inputStream.read();
      inputStream.reset();
      int size = (c3 & 0xff) | (c4 & 0xff) << 8 | (c5 & 0xff) << 16 | (c6 & 0xff) << 24;
      if (c1 == 'B' && c2 == 'M' && 
          size == inputStream.available()) {
        return true;
      }
    } catch (IOException e) {
    }
    return false;
  }

/**
 * <p>Creates a Java image object out of the specified input stream, which is expected to contain a .BMP file.</p>
 * @param inputStream The input.
 * @return The Java image object.
 */
  public static Image makeBMPImage(InputStream inputStream) {
    if (isBMPFile(inputStream)) {
      byte [] bytes = getByteArrayFromStream(inputStream);
      int bitmapHeaderSize = extractDWORD(bytes, 14);
      int pixelBase = extractDWORD(bytes, 24); // start of bitmap data
      int width;             // biWidth
      int height;            // biHeight
      int bitsPerPixel;      // biBitCount
      int compression = 0;   // biCompression
      int imageSize;         // biSizeImage
      int colorsUsed;        // biClrUsed;
      boolean os2ColorTable = false;
      boolean useColorTable = true;
      int [] pixels;

      if (bitmapHeaderSize < 40) { // BITMAPCOREHEADER
        if (bitmapHeaderSize == 12) {
          os2ColorTable = true;
        }
        width = extractWORD(bytes, 18);
        height = extractWORD(bytes, 20);
        bitsPerPixel = extractWORD(bytes, 24);
        imageSize = width * height;
        // have to assume that all colors are used
        colorsUsed = 1 << bitsPerPixel;
      }
      else {  // BITMAPINFOHEADER, BITMAPV4HEADER, or BITMAPV5HEADER
        width = extractDWORD(bytes, 18);
        height = extractDWORD(bytes, 22);
        bitsPerPixel = extractWORD(bytes, 28);
        compression = extractDWORD(bytes, 30);
        // only uncompressed bitmaps supported
        if (compression != 0) {
          return null;
        }
        imageSize = extractDWORD(bytes, 34);
        if (imageSize == 0) {
          imageSize = width * height;
        }
        colorsUsed = extractDWORD(bytes, 46);
        if (colorsUsed == 0) {
          // 1, 4, or 8 bitsPerPixel images require a color table
          if (bitsPerPixel >= 16) {
            // for >= 16-bit images, no colors used means no color table
            useColorTable = false;
          }
          colorsUsed = 1 << bitsPerPixel;
        }
      }

      // only support 1-, 4-, 8-, or 24-bit images
      if (bitsPerPixel > 8 && bitsPerPixel != 24) {
        return null;
      }

      pixels = new int[width * height];
      if (useColorTable) {
        int [] palette = new int[colorsUsed];
        int paletteBase = 14 + bitmapHeaderSize;
        int paletteOffset = paletteBase;
        int bytesPerEntry = os2ColorTable ? 3 : 4;
        for (int i = 0; i < colorsUsed; i++) {
          palette[i] = (bytes[paletteOffset] & 0xff) | // blue
            (bytes[paletteOffset + 1] & 0xff) << 8 |   // green
            (bytes[paletteOffset + 2] & 0xff) << 16 |  // red
            0xff << 24;                                // alpha

          paletteOffset += bytesPerEntry;
        }

        int pixelOffset = paletteOffset;

        if (bitsPerPixel == 1) {
          int base;
          int bitIndex = 0;
          int padding = 0;
          int byteWidth = width / 8;
          if (byteWidth * 8 != width) {
            byteWidth++;
          }
          int remainder = byteWidth % 4;
          if (remainder != 0) {
            padding = 4 - remainder;
          }
          for (int row = height - 1; row >= 0; row--) {
            base = row * width;
            for (int bit = 0; bit < width; bit++) {
              bitIndex = bit % 8;
              if (bitIndex == 0 && bit > 0) {
                pixelOffset++;
              }
              pixels[base + bit] = palette[(bytes[pixelOffset] & (0x01 << (7 - bitIndex))) == 0 ? 0 : 1];
            }
            pixelOffset++;

            pixelOffset += padding;
          }
        }
        else if (bitsPerPixel == 4) {
          int base;
          int nibbleIndex = 0;
          int padding = 0;
          int byteWidth = width / 2;
          if (byteWidth * 2 != width) {
            byteWidth++;
          }
          int remainder = byteWidth % 4;
          if (remainder != 0) {
            padding = 4 - remainder;
          }
          for (int row = height - 1; row >= 0; row--) {
            base = row * width;
            for (int nibble = 0; nibble < width; nibble++) {
              nibbleIndex = nibble % 2;
              if (nibbleIndex == 0 && nibble > 0) {
                pixelOffset++;
              }
              pixels[base + nibble] = palette[(bytes[pixelOffset] & (0xf0 >> (4 * nibbleIndex))) >> (4 * (1 - nibbleIndex))];
            }
            pixelOffset++;

            pixelOffset += padding;
          }
        }
        else if (bitsPerPixel == 8) {
          int base;
          int padding = 0;
          int remainder = width % 4;
          if (remainder != 0) {
            padding = 4 - remainder;
          }
          for (int row = height - 1; row >= 0; row--) {
            base = row * width;
            for (int column = 0; column < width; column++) {
              pixels[base + column] = palette[bytes[pixelOffset++] & 0xff];
            }
            pixelOffset += padding;
          }
        }

      }
      else {  // 24-bit
        int base;
        int padding = width % 4;
        int pixelOffset = 14 + bitmapHeaderSize;
        for (int row = height - 1; row >= 0; row--) {
          base = row * width;
          for (int column = 0; column < width; column++) {
            if (row == 0) {
            }
            pixels[base + column] = bytes[pixelOffset++] & 0xff |  // red
              (bytes[pixelOffset++] & 0xff) << 8 |               // green
              (bytes[pixelOffset++] & 0xff) << 16 |              // red
              0xff << 24;                                          // alpha
            if (row == 0) {
            }
          }
          pixelOffset += padding;
        }
      }
      return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, pixels, 0, width));
    }
    return null;
  }

  private static int extractDWORD(byte [] bytes, int offset) {
    return (bytes[offset] & 0xff) | (bytes[offset+1] & 0xff) << 8 |
      (bytes[offset+2] & 0xff) << 16 | (bytes[offset+3] & 0xff) << 24;
  }

  private static int extractWORD(byte [] bytes, int offset) {
    return (bytes[offset] & 0xff) | (bytes[offset+1] & 0xff) << 8;
  }


  // Returns the index of a year mask in a mask string, ignoring literals.
  // In other words, returns the location of the first 'y' not enclosed in single quotes,
  // or -1 if no such character exists
  /**
   * <p>Returns the index of a year mask in a mask string, ignoring literals. In other words, this method returns the location of the first 'y' not enclosed in single quotes,
or -1 if no such character exists</p>
 *
 * @param mask The year mask.
 * @return The the index of the specified year mask in a mask string,
 */
  public static int yearMaskPos(String mask) {
    boolean inLiteral = false;
    for (int pos = 0; pos < mask.length(); pos++) {
      if (mask.charAt(pos) == '\'') {
        if (pos == 0 ||
            pos > 0 && mask.charAt(pos - 1) != '\\') {
          inLiteral = !inLiteral;
        }
        continue;
      }
      if (!inLiteral && mask.charAt(pos) == 'y') { 
        return pos;
      }
    }
    return -1;
  }

  /**
   * <p>Tests if method introduced in JDK 1.3 is available.</p>
   *
   * @return If <code>true</code>, method is available.
  */
  public static boolean is1pt3() {
    return is1pt3;
  }

  /**
   * Tests to see if we are running on JDK 1.4 or higher
   * @return boolean
   */
  public static boolean islpt4() {
    return islpt4;
  }

  private static boolean is1pt3 = true;
  private static boolean islpt4 = true;

  static {
    try {
      // Test if method introduced in 1.3 is available.
      Method method = JComponent.class.getMethod("getInputVerifier", null); 
      is1pt3 = (method != null);
    } catch (NoSuchMethodException e) {
      is1pt3 = false;
    }
    try {
      // This class was introduced in 1.4
      Class.forName("java.nio.Buffer"); 
    }
    catch (ClassNotFoundException ex) {
      islpt4 = false;
    }
  }

}
