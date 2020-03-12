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

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Event;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.plaf.UIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.TextAction;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import com.borland.dx.dataset.AccessEvent;
import com.borland.dx.dataset.AccessListener;
import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.ColumnAware;
import com.borland.dx.dataset.ColumnPaintListener;
import com.borland.dx.dataset.CustomPaintSite;
import com.borland.dx.dataset.CustomPaintSiteLabel;
import com.borland.dx.dataset.DataChangeEvent;
import com.borland.dx.dataset.DataChangeListener;
import com.borland.dx.dataset.DataRow;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.Designable;
import com.borland.dx.dataset.Locate;
import com.borland.dx.dataset.NavigationEvent;
import com.borland.dx.dataset.NavigationListener;
import com.borland.dx.dataset.StorageDataSet;
import com.borland.dx.dataset.ValidationException;
import com.borland.dx.dataset.Variant;
import com.borland.dx.text.InvalidFormatException;
import com.borland.dx.text.ItemEditMask;
import com.borland.dx.text.ItemEditMaskState;

/**
 * <p>
 * Maps data from a Swing text component to a <code>DataSet</code> <code>Column</code> value. <code>DBTextDataBinder</code> is used internally to make the
 * <code>JdbTextField</code>, <code>JdbTextArea</code>, <code>JdbTextPane</code>, and <code>JdbEditorPane</code> components data-aware.
 * </p>
 *
 * <p>
 * There are two ways to hook up a Swing text component to a <code>DBTextDataBinder</code>:
 * </p>
 *
 * <ul>
 * <li>
 * <p>
 * Set the <code>JTextComponent</code> property to any text component extending the <code>JTextComponent</code> class, such as <code>JTextField</code>,
 * <code>JTextArea</code>, <code>JTextPane</code>, or <code>JEditorPane</code>.
 * </p>
 * <li>
 * <p>
 * Set <code>DBTextDataBinder's</code> <code>document</code> property to the text component's model. Any component using a <code>Document</code> class as its
 * model can be made data-aware. Note that when using this approach you are responsible for opening the <code>DataSet</code> before using it.
 * </p>
 * </ul>
 *
 * <p>
 * In either case, you must set <code>DBTextDataBinder's</code> <code>dataSet</code> and <code>columnName</code> properties to indicate the <code>DataSet</code>
 * and <code>Column</code> from which the text value is to be read and to which it is to be written. <code>DBTextDataBinder</code> ensures that the state of the
 * text component is consistent with the current value of the <code>DataSet</code> <code>Column</code> to which it is attached.
 * </p>
 *
 * <p>
 * If you set the <code>JTextComponent</code> property, <code>DBTextDataBinder</code> also binds the <code>background</code>, <code>foreground</code>, and
 * <code>font</code> properties from those defined on <code>Column</code> <code>columnName</code> (if defined), unless already explicitly set on the
 * <code>JTextComponent</code> itself.
 * </p>
 *
 * <p>
 * Data typed into a component made data-aware by <code>DBTextDataBinder</code> is not saved immediately to the <code>DataSet</code>. Rather, certain
 * conditions/events automatically cause the data to be put into the <code>DataSet's</code> column. These two properties affect this behavior:
 * </p>
 *
 * <ul>
 * <li><code>postOnFocusLost</code> - If <code>true</code>, data is saved to the <code>DataSet</code> whenever the component bound by (or made data-aware by)
 * <code>DBTextDataBinder</code> loses focus.</li>
 * <li><code>postOnRowPosted</code> - If <code>true</code>, data is saved to the <code>DataSet</code> whenever the current <code>DataSet</code> row is
 * posted.</li>
 * </ul>
 *
 * <p>
 * <code>DBTextDataBinder</code> maps a small number of keys to special editing/<code>DataSet</code> navigation functions for each bound text component. See the
 * notes in <a href="JdbTextField.html"><code>JdbTextField</code></a>, <a href="JdbTextArea.html"><code>JdbTextArea</code></a>,
 * <a href="JdbTextPane.html"><code>JdbTextPane</code></a>, and <a href="JdbEditorPane.html"><code>JdbEditorPane</code></a> for more details.
 * </p>
 *
 * <p>
 * <code>DBTextDataBinder</code> provides each bound component a popup menu for performing simple editing tasks, such as cutting, copying, or pasting clipboard
 * data. The popup menu also appears when the user presses <KBD>Shift+F10</KBD> while the cursor is in the bound component. The menu commands on the popup menu
 * vary according to the capabilities of the bound text component. The set of available menu commands can be customized via the following properties:
 * </p>
 *
 * <ul>
 * <li><code>enablePopupMenu</code> - Sets whether or not the popup menu is displayable.</li>
 * <li><code>enableClearAll</code> - Sets whether the Clear All popup menu command appears.</li>
 * <li><code>enableUndoRedo</code> - Sets whether the Undo and Redo menu commands appear on the popup menu.</li>
 * <li><code>enableFileLoading</code> - Sets whether the File Open menu command appears on the popup menu.</li>
 * <li><code>enableFileSaving</code> - Sets whether the File Save menu command appears on the popup menu.</li>
 * <li><code>enableColorChange</code> - Sets whether the foreground and background color setting menu commands appear on the popup menu.</li>
 * <li><code>enableFontChange</code> - Sets whether the font style setting menu commands appear on the popup menu.</li>
 * <li><code>enableURLLoading</code> - Sets whether the Open URL menu command appears on the popup menu (<code>JEditorPane</code> and its subclasses only).</li>
 * </ul>
 *
 * <p>
 * Each text component (subclass of <code>javax.swing.text.JTextComponent</code>) provides a set of built-in <code>javax.swing.Action</code> objects (which have
 * names like 'caret-forward', 'page-down', 'paste-from-clipboard', etc.) returned by its <code>getActions()</code> method. The component's look-and-feel (for
 * example, <code>javax.swing.plaf.basic.BasicTextUI</code>) calls the component's <code>getActions()</code> method to get a list of <code>Actions</code>
 * provided by the component, and maps the appropriate keystroke to each <code>Action</code> by name. Thus, for example, in the Windows look and feel class, the
 * <KBD>right-arrow</KBD> keystroke is linked to the action 'caret-forward', <KBD>PgDn</KBD> to 'page-down', and <KBD>Ctrl+Ins</KBD> to 'paste-from-clipboard'.
 * </p>
 *
 * <p>
 * Nearly all the functionality provided by <code>JdbTextField</code>, <code>JdbTextArea</code>, <code>JdbTextPane</code>, and <code>JdbEditorPane</code> is
 * available as public <code>Actions</code> in <code>DBTextDataBinder</code>. For example, <code>DBTextDataBinder</code> provides a public <code>Action</code>
 * named <code>LoadFileAction</code> that a user can add to a menu, button, or toolbar to load a file into a <code>JTextArea</code> as follows:
 * </p>
 *
 * <pre>
 * JButton loadFileButton = new JButton("Load file");
 * loadFileButton.addActionListener(new DBTextDataBinder.LoadFileAction());
 * </pre>
 *
 * <p>
 * When the button is pressed, the <code>LoadFileAction</code> is invoked, which brings up a Load File dialog box, allowing the user to select a text file to
 * load into the component. The following table lists the available actions, specifies the components the actions apply to, and describes what the actions do.
 * </p>
 *
 * <p>
 * <strong>DBTextDataBinder actions</strong>
 * </p>
 *
 * <TABLE BORDER COLS=3 WIDTH="100%" >
 *
 * <TR>
 * <TH ALIGN="LEFT">Action Name (Class)</TH>
 * <TH ALIGN="LEFT">Applies To</TH>
 * <TH ALIGN="LEFT">Description</TH>
 * </TR>
 *
 * <TR>
 * <TD>undo (<code>DBTextDataBinder.UndoAction</code>)</TD>
 * <TD>any subclass of <code>javax.swing.JTextComponent</code> bound to a <code>DBTextDataBinder</code></TD>
 * <TD>undoes the most recent change</TD>
 * </TR>
 *
 * <TR>
 * <TD>redo (<code>DBTextDataBinder.RedoAction</code>)</TD>
 * <TD>any subclass of <code>javax.swing.JTextComponent</code> bound to a <code>DBTextDataBinder</code></TD>
 * <TD>re-does the most recent change</TD>
 * </TR>
 *
 * <TR>
 * <TD>clear-all (<code>DBTextDataBinder.ClearAllAction</code>)</TD>
 * <TD>any subclass of <code>javax.swing.JTextComponent</code> bound to a <code>DBTextDataBinder</code></TD>
 * <TD>deletes all text in the component</TD>
 * </TR>
 *
 * <TR>
 * <TD>select-all (<code>DBTextDataBinder.SelectAllAction</code>)</TD>
 * <TD>any subclass of <code>javax.swing.JTextComponent</code></TD>
 * <TD>selects all text in the component</TD>
 * </TR>
 *
 * <TR>
 * <TD>load-URL (<code>DBTextDataBinder.LoadURLAction</code>)</TD>
 * <TD><code>javax.swing.JEditorPane</code> or <code>com.borland.dbswing.JdbEditorPane</code></TD>
 * <TD>loads an HTML page into a <code>JdbEditorPane</code> via URL</TD>
 * </TR>
 *
 * <TR>
 * <TD>load-file (<code>DBTextDataBinder.LoadFileAction</code>)</TD>
 * <TD>any subclass of <code>javax.swing.JTextComponent</code> other than <code>JTextField</code></TD>
 * <TD>loads data from a file into the component</TD>
 * </TR>
 *
 * <TR>
 * <TD>save-file (<code>DBTextDataBinder.SaveFileAction</code>)</TD>
 * <TD>any subclass of <code>javax.swing.JTextComponent</code> other than <code>JTextField</code></TD>
 * <TD>saves data in a component to a file</TD>
 * </TR>
 *
 * <TR>
 * <TD>next-row (<code>DBTextDataBinder.NextRowAction</code>)</TD>
 * <TD>any subclass of <code>javax.swing.JTextComponent</code> bound to a <code>DBTextDataBinder</code></TD>
 * <TD>navigates to the next <code>DataSet</code> row</TD>
 * </TR>
 *
 * <TR>
 * <TD>prior-row (<code>DBTextDataBinder.PriorRowAction</code>)</TD>
 * <TD>any subclass of <code>javax.swing.JTextComponent</code> bound to a <code>DBTextDataBinder</code></TD>
 * <TD>navigates to the prior <code>DataSet</code> row</TD>
 * </TR>
 *
 * <TR>
 * <TD>post-data (<code>DBTextDataBinder.PostDataAction</code>)</TD>
 *
 * <TD>any subclass of <code>javax.swing.JTextComponent</code> bound to a <code>DBTextDataBinder</code></TD>
 * <TD>posts component data to its corresponding <code>DataSet</code> <code>Column</code></TD>
 * </TR>
 *
 * <TR>
 * <TD>cancel-post (<code>DBTextDataBinder.CancelPostAction</code>)</TD>
 * <TD>any subclass of <code>javax.swing.JTextComponent</code> bound to a <code>DBTextDataBinder</code></TD>
 * <TD>reverts component data to its corresponding <code>DataSet</code> <code>Column</code> value</TD>
 * </TR>
 *
 * <TR>
 * <TD>font-dialog (<code>DBTextDataBinder.FontDialogAction</code>)</TD>
 * <TD>any subclass of <code>javax.swing.JTextPane</code></TD>
 * <TD>displays dbSwing's <code>FontChooser</code> dialog to change the text's font</TD>
 * </TR>
 *
 * <TR>
 * <TD>foreground-color-dialog (<code>DBTextDataBinder.ForegroundColorDialogAction</code>) *</TD>
 * <TD>any subclass of <code>javax.swing.JTextPane</code></TD>
 * <TD>displays Swing's <code>JColorChooser</code> dialog to change the text's foreground color</TD>
 * </TR>
 *
 * <TR>
 * <TD>background-color-dialog (<code>DBTextDataBinder.BackgroundColorDialogAction</code>)</TD>
 * <TD>any subclass of <code>javax.swing.JTextPane</code></TD>
 * <TD>displays Swing's <code>JColorChooser</code> dialog to change the text's background color</TD>
 * </TR>
 * </TABLE>
 *
 * <p>
 * <strong>Example:</strong>
 *
 * <PRE>
 * JTextField jTextField = new JTextField();
 * DBTextDataBinder dbTextDataBinder = new DBTextDataBinder();
 *
 * // attach the field to DBTextDataBinder
 * dbTextDataBinder.setJTextComponent(jTextField);
 *
 * // set the target DataSet and Column
 * dbTextDataBinder.setDataSet(dataSet);
 * dbTextDataBinder.setColumnName("Name");
 * </pre>
 */

public class DBTextDataBinder
		implements DocumentListener, FocusListener, MouseListener, KeyListener,
		AccessListener, DataChangeListener, NavigationListener,
		UndoableEditListener, PropertyChangeListener, HyperlinkListener,
		// ColumnChangeListener,
		ColumnAware, Designable, Serializable {

	/**
	 * <p>
	 * A reference to an action that undoes the previous action.
	 * </p>
	 */
	public static Action UNDO_ACTION;

	/**
	 * <p>
	 * A reference to an action that redoes the previously undone action.
	 * </p>
	 */
	public static Action REDO_ACTION;

	/**
	 * <p>
	 * A reference to an action that removes the data displayed in the component and places it in the system clipboard.
	 * </p>
	 */
	public static Action CUT_ACTION;

	/**
	 * <p>
	 * A reference to an action that copies the data displayed in the component to the system clipboard.
	 * </p>
	 */
	public static Action COPY_ACTION;

	/**
	 * <p>
	 * A reference to an action that pastes text from the system clipboard into the component.
	 * </p>
	 */
	public static Action PASTE_ACTION;

	/**
	 * <p>
	 * A reference to an action that clears the data displayed in the component.
	 * </p>
	 */
	public static Action CLEARALL_ACTION;

	/**
	 * <p>
	 * A reference to an action that selects all the text in the component.
	 * </p>
	 */
	public static Action SELECTALL_ACTION;

	/**
	 * <p>
	 * A reference to an action that displays a font dialog box so the user can select a font for the component.
	 * </p>
	 */
	public static Action FONTDIALOG_ACTION;

	/**
	 * <p>
	 * A reference to an action that displays a color dialog box so the user can select a foreground color for the component.
	 * </p>
	 */
	public static Action FOREGROUNDCOLOR_ACTION;

	/**
	 * <p>
	 * A reference to an action that displays a color dialog for changing the background color of the component.
	 * </p>
	 */
	public static Action BACKGROUNDCOLOR_ACTION;

	/**
	 * <p>
	 * A reference to an action that loads a file into the component.
	 * </p>
	 */
	public static Action LOADFILE_ACTION;

	/**
	 * <p>
	 * A reference to an action that loads a URL into the component.
	 * </p>
	 */
	public static Action LOADURL_ACTION;

	/**
	 * <p>
	 * A reference to an action that saves the content of the component to a file.
	 * </p>
	 */
	public static Action SAVEFILE_ACTION;

	/**
	 * <p>
	 * Constructs a <code>DBTextDataBinder</code>. Calls the <code>null</code> constructor of its superclass.
	 * </p>
	 */
	public DBTextDataBinder() {}

	/**
	 * <p>
	 * Constructs a <code>DBTextDataBinder</code> and specifies the <code>JTextComponent</code> it makes data-aware. Calls the <code>null</code> constructor of
	 * its superclass.
	 * </p>
	 *
	 * @param textComponent
	 *          The component you are making data-aware with <code>DBTextDataBinder</code>.
	 */

	public DBTextDataBinder(JTextComponent textComponent) {
		setJTextComponent(textComponent);
	}

	static {
		UNDO_ACTION = new UndoAction();
		UNDO_ACTION.putValue(Action.SMALL_ICON, new ImageIcon(UNDO_ACTION.getClass().getResource("image/undo.gif")));
		REDO_ACTION = new RedoAction();
		REDO_ACTION.putValue(Action.SMALL_ICON, new ImageIcon(REDO_ACTION.getClass().getResource("image/redo.gif")));
		CUT_ACTION = new DefaultEditorKit.CutAction();
		CUT_ACTION.putValue(Action.SMALL_ICON, new ImageIcon(DBTextDataBinder.class.getResource("image/cut.gif")));
		COPY_ACTION = new DefaultEditorKit.CopyAction();
		COPY_ACTION.putValue(Action.SMALL_ICON, new ImageIcon(DBTextDataBinder.class.getResource("image/copy.gif")));
		PASTE_ACTION = new DefaultEditorKit.PasteAction();
		PASTE_ACTION.putValue(Action.SMALL_ICON, new ImageIcon(DBTextDataBinder.class.getResource("image/paste.gif")));
		CLEARALL_ACTION = new ClearAllAction();
		CLEARALL_ACTION.putValue(Action.SMALL_ICON, new ImageIcon(CLEARALL_ACTION.getClass().getResource("image/clearAll.gif")));
		SELECTALL_ACTION = new SelectAllAction();
		FONTDIALOG_ACTION = new FontDialogAction();
		FONTDIALOG_ACTION.putValue(Action.SMALL_ICON, new ImageIcon(FONTDIALOG_ACTION.getClass().getResource("image/font.gif")));
		FOREGROUNDCOLOR_ACTION = new ForegroundColorDialogAction();
		FOREGROUNDCOLOR_ACTION.putValue(Action.SMALL_ICON, new ImageIcon(FOREGROUNDCOLOR_ACTION.getClass().getResource("image/fgcolor.gif")));
		BACKGROUNDCOLOR_ACTION = new BackgroundColorDialogAction();
		BACKGROUNDCOLOR_ACTION.putValue(Action.SMALL_ICON, new ImageIcon(BACKGROUNDCOLOR_ACTION.getClass().getResource("image/bgcolor.gif")));
		LOADFILE_ACTION = new LoadFileAction();
		LOADFILE_ACTION.putValue(Action.SMALL_ICON, new ImageIcon(LOADFILE_ACTION.getClass().getResource("image/loadFile.gif")));
		LOADURL_ACTION = new LoadURLAction();
		LOADURL_ACTION.putValue(Action.SMALL_ICON, new ImageIcon(LOADURL_ACTION.getClass().getResource("image/loadURL.gif")));
		SAVEFILE_ACTION = new SaveFileAction();
		SAVEFILE_ACTION.putValue(Action.SMALL_ICON, new ImageIcon(SAVEFILE_ACTION.getClass().getResource("image/saveFile.gif")));
	}

	//
	// DBTextDataBinder properties
	//

	/**
	 * <p>
	 * Sets the text component that <code>DBTextDataBinder</code> makes data-aware. The component must be one that extends <code>JTextComponent</code>, such as
	 * <code>JTextField</code>, <code>JTextArea</code>, <code>JTextPane</code>, or <code>JEditorPane</code>.
	 * </p>
	 *
	 * @param textComponent
	 *          The text component that <code>DBTextDataBinder</code> makes data-aware.
	 * @see #getJTextComponent
	 */
	public void setJTextComponent(JTextComponent textComponent) {
		if (this.textComponent != null && this.textComponent != textComponent) {
			this.textComponent.removePropertyChangeListener(this);
			this.textComponent.removeFocusListener(this);
			this.textComponent.removeMouseListener(this);
			this.textComponent.removeKeymap(keymapName);
			if (this.textComponent instanceof JEditorPane &&
					!(this.textComponent instanceof JTextPane)) {
				((JEditorPane) this.textComponent).removeHyperlinkListener(this);
			}
		}
		this.textComponent = textComponent;

		if (textComponent == null) {
			setDocument(null);
		} else {
			textComponent.addPropertyChangeListener(this);
			textComponent.addFocusListener(this);
			textComponent.addMouseListener(this);
			textComponent.addKeyListener(this);
			// add our keymap to the component
			Keymap originalKeymap = textComponent.getKeymap();
			Keymap textDataBinderKeymap = JTextComponent.addKeymap(keymapName, originalKeymap);
			JTextComponent.loadKeymap(textDataBinderKeymap,
																(textComponent instanceof JTextField) ? fieldKeyBindings : areaKeyBindings,
																TextAction.augmentList(textComponent.getActions(), textDataBinderActions));
			textComponent.setKeymap(textDataBinderKeymap);
			setDocument(textComponent.getDocument());
			if (textComponent instanceof JEditorPane && !(textComponent instanceof JTextPane)) {
				((JEditorPane) textComponent).addHyperlinkListener(this);
			}
		}
	}

	/**
	 * <p>
	 * Retursn the text component that <code>DBTextDataBinder</code> makes data-aware. The component extends <code>JTextComponent</code>.
	 * </p>
	 *
	 * @return The text component that <code>DBTextDataBinder</code> makes data-aware.
	 * @see #setJTextComponent
	 */
	public JTextComponent getJTextComponent() {
		return textComponent;
	}

	/**
	 * <p>
	 * Sets the the text component's model. Any component using a <code>Document</code> class as its model can be made data-aware. Note that when using this
	 * approach you are responsible for opening the <code>DataSet</code> before using it.
	 * </p>
	 *
	 * @param document
	 *          The text component's model.
	 * @see #getDocument
	 */
	public void setDocument(Document document) {
		if (this.document != null) {
			this.document.removeDocumentListener(this);
			this.document.removeUndoableEditListener(this);
			undoManager.discardAllEdits();
		}

		this.document = document;

		if (document != null) {
			document.addDocumentListener(this);
			document.addUndoableEditListener(this);
		}
		rebuildMenu = true;
		bindColumnProperties();
	}

	/**
	 * <p>
	 * Returns the the text component's model.
	 * </p>
	 *
	 * @return The text component's model.
	 * @see #setDocument
	 */
	public Document getDocument() {
		return document;
	}

	// used to bind/unbind the data binder from the document to support serialization.
	void bindDocument(Document document, boolean bind) {
		if (bind) {
			document.addDocumentListener(this);
			document.addUndoableEditListener(this);
		} else {
			document.removeDocumentListener(this);
			document.removeUndoableEditListener(this);
		}
	}

	/**
	 * <p>
	 * Sets whether a popup menu is enabled on the component. If <code>enablePopupMenu</code> is <code>true</code>, a menu pops up when the user right-clicks on
	 * the component or presses <KBD>Shift+F10</KBD>. This is <code>true</code> by default.
	 * </p>
	 *
	 * @param enablePopup
	 *          If <code>true</code>, a popup menu is enabled on the component.
	 * @see #isEnablePopupMenu
	 */
	public void setEnablePopupMenu(boolean enablePopup) {
		this.enablePopup = enablePopup;
	}

	/**
	 * <p>
	 * Returns whether a popup menu is enabled on the component.
	 * </p>
	 *
	 * @return If <code>true</code>, a popup menu is enabled on the component.
	 * @see #setEnablePopupMenu
	 */
	public boolean isEnablePopupMenu() {
		return enablePopup;
	}

	/**
	 * <p>
	 * Sets whether the current text is entered in the <code>DataSet's</code> column when focus is lost on the text area. The default value is <code>true</code>.
	 * </p>
	 *
	 * @param postOnFocusLost
	 *          If <code>true</code>, the current text is entered in the column.
	 * @see #isPostOnFocusLost
	 */
	public void setPostOnFocusLost(boolean postOnFocusLost) {
		this.postOnFocusLost = postOnFocusLost;
	}

	/**
	 * <p>
	 * Returns whether the current text is entered in the <code>DataSet's</code> column when focus is lost on the text area.
	 * </p>
	 *
	 * @return If <code>true</code>, the current text is entered in the column.
	 * @see #setPostOnFocusLost
	 */

	public boolean isPostOnFocusLost() {
		return postOnFocusLost;
	}

	/**
	 * <p>
	 * Sets whether the current text should be put in the <code>DataSet's</code> column when the current row is posted. This occurs, for example, if the user
	 * presses a row navigation key while the text component has current focus. The default value is <code>true</code>.
	 * </p>
	 *
	 * @param postOnRowPosted
	 *          If <code>true</code>, the current text should be put in the <code>DataSet's</code> column.
	 * @see #isPostOnRowPosted
	 */
	public void setPostOnRowPosted(boolean postOnRowPosted) {
		this.postOnRowPosted = postOnRowPosted;
	}

	/**
	 * <p>
	 * Returns whether the current text should be put in the <code>DataSet's</code> column when the current row is posted.
	 * </p>
	 *
	 * @return If <code>true</code>, the current text should be put in the <code>DataSet's</code> column.
	 * @see #setPostOnRowPosted
	 */
	public boolean isPostOnRowPosted() {
		return postOnRowPosted;
	}

	// write current field data to dataset column. does nothing if there's not a valid dataset and column
	// if an error occurs on post, tries to set focus back to the current field

	/**
	 * <p>
	 * Writes the current field data to the <code>DataSet</code> column if the <code>dataSet</code> and <code>columnName</code> properties are valid. The
	 * <code>postText()</code> method displays an error dialog if the data is invalid.
	 * </p>
	 */
	public void postText() {
		try {
			postText2();
		} catch (Exception e) {
			DBExceptionHandler.handleException(columnAwareSupport.dataSet, textComponent, e);
			if (textComponent != null) {
				textComponent.requestFocus();
			}
		}
	}

	/**
	 * <p>
	 * Writes the current field data to the <code>DataSet</code> column if the <code>dataSet</code> and <code>columnName</code> properties are valid. The
	 * <code>postText2()</code> method throws an exception if the data is invalid.
	 * </p>
	 *
	 * @throws Exception
	 *           The exception that was thrown.
	 */
	public void postText2() throws Exception {
		if (isTextModified() && columnAwareSupport.isValidDataSetState()) {

			ignoreValueChange = true;
			if (columnAwareSupport.getColumn().getDataType() == Variant.OBJECT) {
				bindDocument(document, false); // to allow DataStore to serialize the document
				ByteArrayOutputStream baos;
				ObjectOutputStream oos = new ObjectOutputStream(baos = new ByteArrayOutputStream(50000));
				oos.writeObject(document);
				oos.flush();
				oos.close();
				byte[] b = baos.toByteArray();
				ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(b));
				columnAwareSupport.setObject((Document) ois.readObject());
				baos = null;
				bindDocument(document, true);
			} else {
				String text = null;
				if (ems != null) {
					if (maskVariant == null) {
						maskVariant = new Variant();
					}
					try {
						if (!ems.isComplete(state)) { throw new InvalidFormatException(Res._DataEntryIncomplete); }
						ems.getFinalValue(state, maskVariant);
						columnAwareSupport.setVariant(maskVariant);
						document.remove(0, document.getLength());
						document.insertString(0, columnAwareSupport.getFormattedString(), null);
						displayMode = true;
					} catch (Exception e) {
						if (e instanceof InvalidFormatException) {
							state.cursorPos = ((InvalidFormatException) e).getErrorOffset(); // error adjusts cursor
						}
						updateSelection(); // and shows it to user
						ignoreValueChange = false;
						ValidationException.invalidFormat(e, null, null);
					}
				} else {
					text = document.getText(0, document.getLength());
					columnAwareSupport.setFromString2(text, textComponent, columnAwareSupport);
					document.remove(0, document.getLength());
					document.insertString(0, expandDateYear(columnAwareSupport), null);
				}
				if (textComponent != null) {
					textComponent.setCaretPosition(0);
					if (!focusLost && textComponent instanceof JTextField) {
						textComponent.moveCaretPosition(document.getLength());
						if (ems != null) {
							ems.move(state, KeyEvent.VK_END);
						}
					}
				}
			}
			ignoreValueChange = false;
			setTextModified(false);
		}
		if (customPaintSite != null) {
			customPaintSite.firePainting(textComponent, this, columnPaintListener);
		}
	}

	/**
	 * <p>
	 * Synchronizes the text with the current DataSet value.
	 * </p>
	 */
	public void updateText() {
		if (ignoreValueChange) { return; }
		if (customPaintSite != null) {
			customPaintSite.firePainting(textComponent, this, columnPaintListener);
		}
		ignoreValueChange = true;
		try {
			if (columnAwareSupport.isValidDataSetState()) {
				if (columnAwareSupport.getColumn().getDataType() == Variant.OBJECT) {
					if (textComponent != null) {
						Column columnURL;
						if (columnNameURL != null && (columnURL = columnAwareSupport.dataSet.hasColumn(columnNameURL)) != null &&
								!columnAwareSupport.dataSet.isNull(columnNameURL) && columnAwareSupport.dataSet.isNull(columnAwareSupport.columnName)) {
							((JEditorPane) textComponent).setPage(columnAwareSupport.dataSet.getString(columnNameURL));
						} else {
							Object object = columnAwareSupport.dataSet.getObject(columnAwareSupport.columnName);
							if (object instanceof Document) { // Variant seems to return a ByteArrayInputStream for null values
								try {
									ByteArrayOutputStream baos;
									ObjectOutputStream oos = new ObjectOutputStream(baos = new ByteArrayOutputStream(50000));
									oos.writeObject(columnAwareSupport.dataSet.getObject(columnAwareSupport.columnName));
									oos.flush();
									oos.close();
									byte[] b = baos.toByteArray();
									ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(b));
									textComponent.setDocument((Document) ois.readObject());
									baos = null;
								} catch (Exception e) {
									DBExceptionHandler.handleException(columnAwareSupport.dataSet, textComponent, e);
								}
							} else {
								if (!ignoreModelChange) {
									textComponent.setDocument(textComponent.getUI().getEditorKit(textComponent).createDefaultDocument());
								}
							}
						}
					}
				} else {
					document.remove(0, document.getLength());
					document.insertString(0, expandDateYear(columnAwareSupport), null);
					if (ems != null) {
						displayMode = true;
						// state = ems.prepare(columnAwareSupport.getVariant());
					}
				}
				if (textComponent != null) {
					textComponent.setCaretPosition(0);
					// if (ems != null) {
					// ems.move(state, KeyEvent.VK_END);
					// }
				}
			}
		} catch (Exception e) {
			DBExceptionHandler.handleException(columnAwareSupport.dataSet, textComponent, e);
		}
		setTextModified(false);
		ignoreValueChange = false;
	}

	/**
	 * <p>
	 * The <code>expandDateYear()</code> method returns a dataset column value with the year always expanded to a 4 digit year (the default is to show only 2
	 * digits), unless an edit mask has been explicitly set on the column.
	 * </p>
	 *
	 * @param columnAwareSupport
	 *          The name of the dataset column to expand.
	 * @return The formatted value.
	 */
	protected String expandDateYear(DBColumnAwareSupport columnAwareSupport) {
		Column column = columnAwareSupport.getColumn();
		String formattedValue;
		if (column != null && column.getEditMask() == null &&
				column.getFormatter().getFormatObj() instanceof java.text.DateFormat) {
			String oldPattern = column.getFormatter().getPattern();
			String widePattern = null;
			int yearMaskIndex = -1;
			if ((yearMaskIndex = DBUtilities.yearMaskPos(oldPattern)) != -1) {
				int yearMaskCount = 1;
				int patternLength = oldPattern.length();
				while ((yearMaskIndex + yearMaskCount) < patternLength && oldPattern.charAt(yearMaskIndex + yearMaskCount) == 'y') {
					yearMaskCount++;
				}
				if (yearMaskCount < 4) {
					widePattern = oldPattern.substring(0, yearMaskIndex) + "yyyy" +
							((yearMaskIndex + yearMaskCount < patternLength) ? oldPattern.substring(yearMaskIndex + yearMaskCount) : "");
				}
			}
			if (widePattern != null) {
				column.getFormatter().setPattern(widePattern);
			}
			formattedValue = column.format(columnAwareSupport.getVariant());
			if (widePattern != null) {
				column.getFormatter().setPattern(oldPattern);
			}
		} else {
			formattedValue = columnAwareSupport.getFormattedString();
		}
		return formattedValue;
	}

	private static javax.swing.filechooser.FileFilter HTMLFileFilter = new javax.swing.filechooser.FileFilter() {
		public boolean accept(File file) {
			if (file.isDirectory()) { return true; }
			String fileName = file.getName().toLowerCase();
			if (fileName.toUpperCase().endsWith("HTML") ||
					fileName.toUpperCase().endsWith("HTM")) { return true; }
			return false;
		}

		public String getDescription() {
			return Res._HTMLFileFilter;
		}
	};

	private static javax.swing.filechooser.FileFilter RTFFileFilter = new javax.swing.filechooser.FileFilter() {
		public boolean accept(File file) {
			if (file.isDirectory()) { return true; }
			String fileName = file.getName().toLowerCase();
			if (fileName.toUpperCase().endsWith("RTF")) { return true; }
			return false;
		}

		public String getDescription() {
			return Res._RTFFileFilter;
		}
	};

	private static javax.swing.filechooser.FileFilter TextFileFilter = new javax.swing.filechooser.FileFilter() {
		public boolean accept(File file) {
			if (file.isDirectory()) { return true; }
			String fileName = file.getName().toLowerCase();
			if (fileName.toUpperCase().endsWith("TXT")) { return true; }
			return false;
		}

		public String getDescription() {
			return Res._TextFileFilter;
		}
	};

	private static javax.swing.filechooser.FileFilter SerFileFilter = new javax.swing.filechooser.FileFilter() {
		public boolean accept(File file) {
			if (file.isDirectory()) { return true; }
			String fileName = file.getName().toLowerCase();
			if (fileName.toUpperCase().endsWith("SER")) { return true; }
			return false;
		}

		public String getDescription() {
			return Res._SerFileFilter;
		}
	};

	private static JFileChooser createFileChooser(javax.swing.filechooser.FileFilter[] fileFilters) {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setMultiSelectionEnabled(false);
		}

		fileChooser.resetChoosableFileFilters();

		for (int i = 0; i < fileFilters.length; i++) {
			fileChooser.addChoosableFileFilter(fileFilters[i]);
		}
		// workaround for Java bug id: 4163841
		// to remove the 'all files' filter, I had to call setFileFilter and then remove the
		// 'all files' filter (dcy).
		fileChooser.setFileFilter(fileFilters[0]);
		fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());

		return fileChooser;
	}

	//
	// ColumnAware interface implememtation
	//

	/**
	 * <p>
	 * Sets the <code>DataSet</code> from which values are read and to which values are written.
	 * </p>
	 *
	 * @param dataSet
	 *          The <code>DataSet</code>.
	 * @see #getDataSet
	 * @see #setColumnName
	 */
	public void setDataSet(DataSet dataSet) {
		if (columnAwareSupport.dataSet != null) {
			columnAwareSupport.dataSet.removeNavigationListener(this);
		}
		columnAwareSupport.setDataSet(dataSet);
		if (dataSet != null) {
			columnAwareSupport.dataSet.addNavigationListener(this);
			bindColumnProperties();
		}
	}

	/**
	 * <p>
	 * Returns the <code>DataSet</code> from which values are read and to which values are written.
	 * </p>
	 *
	 * @return The <code>DataSet</code>.
	 * @see #setDataSet
	 * @see #getColumnName
	 */
	public DataSet getDataSet() {
		return columnAwareSupport.dataSet;
	}

	/**
	 * <p>
	 * Sets the column name of the <code>DataSet</code> to/from which values should be written/read. For a <code>JdbTextField</code>, the column can be of any
	 * type. For a <code>JdbTextArea</code>, the column would typically be of type <code>String.</code> For <code>JdbTextPane</code> and
	 * <code>JdbEditorPane</code> components, the column can be of type <code>String</code>, but must be of type <code>Object</code> to hold styled text, RTF, or
	 * HTML documents.
	 * </p>
	 *
	 * @param columnName
	 *          The column name of the <code>DataSet</code>.
	 * @see #getColumnName
	 * @see #setDataSet
	 */
	public void setColumnName(String columnName) {
		columnAwareSupport.setColumnName(columnName);
		if (columnName != null) {
			bindColumnProperties();
		}
	}

	/**
	 * <p>
	 * Returns the column name of the <code>DataSet</code> to/from which values should be written/read.
	 * </p>
	 *
	 * @return The column name of the <code>DataSet</code>.
	 * @see #setColumnName
	 * @see #getDataSet
	 */

	public String getColumnName() {
		return columnAwareSupport.columnName;
	}

	/**
	 * <p>
	 * Sets the column name in which URL names (e.g., "http://www.borland.com") are cached and searched. The column should be of type <code>String</code>, and
	 * will only be used if a <code>JdbEditorPane</code> is bound to this data binder. Setting a value in this column will cause the corresponding page to be
	 * loaded by a <code>JdbEditorPane</code> and saved in <code>columnName.</code>
	 * </p>
	 *
	 * @param columnNameURL
	 *          The column name in which URL names are cached and searched.
	 * @see #getColumnNameURL
	 * @see #setDataSet
	 * @see #setEnableURLAutoCache
	 */
	public void setColumnNameURL(String columnNameURL) {
		this.columnNameURL = columnNameURL;
	}

	/**
	 * <p>
	 * Returns the column name in which URL names (e.g., "http://www.borland.com") are cached and searched.
	 * </p>
	 *
	 * @return The column name in which URL names are cached and searched.
	 * @see #setColumnNameURL
	 * @see #getDataSet
	 * @see #isEnableURLAutoCache
	 */

	public String getColumnNameURL() {
		return columnNameURL;
	}

	/**
	 * <p>
	 * Creates the popup menu displayed when the user right-clicks or presses <KBD>Shift+F10</KBD> within the bound text component.
	 * </p>
	 *
	 * <p>
	 * The contents of the menu vary according to the kind of text component and property settings as shown in the following table. Menu items are displayed in
	 * the vertical order in which they are listed below.
	 * </p>
	 *
	 * <p>
	 * <strong>DBTextDataBinder popup menus</strong>
	 * </p>
	 *
	 * <TABLE BORDER COLS=3>
	 *
	 * <TR>
	 * <TH ALIGN="LEFT">Menu item</TH>
	 * <TH ALIGN="LEFT">Description</TH>
	 * <TH ALIGN="LEFT">Availability</TH>
	 * </TR>
	 *
	 * <TR>
	 * <TD>Cut</TD>
	 * <TD>Removes selected text to clipboard</TD>
	 * <TD><code>editable</code> = <code>true</code></TD>
	 * </TR>
	 *
	 * <TR>
	 * <TD>Copy</TD>
	 * <TD>Copies selected text to clipboard</TD>
	 * <TD>always</TD>
	 * </TR>
	 *
	 * <TR>
	 * <TD>Paste</TD>
	 * <TD>Pastes text from clipboard</TD>
	 * <TD><code>editable</code> = <code>true</code></TD>
	 * </TR>
	 *
	 * <TR>
	 * <TD>ClearAll</TD>
	 * <TD>Deletes all text</TD>
	 * <TD><code>enableClearAll</code> = <code>true</code></TD>
	 * </TR>
	 *
	 * <TR>
	 * <TD>SelectAll</TD>
	 * <TD>Selects all text</TD>
	 * <TD>always</TD>
	 * </TR>
	 *
	 * <TR>
	 * <TD>Font...</TD>
	 * <TD>Sets font of selected text</TD>
	 * <TD><code>JdbTextPane</code>, <code>enableFontChange</code> = <code>true</code></TD>
	 * </TR>
	 * 
	 * <TR>
	 * <TD>Foreground Color...</TD>
	 * <TD>Sets foreground color of selected text</TD>
	 * <TD><code>JdbTextPane</code>, <code>enableColorChange</code> = <code>true</code></TD>
	 * </TR>
	 *
	 * <TR>
	 * <TD>Background Color...</TD>
	 * <TD>Sets background color of selected text</TD>
	 * <TD><code>JdbTextPane</code>, <code>enableColorChange</code> = <code>true</code></TD>
	 * </TR>
	 *
	 * <TR>
	 * <TD>Open URL...</TD>
	 * <TD>Loads HTML content from a URL</TD>
	 * <TD><code>JdbEditorPane</code>, <code>enableURLLoading</code> = <code>true</code></TD>
	 * </TR>
	 *
	 * <TR>
	 * <TD>Open File...</TD>
	 * <TD>Loads content from a file</TD>
	 * <TD><code>JdbTextArea</code> (text files only), <code>JdbTextPane</code> (text or ser file), <code>JdbEditorPane</code> (text, RTF, HTML, ser files),
	 * <code>enableFileLoading</code> = <code>true</code></TD>
	 * </TR>
	 *
	 * <TR>
	 * <TD>Save File...</TD>
	 * <TD>Saves contents to a file</TD>
	 * <TD><code>JdbTextArea</code> (text files only), <code>JdbTextPane</code> (text or ser file), <code>JdbEditorPane</code> (text, RTF, HTML, ser files),
	 * <code>enableFileSaving</code> = <code>true</code></TD>
	 * </TR>
	 *
	 * <TR>
	 * <TD>Undo</TD>
	 * <TD>Undoes the most recent change</TD>
	 * <TD><code>enableUndoRedo</code> = <code>true</code></TD>
	 * </TR>
	 *
	 * <TR>
	 * <TD>Redo</TD>
	 * <TD>Redoes the most recent change</TD>
	 * <TD><code>enableUndoRedo</code> = <code>true</code></TD>
	 * </TR>
	 * </TABLE>
	 *
	 * <p>
	 * Setting the <code>enablePopupMenu</code> property to <code>false</code> prevents the entire menu from appearing. To add your own menu items to the menu,
	 * override this method in a subclass to return a customized menu.
	 * </p>
	 *
	 * @return The menu.
	 */
	protected JPopupMenu createPopupMenu() {
		// should only be called in textComponent != null

		if (menu != null && !rebuildMenu) { return menu; }

		menu = new JPopupMenu();
		menu.setDefaultLightWeightPopupEnabled(false);

		JMenuItem menuItem;

		if (textComponent.isEditable()) {
			menuItem = menu.add(CUT_ACTION);
			menuItem.setIcon(null);
			menuItem.setText(Res._Cut);
		}

		menuItem = menu.add(COPY_ACTION);
		menuItem.setIcon(null);
		menuItem.setText(Res._Copy);

		if (textComponent.isEditable()) {
			menuItem = menu.add(PASTE_ACTION);
			menuItem.setIcon(null);
			menuItem.setText(Res._Paste);
		}

		menu.addSeparator();

		if (textComponent.isEditable() && enableClearAll) {
			menuItem = menu.add(CLEARALL_ACTION);
			menuItem.setIcon(null);
			menuItem.setText(Res._ClearAll);
		}

		menuItem = menu.add(SELECTALL_ACTION);
		menuItem.setText(Res._SelectAll);

		if ((enableFontChange || enableColorChange) && textComponent.isEditable() && textComponent instanceof JEditorPane &&
				(((JEditorPane) textComponent).getEditorKit() instanceof StyledEditorKit ||
						((JEditorPane) textComponent).getEditorKit() instanceof RTFEditorKit)) {
			if (columnAwareSupport.dataSet == null || columnAwareSupport.columnName == null ||
					(columnAwareSupport.columnOrdinal != -1 && columnAwareSupport.getColumn().getDataType() == Variant.OBJECT)) {

				if (enableFontChange) {
					menu.addSeparator();
					menuItem = menu.add(FONTDIALOG_ACTION);
					menuItem.setIcon(null);
					menuItem.setText(Res._Font);
				}

				if (enableColorChange) {
					if (!enableFontChange) {
						menu.addSeparator();
					}

					menuItem = menu.add(FOREGROUNDCOLOR_ACTION);
					menuItem.setIcon(null);
					menuItem.setText(Res._ForegroundColor);

					menuItem = menu.add(BACKGROUNDCOLOR_ACTION);
					menuItem.setIcon(null);
					menuItem.setText(Res._BackgroundColor);
				}

			}
		}

		boolean loadMenuAdded = false;

		if ((textComponent instanceof JEditorPane && !(textComponent instanceof JTextPane)) ||
				(enableFileLoading && textComponent.isEditable() && !(textComponent instanceof JTextField))) {

			if (textComponent instanceof JTextArea || textComponent instanceof JTextPane) {
				menu.addSeparator();
				menuItem = menu.add(LOADFILE_ACTION);
				menuItem.setIcon(null);
				menuItem.setText(Res._OpenMenuDialog);
				loadMenuAdded = true;
			} else { // JEditorPane
				menu.addSeparator();
				JMenu loadSubMenu = new JMenu(Res._OpenMenu);
				if (textComponent.isEditable() && enableFileLoading) {
					if (enableURLLoading) {
						menuItem = loadSubMenu.add(LOADURL_ACTION);
						menuItem.setIcon(null);
						menuItem.setText(Res._OpenURLDialog);
						loadMenuAdded = true;
					}

					menuItem = loadSubMenu.add(LOADFILE_ACTION);
					menuItem.setIcon(null);
					menuItem.setText(Res._OpenFileDialog);
					menu.add(loadSubMenu);
					loadMenuAdded = true;
				} else {
					if (enableURLLoading) {
						menuItem = menu.add(LOADURL_ACTION);
						menuItem.setIcon(null);
						menuItem.setText(Res._OpenURL);
						loadMenuAdded = true;
					}
				}
			}
		}

		if (textComponent instanceof JTextArea || textComponent instanceof JEditorPane) {
			if (enableFileSaving) {
				if (!loadMenuAdded) {
					menu.addSeparator();
				}

				menuItem = menu.add(SAVEFILE_ACTION);
				menuItem.setIcon(null);
				menuItem.setText(Res._SaveMenuDialog);
			}
		}

		if (textComponent.isEditable() && enableUndoRedo) {
			menu.addSeparator();

			menuItem = menu.add(UNDO_ACTION);
			menuItem.setIcon(null);
			menuItem.setText(Res._Undo);

			menuItem = menu.add(REDO_ACTION);
			menuItem.setIcon(null);
			menuItem.setText(Res._Redo);
		}

		rebuildMenu = false;

		return menu;
	}

	/**
	 * <p>
	 * Sets whether the Clear All menu selection is added to the default popup menu. <code>True</code> by default.
	 * </p>
	 *
	 * @param enableClearAll
	 *          If <code>true</code>, Clear All is added to the default popup menu.
	 * @see #isEnableClearAll
	 */
	public void setEnableClearAll(boolean enableClearAll) {
		this.enableClearAll = enableClearAll;
		rebuildMenu = true;
	}

	/**
	 * <p>
	 * Returns whether the Clear All menu selection has been added to the default popup menu.
	 *
	 * @return If <code>true</code>, Clear All is added to the default popup menu.
	 * @see #setEnableClearAll
	 */
	public boolean isEnableClearAll() {
		return enableClearAll;
	}

	/**
	 * <p>
	 * Sets whether the foreground and background color menu selections are added to the default popup menu. <code>True</code> by default.
	 * </p>
	 *
	 * @param enableColorChange
	 *          If <code>true</code>, the foreground and background color menu selections are added to the default popup menu.
	 * @see #isEnableColorChange
	 */
	public void setEnableColorChange(boolean enableColorChange) {
		this.enableColorChange = enableColorChange;
		rebuildMenu = true;
	}

	/**
	 * <p>
	 * Returns whether the foreground and background color menu selections are added to the default popup menu.
	 *
	 * @return If <code>true</code>, the foreground and background color menu selections are added to the default popup menu.
	 * @see #setEnableColorChange
	 */
	public boolean isEnableColorChange() {
		return enableColorChange;
	}

	/**
	 * <p>
	 * Sets whether the font setting menu command is displayed on the popup menu.
	 * </p>
	 *
	 * <p>
	 * The default value is <code>true</code>.
	 * </p>
	 *
	 * @param enableFontChange
	 *          If <code>true</code>, the font setting menu command is displayed.
	 * @see #isEnableFontChange
	 * @see #setEnablePopupMenu
	 */
	public void setEnableFontChange(boolean enableFontChange) {
		this.enableFontChange = enableFontChange;
		rebuildMenu = true;
	}

	/**
	 * <p>
	 * Returns whether the font setting menu command is displayed on the popup menu.
	 * </p>
	 *
	 * <p>
	 * The default value is <code>true</code>.
	 * </p>
	 *
	 * @return If <code>true</code>, the font setting menu command is displayed.
	 * @see #setEnableFontChange
	 * @see #isEnablePopupMenu
	 */
	public boolean isEnableFontChange() {
		return enableFontChange;
	}

	/**
	 * <p>
	 * Sets whether the Undo and Redo popu menu commands are displayed on the popup menu. The default value is <code>true</code>.
	 * </p>
	 *
	 * @param enableUndoRedo
	 *          If <code>true</code>, the Undo/Redo menu selection is displayed.
	 * @see #isEnableUndoRedo
	 * @see #setEnablePopupMenu
	 */
	public void setEnableUndoRedo(boolean enableUndoRedo) {
		this.enableUndoRedo = enableUndoRedo;
		rebuildMenu = true;
	}

	/**
	 * <p>
	 * Returns whether the Undo and Redo popu menu commands are displayed on the popup menu.
	 * </p>
	 *
	 * @return If <code>true</code>, the Undo/Redo menu selection is displayed.
	 * @see #setEnableUndoRedo
	 * @see #isEnablePopupMenu
	 */
	public boolean isEnableUndoRedo() {
		return enableUndoRedo;
	}

	/**
	 * <p>
	 * Sets whether the Open URL menu command is displayed on the popup menu.
	 * </p>
	 *
	 * <p>
	 * The default value is <code>true</code>.
	 * </p>
	 *
	 * @param enableURLLoading
	 *          If <code>true</code>, the Open URL menu command is displayed.
	 * @see #isEnableURLLoading
	 * @see #setEnablePopupMenu
	 */
	public void setEnableURLLoading(boolean enableURLLoading) {
		this.enableURLLoading = enableURLLoading;
		rebuildMenu = true;
	}

	/**
	 * <p>
	 * Returns whether the Open URL menu command is displayed on the popup menu.
	 * </p>
	 *
	 * @return If <code>true</code>, the Open URL menu command is displayed.
	 * @see #setEnableURLLoading
	 * @see #isEnablePopupMenu
	 */
	public boolean isEnableURLLoading() {
		return enableURLLoading;
	}

	/**
	 * <p>
	 * Sets whether HTML pages fetched using hyperlinks are automatically inserted as new rows if they don't already exist in the <code>DataSet</code> (for
	 * example, if the URL in the <code>columnNameURL</code> column can't be found).
	 * </p>
	 *
	 * <p>
	 * This property is ignored unless the data binder is bound to a <code>JdbEditorPane</code>, the <code>columnNameURL</code> property is set, and the
	 * associated <code>DataSet</code> allows new rows to be inserted. The default value is <code>true</code>.
	 * </p>
	 *
	 * @param enableURLCache
	 *          If <code>true</code>, HTML pages are automatically inserted as new rows.
	 * @see #isEnableURLAutoCache
	 */
	public void setEnableURLAutoCache(boolean enableURLCache) {
		this.enableURLCache = enableURLCache;
	}

	/**
	 * <p>
	 * Returns whether HTML pages fetched using hyperlinks are automatically inserted as new rows if they don't already exist in the <code>DataSet</code> (for
	 * example, if the URL in the <code>columnNameURL</code> column can't be found).
	 * </p>
	 *
	 * @return If <code>true</code>, HTML pages are automatically inserted as new rows.
	 * @see #setEnableURLAutoCache
	 */
	public boolean isEnableURLAutoCache() {
		return enableURLCache;
	}

	/**
	 * <p>
	 * Determines if pressing <kbd>Enter</kbd> in a <code>JTextField</code> automatically moves focus to the next focusable field. This is <code>true</code> by
	 * default.
	 * </p>
	 *
	 * @param nextFocusOnEnter
	 *          If <code>true</code>, pressing <kbd>Enter</kbd> will move focus to next focusable field.
	 * @see #isNextFocusOnEnter
	 */
	public void setNextFocusOnEnter(boolean nextFocusOnEnter) {
		this.nextFocusOnEnter = nextFocusOnEnter;
	}

	/**
	 * <p>
	 * Returns <code>true</code>if pressing <kbd>Enter</kbd> in a <code>JTextField</code> automaticallys moves focus to the next focusable field.
	 * </p>
	 *
	 * @return If <code>true</code>, pressing <kbd>Enter</kbd> will move focus to next focusable field.
	 * @see #setNextFocusOnEnter
	 */
	public boolean isNextFocusOnEnter() {
		return nextFocusOnEnter;
	}

	/**
	 * <p>
	 * Sets whether the Load Text menu command is displayed on the popup menu. This is <code>true</code> by default.
	 * </p>
	 *
	 * @param enableFileLoading
	 *          If <code>true</code>, the Load Text menu command is displayed.
	 * @see #isEnableFileLoading
	 * @see #setEnablePopupMenu
	 */
	public void setEnableFileLoading(boolean enableFileLoading) {
		this.enableFileLoading = enableFileLoading;
	}

	/**
	 * <p>
	 * Returns whether the Load Text menu command is displayed on the popup menu.
	 * </p>
	 *
	 * @return If <code>true</code>, the Load Text menu command is displayed.
	 * @see #setEnableFileLoading
	 * @see #isEnablePopupMenu
	 */
	public boolean isEnableFileLoading() {
		return enableFileLoading;
	}

	/**
	 * <p>
	 * Sets whether the Save Text menu command is displayed on the popup menu. This is <code>true</code> by default.
	 * </p>
	 *
	 * @param enableFileSaving
	 *          If <code>true</code>, the Save Text menu command is displayed.
	 * @see #isEnableFileSaving
	 * @see #setEnablePopupMenu
	 */

	public void setEnableFileSaving(boolean enableFileSaving) {
		this.enableFileSaving = enableFileSaving;
	}

	/**
	 * <p>
	 * Returns whether the Save Text menu command is displayed on the popup menu.
	 * </p>
	 *
	 * @return If <code>true</code>, the Save Text menu command is displayed.
	 * @see #isEnableFileSaving
	 * @see #isEnablePopupMenu
	 */
	public boolean isEnableFileSaving() {
		return enableFileSaving;
	}

	/**
	 * Returns whether or not the text has been modified. It is set to <code>true</code> on any change to the document model. It is set to <code>false</code>
	 * after the value has been saved to a <code>DataSet</code> or file.
	 *
	 * @return If <code>true</code>, text has been modified.
	 * @see #setTextModified
	 */

	public boolean isTextModified() {
		return textModified;
	}

	/**
	 * Sets whether or not the text has been modified. Set to <code>true</code> on any change to the document model. Set to <code>false</code> after the value has
	 * been saved to a <code>DataSet</code> or file.
	 *
	 * @param textModified
	 *          If <code>true</code>, text has been modified.
	 * @see #isTextModified
	 */
	public void setTextModified(boolean textModified) {
		this.textModified = textModified;
	}

	private void displayPopup(Point point) {
		if (enablePopup) {
			createPopupMenu().show(textComponent, point.x, point.y);
		}
	}

	//
	// com.borland.dx.dataset.ColumnChangeListener
	//
	/*
	 * public void validate(DataSet dataSet, Column column, Variant value) {} public void changed(DataSet dataSet, Column column, Variant value) { if
	 * (!ignoreURLChange) { if (textComponent instanceof JEditorPane && !(textComponent instanceof JTextPane)) { try { ((JEditorPane)
	 * textComponent).setPage(value.getString()); } catch (Exception ex) { DBExceptionHandler.handleException(dataSet, textComponent, ex); } } } ignoreURLChange =
	 * false; }
	 */

	//
	// javax.beans.PropertyChangeListener
	//
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals("ancestor")) {
			bindColumnProperties();
		}
		if (e.getPropertyName().equals("document")) {
			setDocument((Document) e.getNewValue());
		}
	}

	//
	// javax.swing.event.DocumentListener
	//
	public void insertUpdate(DocumentEvent e) {
		textModified(e);
	}

	public void removeUpdate(DocumentEvent e) {
		textModified(e);
	}

	public void changedUpdate(DocumentEvent e) {
		textModified(e);
	}

	private void textModified(DocumentEvent e) {
		if (!ignoreValueChange && !isTextModified() && columnAwareSupport.isValidDataSetState()) {
			ignoreValueChange = true;
			try {
				// touch the field so we're guaranteed to get a postRow notification later
				columnAwareSupport.dataSet.startEdit(columnAwareSupport.getColumn());
			} catch (DataSetException ex) {
				DBExceptionHandler.handleException(columnAwareSupport.dataSet, textComponent, ex);
			}
			if (customPaintSite != null) {
				customPaintSite.fireEditing(textComponent, this, columnPaintListener);
			}
			ignoreValueChange = false;
			setTextModified(true);
		}
	}

	//
	// java.awt.event.FocusListener
	//
	public void focusGained(FocusEvent e) {
		updateUndoRedoMenu();

		CLEARALL_ACTION.setEnabled(textComponent.isEditable());
		CUT_ACTION.setEnabled(textComponent.isEditable());
		PASTE_ACTION.setEnabled(textComponent.isEditable());
		FONTDIALOG_ACTION.setEnabled(((FontDialogAction) FONTDIALOG_ACTION).isActionAvailable(textComponent));
		FOREGROUNDCOLOR_ACTION.setEnabled(((ForegroundColorDialogAction) FOREGROUNDCOLOR_ACTION).isActionAvailable(textComponent));
		BACKGROUNDCOLOR_ACTION.setEnabled(((BackgroundColorDialogAction) BACKGROUNDCOLOR_ACTION).isActionAvailable(textComponent));
		LOADURL_ACTION.setEnabled(((LoadURLAction) LOADURL_ACTION).isActionAvailable(textComponent));
		LOADFILE_ACTION.setEnabled(((LoadFileAction) LOADFILE_ACTION).isActionAvailable(textComponent));
		SAVEFILE_ACTION.setEnabled(((SaveFileAction) SAVEFILE_ACTION).isActionAvailable(textComponent));

		focusedUndoManager = undoManager;
		focusedDataBinder = this;
		DBUtilities.updateCurrentDataSet(textComponent, columnAwareSupport.dataSet);
	}

	public void focusLost(FocusEvent e) {
		if (postOnFocusLost && isTextModified()) {
			focusLost = true;
			postText();
			focusLost = false;
		}
	}

	//
	// java.awt.event.MouseListener
	//
	public void mouseClicked(MouseEvent e) {
		if (ems != null && textComponent != null) {
			if (displayMode) {
				state = ems.prepare(columnAwareSupport.getVariant());
				updateDisplay(true);
				textComponent.setCaretPosition(0);
				textComponent.moveCaretPosition(document.getLength());
				ems.move(state, KeyEvent.VK_END);
				displayMode = false;
				return;
			}
			state.cursorPos = textComponent.viewToModel(e.getPoint());
			// System.out.println("DBTextDataBinder.mouseClicked() propose cursor at " + state.cursorPos);
			// ems.move(state, MouseEvent.MOUSE_CLICKED);
			// System.out.println("DBTextDataBinder.mouseClicked() final cursor at " + state.cursorPos);
			// if (displayMode) {
			// updateDisplay();
			// displayMode = false;
			// }
			// else {
			updateSelection();
			// }
		}

	}

	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
			textComponent.requestFocus();
			focusGained(null);
			displayPopup(e.getPoint());
		}
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			textComponent.requestFocus();
			focusGained(null);
			displayPopup(e.getPoint());
		}
		// else {
		// if (ems != null) {
		// updateSelection();
		// }
		// }
	}

	public void keyTyped(KeyEvent e) {
		if (ems != null) {
			char ch = e.getKeyChar();
			if (ch >= 0 && ch < ' ') {
				if (ch == '\b') {
					e.consume();
				}
				return;
			}
			if (displayMode) {
				state = ems.prepare(null);
				ems.move(state, KeyEvent.VK_HOME);
				updateDisplay(true);
				displayMode = false;
			} else {
				deleteSelection(true);
			}
			ems.insert(state, ch);
			updateDisplay(false);
			e.consume();
		}
	}

	private void deleteSelection(boolean preserveAtCursor) {
		int selStart = textComponent.getSelectionStart();
		int selEnd = textComponent.getSelectionEnd();
		int nChars = selEnd - selStart;
		if (nChars > 1 || !preserveAtCursor) {
			ems.delete(state, selStart, nChars);
		}
	}

	private void updateSelection() {
		// if (state != null) {
		// Diagnostic.trace(Trace.MaskableEditor, "updateSelection(" + state.cursorPos + ")");
		// Diagnostic.trace(Trace.MaskableEditor, " buffer is " + state.displayString.length() + " and text is " + getText().length());
		// if (allSelected) {
		// textComponent.setCaretPosition(0);
		// textComponent.moveCaretPosition(state.displayString.toString().length());
		// textComponent.setCaretPosition(state.displayString.toString().length());
		// }
		// else {
		textComponent.setCaretPosition(state.cursorPos);
		if (state.cursorPos + 1 <= document.getLength()) {
			textComponent.moveCaretPosition(state.cursorPos + 1);
		}
		// }
		// }
	}

	private void updateDisplay(boolean ignore) {
		// if (state != null) {
		// setText(state.displayString.toString());
		if (ignore) {
			ignoreValueChange = true;
		}
		try {
			document.remove(0, document.getLength());
			document.insertString(0, state.displayString.toString(), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		if (ignore) {
			ignoreValueChange = false;
		}
		updateSelection();
		// }
	}

	public void keyPressed(KeyEvent e) {
		if (ems != null) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_HOME:
				case KeyEvent.VK_END:
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_RIGHT:
					endDisplayMode();
					ems.move(state, e.getKeyCode());
					updateSelection();
					break;
				case KeyEvent.VK_BACK_SPACE:
					if (displayMode) {
						endDisplayMode();
						ems.delete(state, state.cursorPos, 1);
						updateDisplay(false);
					} else {
						endDisplayMode();
						if (ems.move(state, KeyEvent.VK_LEFT)) {
							updateSelection();
							deleteSelection(false);
							updateDisplay(false);
						}
					}
					break;
				case KeyEvent.VK_DELETE:
					endDisplayMode();
					updateSelection();
					deleteSelection(false);
					ems.move(state, KeyEvent.VK_RIGHT);
					updateDisplay(false);
					break;
				case KeyEvent.VK_TAB:
				case KeyEvent.VK_ENTER:
					if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
						endDisplayMode();
						e.consume();
					}
					// if (e.getID() != KeyEvent.KEY_TYPED) {
					return;
				// }
				// System.err.println("ENTER ...");
				// Diagnostic.trace(Trace.MaskableEditor, "commit key");
				// canPost();
				// break;
				case KeyEvent.VK_F2:
					if (displayMode) {
						endDisplayMode();
						e.consume();
					}
					break;
				case KeyEvent.VK_ESCAPE:
					// Diagnostic.trace(Trace.MaskableEditor, "esc key");
					// startEdit(startingValue, null, null);
					// changed = false;
					// endDisplayMode();
					updateText();
					break;
			}
			if ((e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V) ||
					(e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_INSERT)) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable content = clipboard.getContents(this);
				if (content != null) {
					try {
						String data = (String) (content.getTransferData(DataFlavor.stringFlavor));
						if (data.length() > 0) {
							deleteSelection(true);
						}
						for (int i = 0, end = data.length(); i < end; i++) {
							ems.insert(state, data.charAt(i));
						}
						updateDisplay(true);
					} catch (Exception ex) {
						Toolkit.getDefaultToolkit().beep();
					}
				}
				e.consume();
				return;
			}
			e.consume();
		}
	}

	private void endDisplayMode() {
		if (displayMode) {
			state = ems.prepare(columnAwareSupport.getVariant());
			ems.move(state, KeyEvent.VK_END);
			updateDisplay(true);
			displayMode = false;
		}
	}

	public void keyReleased(KeyEvent e) {
		// disabled in JDK 1.2 and 1.2.2, since the keyboard can't
		// be used to navigate popup menus
		// if (e.getKeyCode() == KeyEvent.VK_F10 && e.isShiftDown()) {
		// try {
		// Rectangle rect = textComponent.modelToView(textComponent.getCaretPosition());
		// point.x = rect.x;
		// point.y = rect.y;
		// displayPopup(point);
		// }
		// catch (BadLocationException ex) {
		// // should never ever happen
		// DBExceptionHandler.handleException(columnAwareSupport.dataSet, textComponent, ex);
		// }
		// }
	}

	//
	// com.borland.dx.dataset.NavigationListener interface implementation
	//

	public void navigated(NavigationEvent event) {
		if (!dataSetEventsEnabled) { return; }
		updateText();
	}

	//
	// com.borland.dx.dataset.DataChangeListener interface implementation
	//

	public void dataChanged(DataChangeEvent event) {
		if (!dataSetEventsEnabled) { return; }
		// depending upon the event, we might have to update the current
		// field value.
		int affectedRow = event.getRowAffected();
		boolean affectedOurRow = (affectedRow == columnAwareSupport.dataSet.getRow()) ||
				affectedRow == -1;
		if (affectedOurRow && !ignoreValueChange) {
			updateText();
		}
	}

	public void postRow(DataChangeEvent event) throws Exception {
		if (postOnRowPosted) {
			try {
				postText2();
			} catch (Exception e) {
				if (textComponent != null) {
					textComponent.requestFocus();
				}
				throw e;
			}
		}
	}

	// returns true if the property has not been explicitly overridden
	// true if and only if the object is an instance of UIResource
	private boolean isDefaultProperty(Object property) {
		return (property == null) || (property instanceof UIResource);
	}

	// binds alignment, font, foreground, and background properties from column
	// if not explicitly set on text component
	private void bindColumnProperties() {
		if (oldTextComponent != null) {
			oldTextComponent = null;
		}

		if (textComponent != null && textComponent.isDisplayable()) {
			// ensures that if lazyOpen has to reopen the DataSet, we won't call bindColumnProperties
			// again on the OPEN access event
			rebindColumnProperties = false;
			// will resync our internal state if the dataSet or column has changed
			columnAwareSupport.lazyOpen();

			updateText();

			if (columnAwareSupport.isValidDataSetState()) {
				oldTextComponent = textComponent;

				Column column = columnAwareSupport.getColumn();
				if (textComponent instanceof JTextField && ((JTextField) textComponent).getHorizontalAlignment() == (DBUtilities.is1pt3() ? JTextField.LEADING
						: JTextField.LEFT)) {
					((JTextField) textComponent).setHorizontalAlignment(DBUtilities.convertJBCLToSwingAlignment(column.getAlignment(), true));
				}
				if (isDefaultProperty(textComponent.getBackground())) {
					if (column.getBackground() != null) {
						textComponent.setBackground(column.getBackground());
					}
				}
				if (isDefaultProperty(textComponent.getForeground())) {
					if (column.getForeground() != null) {
						textComponent.setForeground(column.getForeground());
					}
				}
				if (isDefaultProperty(textComponent.getFont())) {
					if (column.getFont() != null) {
						textComponent.setFont(column.getFont());
					}
				}

				columnPaintListener = column.getColumnPaintListener();
				if (columnPaintListener != null) {
					if (customPaintSite == null) {
						customPaintSite = new DBCustomPaintSite(textComponent);
						updateText();
					}
				}

				if (column.getDataType() == Variant.STRING && textComponent != null &&
						(textComponent instanceof JTextField || textComponent instanceof JTextArea)) {
					if (!(textComponent.getDocument() instanceof DBPlainDocument)) {
						try {
							String content = textComponent.getDocument().getText(0, textComponent.getDocument().getLength());
							ignoreValueChange = true;
							textComponent.setDocument(new DBPlainDocument());
							textComponent.getDocument().insertString(0, content, null);
							ignoreValueChange = false;
						} catch (BadLocationException e) {
							DBExceptionHandler.handleException(columnAwareSupport.dataSet, textComponent, e);
							textComponent.setDocument(new DBPlainDocument());
						}
					}
					DBPlainDocument document = (DBPlainDocument) textComponent.getDocument();
					if (column.getPrecision() > 0 && document.getMaxLength() != column.getPrecision()) {
						document.setMaxLength(column.getPrecision());
					}

					if (column.getEditMask() != null && textComponent instanceof JTextField) {
						ems = column.getEditMasker();
					}
					updateText();
				} else {
					if (column.getEditMask() != null && textComponent instanceof JTextField) {
						ems = column.getEditMasker();
						updateText();
					}
				}

				if (textComponent.isEditable() && !column.isEditable()) {
					textComponent.setEditable(false);
				}
			}
		}
	}

	//
	// com.borland.dx.dataset.AccessListener interface implementation
	//

	public void accessChange(AccessEvent event) {
		if (event.getID() == AccessEvent.CLOSE) {
			if (event.getReason() == AccessEvent.STRUCTURE_CHANGE) {
				dataSetEventsEnabled = false;
			}
			if (event.getReason() == AccessEvent.PROPERTY_CHANGE) {
				rebindColumnProperties = true;
			}
		} else {
			dataSetEventsEnabled = true;
			if (event.getReason() == AccessEvent.UNSPECIFIED || rebindColumnProperties || event.getReason() == AccessEvent.DATA_CHANGE) {
				bindColumnProperties();
			}
		}
	}

	//
	// javax.swing.undo.UndoableEditListener
	//
	public void undoableEditHappened(UndoableEditEvent e) {
		if (document instanceof HTMLDocument &&
				(textComponent != null && !textComponent.isEditable())) { return; }
		undoManager.addEdit(e.getEdit());
		updateUndoRedoMenu();
	}

	void updateUndoRedoMenu() {
		UNDO_ACTION.setEnabled(undoManager.canUndo() && textComponent.isEditable());
		REDO_ACTION.setEnabled(undoManager.canRedo() && textComponent.isEditable());
	}

	public static class UndoAction extends DBTextAction {
		public UndoAction() {
			super(undoAction);
			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("image/undo.gif")));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			DBTextDataBinder dataBinder = getDBTextDataBinder(e);
			if (dataBinder != null) {
				try {
					if (dataBinder.focusedUndoManager.canUndo()) {
						dataBinder.focusedUndoManager.undo();
						dataBinder.UNDO_ACTION.setEnabled(dataBinder.focusedUndoManager.canUndo());
						dataBinder.REDO_ACTION.setEnabled(dataBinder.focusedUndoManager.canRedo());
					}
				} catch (CannotUndoException ex) {
					target.getToolkit().beep();
				}
			}
		}

	}

	public static class RedoAction extends DBTextAction {
		public RedoAction() {
			super(redoAction);
			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("image/redo.gif")));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			DBTextDataBinder dataBinder = getDBTextDataBinder(e);
			if (dataBinder != null) {
				try {
					if (dataBinder.focusedUndoManager.canRedo()) {
						dataBinder.focusedUndoManager.redo();
						dataBinder.UNDO_ACTION.setEnabled(dataBinder.focusedUndoManager.canUndo());
						dataBinder.REDO_ACTION.setEnabled(dataBinder.focusedUndoManager.canRedo());
					}
				} catch (CannotRedoException ex) {
					target.getToolkit().beep();
				}
			}
		}
	}

	//
	// javax.swing.event.HyperlinkListener interface implementation
	//
	public void hyperlinkUpdate(HyperlinkEvent e) {

		if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
			((JEditorPane) textComponent).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
			((JEditorPane) textComponent).setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				URL currentPage = ((JEditorPane) textComponent).getPage();
				Frame frame = DBUtilities.getFrame(textComponent);
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				try {
					if (columnNameURL != null && e.getDescription().charAt(0) != '#') {
						if (locateRow == null || (locateRow.hasColumn(columnNameURL) == null)) {
							locateRow = new DataRow(columnAwareSupport.dataSet, columnNameURL);
						}
						locateRow.setString(columnNameURL, e.getURL().toString());
						if (!columnAwareSupport.dataSet.locate(locateRow, Locate.FIRST)) {
							if (enableURLCache && columnAwareSupport.dataSet.isEnableInsert()) {
								columnAwareSupport.dataSet.insertRow(false);
							}
							((JEditorPane) textComponent).setPage(e.getURL());
							ignoreURLChange = true;
							columnAwareSupport.dataSet.setString(columnNameURL, e.getURL().toString());
							ignoreURLChange = false;
						} else {
							if (columnAwareSupport.dataSet.isNull(columnAwareSupport.columnName)) {
								((JEditorPane) textComponent).setPage(e.getURL());
							}
						}
					} else {
						((JEditorPane) textComponent).setPage(e.getURL());
					}
				} catch (Exception ex) {
					DBExceptionHandler.handleException(columnAwareSupport.dataSet, textComponent, ex);
				}
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}

	//
	// Actions
	//
	/**
	 * <p>
	 * The name of an action that undoes the previous action.
	 * </p>
	 */

	public static final String undoAction = "undo";

	/**
	 * <p>
	 * The name of the action that redoes the previously undone action.
	 * </p>
	 */
	public static final String redoAction = "redo";

	/**
	 * <p>
	 * The name of the action that clears the data displayed in the component.
	 * </p>
	 */
	public static final String clearAllAction = "clear-all";

	/**
	 * <p>
	 * The name of the action that selects all the text in the component.
	 * </p>
	 */
	public static final String selectAllAction = "select-all";

	/**
	 * <p>
	 * The name of the action that loads a URL into the component.
	 * </p>
	 */
	public static final String loadURLAction = "load-URL";

	/**
	 * <p>
	 * The name of the action that loads a file into the component.
	 * </p>
	 */
	public static final String loadFileAction = "load-file";

	/**
	 * <p>
	 * The name of the action that saves the content of the component to a file.
	 * </p>
	 */
	public static final String saveFileAction = "save-file";

	/**
	 * <p>
	 * The name of the action that displays a font dialog box so the user can select a font for the component.
	 * </p>
	 */
	public static final String fontDialogAction = "font-dialog";

	/**
	 * <p>
	 * The name of the action that displays a color dialog box so the user can select a foreground color for the component.
	 * </p>
	 */
	public static final String foregroundColorDialogAction = "foreground-color-dialog";

	/**
	 * <p>
	 * The name of the action that displays a color dialog box so the user can select a background color for the component.
	 * </p>
	 */
	public static final String backgroundColorDialogAction = "background-color-dialog";

	/**
	 * <p>
	 * The name of the action that navigates to the next row in the <code>DataSet</code> the component is bound to.
	 * </p>
	 */
	public static final String nextRowAction = "next-row";

	/**
	 * <p>
	 * The name of the action that navigates to the previous row in the <code>DataSet</code> the component is bound to.
	 * </p>
	 */
	public static final String priorRowAction = "prior-row";

	/**
	 * <p>
	 * The name of the action that posts text to the <code>DataSet</code>.
	 * </p>
	 */
	public static final String postDataAction = "post-data";

	/**
	 * <p>
	 * The name of the action that cancels a posting of the text to the <code>DataSet</code>.
	 * </p>
	 */
	public static final String cancelPostAction = "cancel-post";

	/**
	 * <p>
	 * The name of the action that inserts a row.
	 * </p>
	 */
	public static final String insertRowAction = "insert-row";

	/**
	 * <p>
	 * The name of the action that deletes the current row.
	 * </p>
	 */
	public static final String deleteRowAction = "delete-row";

	private transient JTextComponent.KeyBinding[] fieldKeyBindings = {
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0, true), nextRowAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0, true), priorRowAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), postDataAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), cancelPostAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, Event.CTRL_MASK, true), insertRowAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, Event.CTRL_MASK, true), deleteRowAction),
	};

	private transient JTextComponent.KeyBinding[] areaKeyBindings = {
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, Event.CTRL_MASK, true), nextRowAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, Event.CTRL_MASK, true), priorRowAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Event.CTRL_MASK, true), postDataAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), cancelPostAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, Event.CTRL_MASK, true), insertRowAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, Event.CTRL_MASK, true), deleteRowAction),
	};

	private Action[] textDataBinderActions = {
			new NextRowAction(),
			new PriorRowAction(),
			new PostDataAction(),
			new CancelPostAction(),
			new InsertRowAction(),
			new DeleteRowAction(),
	};

	public static class ClearAllAction extends TextAction {
		public ClearAllAction() {
			super(clearAllAction);
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			JTextComponent target = getTextComponent(e);
			if (target != null && target.isEditable()) {
				try {
					target.getDocument().remove(0, target.getDocument().getLength());
				} catch (BadLocationException ex) {
					// if this happens, then something is wrong internally with
					// the document, and we're in REAL SERIOUS trouble.
					DBExceptionHandler.handleException(null, target, ex);
				}
			}
		}
	}

	public static class SelectAllAction extends TextAction {
		public SelectAllAction() {
			super(selectAllAction);
		}

		public void actionPerformed(ActionEvent e) {
			JTextComponent target = getTextComponent(e);
			if (target != null) {
				target.setCaretPosition(0);
				target.moveCaretPosition(target.getDocument().getLength());
			}
		}
	}

	public static class LoadURLAction extends DBTextAction {
		public LoadURLAction() {
			super(loadURLAction);
			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("image/loadURL.gif")));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			// has the side effect of setting target in DBTextAction to the JTextComponent
			DBTextDataBinder dataBinder = getDBTextDataBinder(e);
			// only allow use on JEditorPane
			if (isActionAvailable(target)) {
				// make sure the dialog's entry field is wide enough
				String promptString = Res._URLDialogPrompt;
				StringBuffer widePrompt = new StringBuffer(java.lang.Math.max(60, promptString.length()));
				widePrompt.append(promptString);
				for (int i = widePrompt.length(); i < widePrompt.capacity(); i++) {
					widePrompt.append(' ');
				}
				String url = JOptionPane.showInputDialog(	target,
																									widePrompt.toString(),
																									Res._URLDialogTitle,
																									JOptionPane.QUESTION_MESSAGE);
				if (url == null) { return; }
				Frame frame = DBUtilities.getFrame(target);
				if (frame != null) {
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				}
				try {
					((JEditorPane) target).setPage(url);
					if (dataBinder != null && dataBinder.columnAwareSupport.dataSet != null) {
						if (dataBinder.columnNameURL != null &&
								dataBinder.columnAwareSupport.dataSet.hasColumn(dataBinder.columnNameURL) != null) {
							// avoid auto page reload when URL changes
							dataBinder.ignoreURLChange = true;
							dataBinder.columnAwareSupport.dataSet.setString(dataBinder.columnNameURL, url);
							dataBinder.ignoreURLChange = false;
						}
					}
				} catch (Exception ex) {
					DBExceptionHandler.handleException(null, target, ex);
				}
				if (frame != null) {
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		}

		public boolean isActionAvailable(JTextComponent target) {
			return target != null && target instanceof JEditorPane && !(target instanceof JTextPane);
		}
	}

	public static class LoadFileAction extends TextAction {
		public LoadFileAction() {
			super(loadFileAction);
			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("image/loadFile.gif")));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			JTextComponent target = getTextComponent(e);
			// only allow use on JTextArea, JTextPane, or JEditorPane
			if (isActionAvailable(target)) {
				JFileChooser fileChooser = null;

				if (target instanceof JTextArea) {
					fileChooser = createFileChooser(new javax.swing.filechooser.FileFilter[] { TextFileFilter });
					fileChooser.setDialogTitle(Res._OpenTextTitle);
				} else if (target instanceof JTextPane) {
					fileChooser = createFileChooser(new javax.swing.filechooser.FileFilter[] { TextFileFilter, SerFileFilter });
					fileChooser.setDialogTitle(Res._OpenTextorSerTitle);
				} else if (target instanceof JEditorPane) {
					fileChooser = createFileChooser(new javax.swing.filechooser.FileFilter[] { TextFileFilter, HTMLFileFilter, RTFFileFilter, SerFileFilter });
					fileChooser.setDialogTitle(Res._OpenTextorHTMLorRTForSerTitle);
				}

				if (fileChooser.showOpenDialog(target) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					if (file != null && file.exists()) {
						Frame frame = DBUtilities.getFrame(target);
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

						if (target instanceof JTextArea || target instanceof JTextPane) {
							if (target instanceof JTextPane && file.getName().toUpperCase().endsWith("SER")) {
								try {
									ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
									Object object = objectInputStream.readObject();
									if (object instanceof StyledDocument) {
										((JTextPane) target).setStyledDocument((StyledDocument) object);
									} else {
										JOptionPane.showMessageDialog(target,
																									Res._InvalidSerFile,
																									"",
																									JOptionPane.ERROR_MESSAGE);
									}
									objectInputStream.close();
								} catch (Exception ex) {
									// IOException or FileNotFoundException
									DBExceptionHandler.handleException(null, target, ex);
								}
							} else {
								try {
									Reader reader = new BufferedReader(new FileReader(file));
									// target.read(reader, file.getCanonicalPath());
									JTextField temp = new JTextField();
									temp.read(reader, file.getCanonicalPath());
									try {
										String content = temp.getDocument().getText(0, temp.getDocument().getLength());
										target.getDocument().remove(0, target.getDocument().getLength());
										target.getDocument().insertString(0, content, null);
									} catch (BadLocationException ex) {
										DBExceptionHandler.handleException(ex);
									}
									reader.close();
								} catch (Exception ex) {
									// IOException or FileNotFoundException
									DBExceptionHandler.handleException(null, target, ex);
								}
							}
						} else { // JEditorPane
							if (file.getName().toUpperCase().endsWith("SER")) {
								try {
									ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
									Object object = objectInputStream.readObject();
									if (object instanceof HTMLDocument) {
										((JEditorPane) target).setContentType("text/html");
										target.setDocument((Document) object);
									} else if (object instanceof PlainDocument ||
											object instanceof StyledDocument) {
										((JEditorPane) target).setContentType("text/plain");
										target.setDocument((Document) object);
									} else {
										JOptionPane.showMessageDialog(target,
																									Res._InvalidSerFile,
																									null,
																									JOptionPane.INFORMATION_MESSAGE);
									}
									objectInputStream.close();
								} catch (Exception ex) {
									// IOException or FileNotFoundException
									DBExceptionHandler.handleException(null, target, ex);
								}
							} else {
								try {
									InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
									String type = URLConnection.guessContentTypeFromStream(inputStream);
									// many thanks to Kim Topley, author of "CORE Java Foundation Classes" for
									// the following code! (dcy)
									if (type == null) {
										inputStream.mark(5);
										int c1 = inputStream.read();
										int c2 = inputStream.read();
										int c3 = inputStream.read();
										int c4 = inputStream.read();
										int c5 = inputStream.read();
										inputStream.reset();
										if (c1 == '{' && c2 == '\\' && c3 == 'r' && c4 == 't' && c5 == 'f') {
											type = "text/rtf";
										} else {
											type = "text/plain";
										}
									}
									((JEditorPane) target).setContentType(type);
									Document document = ((JEditorPane) target).getEditorKit().createDefaultDocument();
									ignoreModelChange = true;
									target.setDocument(document);
									ignoreModelChange = false;
									if (type.equals("text/rtf")) {
										((JEditorPane) target).getEditorKit().read(inputStream, document, 0);
									} else {
										try {
											Reader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream));
											document.putProperty("IgnoreCharsetDirective", new Boolean(true));
											((JEditorPane) target).getEditorKit().read(inputStreamReader, document, 0);
											// ((JEditorPane) target).read(inputStreamReader, document);
											inputStreamReader.close();
										} catch (Exception ex) {
											// IOException or FileNotFoundException
											DBExceptionHandler.handleException(null, target, ex);
										}
									}
									inputStream.close();
								} catch (Exception ex) {
									// IOException or FileNotFoundException
									DBExceptionHandler.handleException(null, target, ex);
								}
							}
						}
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					} else {
						JOptionPane.showMessageDialog(target,
																					Res._FileNotExist,
																					"",
																					JOptionPane.ERROR_MESSAGE);
					}
				}
				target.repaint();
			}
		}

		public boolean isActionAvailable(JTextComponent target) {
			return target != null && target.isEditable() && (target instanceof JTextArea) || (target instanceof JEditorPane);
		}
	}

	public static class SaveFileAction extends TextAction {
		public SaveFileAction() {
			super(saveFileAction);
			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("image/saveFile.gif")));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			JTextComponent target = getTextComponent(e);
			// only allow use on JTextArea, JTextPane, or JEditorPane
			if (isActionAvailable(target)) {
				JFileChooser fileChooser = null;
				String contentType = "text/plain";

				if (target instanceof JTextArea) {
					fileChooser = createFileChooser(new javax.swing.filechooser.FileFilter[] { TextFileFilter });
					fileChooser.setDialogTitle(Res._SaveTextTitle);
				} else if (target instanceof JTextPane) {
					fileChooser = createFileChooser(new javax.swing.filechooser.FileFilter[] { TextFileFilter, SerFileFilter });
					fileChooser.setDialogTitle(Res._SaveTextorSerTitle);
				} else if (target instanceof JEditorPane) {
					contentType = ((JEditorPane) target).getContentType();
					if (contentType.equals("text/rtf")) {
						fileChooser = createFileChooser(new javax.swing.filechooser.FileFilter[] { TextFileFilter, RTFFileFilter, SerFileFilter });
						fileChooser.setDialogTitle(Res._SaveTextorRTForSerTitle);
					} else if (contentType.equals("text/html")) {
						fileChooser = createFileChooser(new javax.swing.filechooser.FileFilter[] { TextFileFilter, HTMLFileFilter, SerFileFilter });
						fileChooser.setDialogTitle(Res._SaveTextorHTMLorSerTitle);
					} else { // assume text/plain
						fileChooser = createFileChooser(new javax.swing.filechooser.FileFilter[] { TextFileFilter });
						fileChooser.setDialogTitle(Res._SaveTextTitle);
					}
				}

				if (fileChooser.showSaveDialog(target) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					if (file != null) {
						if (file.exists()) {
							int response = JOptionPane.showConfirmDialog(	target,
																														Res._OverwriteFile,
																														"",
																														JOptionPane.OK_CANCEL_OPTION);
							if (response != JOptionPane.OK_OPTION) { return; }
						}
						Frame frame = DBUtilities.getFrame(target);
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						if (file.getName().toUpperCase().endsWith("SER")) {
							try {
								ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
								// have to temporarily detach ourself as a Document listener, since JTextComponent serializes
								// its document listeners
								if (DBTextDataBinder.focusedDataBinder != null && DBTextDataBinder.focusedDataBinder.getJTextComponent() == target) {
									DBTextDataBinder.focusedDataBinder.bindDocument(target.getDocument(), false);
								}
								objectOutputStream.writeObject(target.getDocument());
								if (DBTextDataBinder.focusedDataBinder != null && DBTextDataBinder.focusedDataBinder.getJTextComponent() == target) {
									DBTextDataBinder.focusedDataBinder.bindDocument(target.getDocument(), true);
								}
								objectOutputStream.flush();
								objectOutputStream.close();
							} catch (Exception ex) {
								// IOException or FileNotFoundException
								DBExceptionHandler.handleException(null, target, ex);
							}
						} else {
							if (contentType.equals("text/rtf") && !file.getName().toUpperCase().endsWith("TXT")) {
								// have to use an OutputStream for 8-bit RTF, which should only be loadable into a JEditorPane
								try {
									OutputStream outputStream = new FileOutputStream(file);
									((JEditorPane) target).getEditorKit().write(outputStream, target.getDocument(), 0, target.getDocument().getLength());
									outputStream.flush();
									outputStream.close();
								} catch (Exception ex) {
									// IOException or FileNotFoundException
									DBExceptionHandler.handleException(null, target, ex);
								}
							} else { // either text/plain or text/html, or text/rtf forced to plain text. Either way, we should use a Writer
								try {
									Writer writer = new FileWriter(file);
									if (file.getName().toUpperCase().endsWith("TXT")) {
										writer.write(target.getDocument().getText(0, target.getDocument().getLength()));
									} else {
										target.write(writer);
									}
									writer.flush();
									writer.close();
								} catch (Exception ex) {
									// IOException or FileNotFoundException
									DBExceptionHandler.handleException(null, target, ex);
								}
							}
						}
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
					target.repaint();
				}
			}
		}

		public boolean isActionAvailable(JTextComponent target) {
			return target != null && (target instanceof JTextArea) || (target instanceof JEditorPane);
		}
	}

	public static class NextRowAction extends DBTextAction {
		public NextRowAction() {
			super(nextRowAction);
			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("image/next.gif")));
		}

		public void actionPerformed(ActionEvent e) {
			DBTextDataBinder dataBinder = getDBTextDataBinder(e);
			if (dataBinder != null && dataBinder.columnAwareSupport.dataSet != null) {
				try {
					dataBinder.columnAwareSupport.dataSet.next();
				} catch (DataSetException ex) {
					DBExceptionHandler.handleException(dataBinder.columnAwareSupport.dataSet, target, ex);
				}
			}
		}
	}

	public static class PriorRowAction extends DBTextAction {
		public PriorRowAction() {
			super(priorRowAction);
			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("image/prior.gif")));
		}

		public void actionPerformed(ActionEvent e) {
			DBTextDataBinder dataBinder = getDBTextDataBinder(e);
			if (dataBinder != null && dataBinder.columnAwareSupport.dataSet != null) {
				try {
					dataBinder.columnAwareSupport.dataSet.prior();
				} catch (DataSetException ex) {
					DBExceptionHandler.handleException(dataBinder.columnAwareSupport.dataSet, target, ex);
				}
			}
		}
	}

	public static class PostDataAction extends DBTextAction {
		public PostDataAction() {
			super(postDataAction);
			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("image/post.gif")));
		}

		public void actionPerformed(ActionEvent e) {
			DBTextDataBinder dataBinder = getDBTextDataBinder(e);
			if (dataBinder != null) {
				try {
					dataBinder.postText2();
					if (dataBinder.isNextFocusOnEnter() &&
							target instanceof JTextField) {
						javax.swing.FocusManager.getCurrentManager().focusNextComponent(target);
					}
				} catch (Exception ex) {
					// display error
					DBExceptionHandler.handleException(dataBinder.columnAwareSupport.dataSet, target, ex);
				}
			}
		}
	}

	public static class CancelPostAction extends DBTextAction {
		public CancelPostAction() {
			super(cancelPostAction);
			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("image/cancel.gif")));
		}

		public void actionPerformed(ActionEvent e) {
			DBTextDataBinder dataBinder = getDBTextDataBinder(e);
			if (dataBinder != null && dataBinder.isTextModified()) {
				dataBinder.updateText();
			}
			if (target != null) {
				target.setCaretPosition(0);
				target.moveCaretPosition(target.getDocument().getLength());
			}
		}
	}

	public static class InsertRowAction extends DBTextAction {
		public InsertRowAction() {
			super(insertRowAction);
			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("image/insert.gif")));
		}

		public void actionPerformed(ActionEvent e) {
			DBTextDataBinder dataBinder = getDBTextDataBinder(e);
			DataSet dataSet;
			if (dataBinder != null && (dataSet = dataBinder.columnAwareSupport.dataSet) != null &&
					dataSet.isEditable() && dataSet.isEnableInsert() && !dataSet.isEditingNewRow()) {
				try {
					dataSet.insertRow(true);
				} catch (DataSetException ex) {
					DBExceptionHandler.handleException(dataSet, target, ex);
				}
			}
		}
	}

	public static class DeleteRowAction extends DBTextAction {
		public DeleteRowAction() {
			super(deleteRowAction);
			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("image/delete.gif")));
		}

		public void actionPerformed(ActionEvent e) {
			DBTextDataBinder dataBinder = getDBTextDataBinder(e);
			DataSet dataSet;
			if (dataBinder != null && (dataSet = dataBinder.columnAwareSupport.dataSet) != null &&
					dataSet.isEditable() && dataSet.isEnableDelete()) {
				boolean readOnly = false;
				if (dataSet instanceof StorageDataSet) {
					readOnly = ((StorageDataSet) dataSet).isReadOnly();
				}
				if (!readOnly) {
					try {
						if (!dataSet.isEmpty()) {
							dataSet.deleteRow();
						}
					} catch (DataSetException ex) {
						DBExceptionHandler.handleException(dataSet, target, ex);
					}
				}
			}
		}
	}

	public static class FontDialogAction extends TextAction {
		FontChooser fontChooser = new FontChooser();

		public FontDialogAction() {
			super(fontDialogAction);
			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("image/font.gif")));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			JTextComponent target = getTextComponent(e);
			if (isActionAvailable(target)) {
				fontChooser.setFrame(DBUtilities.getFrame(target));
				int selStart = target.getSelectionStart();
				int selEnd = target.getSelectionEnd();
				if (fontChooser.showDialog()) {
					Font selectedFont = fontChooser.getSelectedFont();
					MutableAttributeSet attr = new SimpleAttributeSet();
					StyleConstants.setFontFamily(attr, selectedFont.getFontName());
					StyleConstants.setFontSize(attr, selectedFont.getSize());
					if (selStart != selEnd) {
						StyledDocument doc = (StyledDocument) target.getDocument();
						doc.setCharacterAttributes(selStart, selEnd - selStart, attr, false);
					} else {
						if (target instanceof JEditorPane) {
							EditorKit editorKit = ((JEditorPane) target).getEditorKit();
							if (editorKit instanceof StyledEditorKit) {
								MutableAttributeSet inputAttributes = ((StyledEditorKit) editorKit).getInputAttributes();
								inputAttributes.addAttributes(attr);
							}
						}
					}
				}
			}
		}

		public boolean isActionAvailable(JTextComponent target) {
			return target != null && target.isEditable() && target.getDocument() instanceof StyledDocument;
		}
	}

	public static class ForegroundColorDialogAction extends TextAction {
		public ForegroundColorDialogAction() {
			super(foregroundColorDialogAction);
			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("image/fgcolor.gif")));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			JTextComponent target = getTextComponent(e);
			if (isActionAvailable(target)) {
				Color selectedColor = null;
				if ((selectedColor = JColorChooser.showDialog(target, Res._SelectColor, null)) != null) {
					MutableAttributeSet attr = new SimpleAttributeSet();
					StyleConstants.setForeground(attr, selectedColor);
					int selStart = target.getSelectionStart();
					int selEnd = target.getSelectionEnd();
					if (selStart != selEnd) {
						StyledDocument doc = (StyledDocument) target.getDocument();
						doc.setCharacterAttributes(selStart, selEnd - selStart, attr, false);
					} else {
						if (target instanceof JEditorPane) {
							EditorKit editorKit = ((JEditorPane) target).getEditorKit();
							if (editorKit instanceof StyledEditorKit) {
								MutableAttributeSet inputAttributes = ((StyledEditorKit) editorKit).getInputAttributes();
								inputAttributes.addAttributes(attr);
							}
						}
					}
				}
			}
		}

		public boolean isActionAvailable(JTextComponent target) {
			return target != null && target.isEditable() && target.getDocument() instanceof StyledDocument;
		}
	}

	public static class BackgroundColorDialogAction extends TextAction {
		public BackgroundColorDialogAction() {
			super(backgroundColorDialogAction);
			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("image/bgcolor.gif")));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			JTextComponent target = getTextComponent(e);
			if (isActionAvailable(target)) {
				Color selectedColor = null;
				if ((selectedColor = JColorChooser.showDialog(target, Res._SelectColor, null)) != null) {
					MutableAttributeSet attr = new SimpleAttributeSet();
					StyleConstants.setBackground(attr, selectedColor);
					int selStart = target.getSelectionStart();
					int selEnd = target.getSelectionEnd();
					if (selStart != selEnd) {
						StyledDocument doc = (StyledDocument) target.getDocument();
						doc.setCharacterAttributes(selStart, selEnd - selStart, attr, false);
					} else {
						if (target instanceof JEditorPane) {
							EditorKit editorKit = ((JEditorPane) target).getEditorKit();
							if (editorKit instanceof StyledEditorKit) {
								MutableAttributeSet inputAttributes = ((StyledEditorKit) editorKit).getInputAttributes();
								inputAttributes.addAttributes(attr);
							}
						}
					}
				}
			}
		}

		public boolean isActionAvailable(JTextComponent target) {
			return target != null && target.isEditable() && target.getDocument() instanceof StyledDocument;
		}
	}

	abstract static class DBTextAction extends TextAction {
		JTextComponent target;

		public DBTextAction(String actionName) {
			super(actionName);
		}

		protected final DBTextDataBinder getDBTextDataBinder(ActionEvent e) {
			target = getTextComponent(e);
			if (DBTextDataBinder.focusedDataBinder != null &&
					DBTextDataBinder.focusedDataBinder.getJTextComponent() == target) {
				return DBTextDataBinder.focusedDataBinder;
			}

			if (target != null) {
				if (target instanceof JdbTextField) {
					return ((JdbTextField) target).getDataBinder();
				} else if (target instanceof JdbTextArea) {
					return ((JdbTextArea) target).getDataBinder();
				} else if (target instanceof JdbTextPane) {
					return ((JdbTextPane) target).getDataBinder();
				} else if (target instanceof JdbEditorPane) { return ((JdbEditorPane) target).getDataBinder(); }
			}

			return null;
		}
	}

	// ColumnPaintListener support class. Need not be deployed if ColumnPaintListener is not used.
	static class DBCustomPaintSite implements CustomPaintSite {
		private Color background;
		private Color foreground;
		private Font font;
		private int alignment;
		private int hAlignment;
		private Insets margin;
		private Color defaultForeground;
		private Color defaultBackground;
		private Font defaultFont;
		private int defaultAlignment;
		private Insets defaultMargins;
		private JTextComponent textComponent;

		public DBCustomPaintSite(JTextComponent textComponent) {
			initDefaults(textComponent);
		}

		void initDefaults(JTextComponent textComponent) {
			background = defaultBackground = textComponent.getBackground();
			foreground = defaultForeground = textComponent.getForeground();
			font = defaultFont = textComponent.getFont();
			if (textComponent instanceof JTextField) {
				hAlignment = defaultAlignment = ((JTextField) textComponent).getHorizontalAlignment();
			}
			margin = defaultMargins = textComponent.getMargin();
			this.textComponent = textComponent;
		}

		void updateComponent() {
			if (background != textComponent.getBackground()) {
				textComponent.setBackground(background);
			}
			if (foreground != textComponent.getForeground()) {
				textComponent.setForeground(foreground);
			}
			if (font != textComponent.getFont()) {
				textComponent.setFont(font);
			}
			if (textComponent instanceof JTextField) {
				JTextField textField = (JTextField) textComponent;
				if (hAlignment != textField.getHorizontalAlignment()) {
					textField.setHorizontalAlignment(hAlignment);
				}
			}
			if (!margin.equals(textComponent.getMargin())) {
				textComponent.setMargin(margin);
			}
		}

		void fireEditing(JTextComponent textComponent, DBTextDataBinder binder, ColumnPaintListener columnPaintListener) {
			reset();
			columnPaintListener.editing(binder.columnAwareSupport.dataSet,
																	binder.columnAwareSupport.getColumn(),
																	this);
			updateComponent();
		}

		void firePainting(JTextComponent textComponent, DBTextDataBinder binder, ColumnPaintListener columnPaintListener) {
			reset();
			Variant variantValue = binder.columnAwareSupport.getVariant();
			Variant variantCopy = (Variant) variantValue.clone();
			columnPaintListener.painting(	binder.columnAwareSupport.dataSet,
																		binder.columnAwareSupport.getColumn(),
																		binder.columnAwareSupport.dataSet.getRow(),
																		variantValue,
																		this);
			if (variantCopy != null && !variantCopy.equals(variantValue)) {
				binder.ignoreValueChange = true;
				try {
					textComponent.setText(binder.columnAwareSupport.getColumn().format(variantValue));
				} catch (Exception e) {
					DBExceptionHandler.handleException(binder.columnAwareSupport.dataSet, textComponent, e);
				}
				binder.ignoreValueChange = false;
			}
			updateComponent();
		}

		// com.borland.dx.dataset.CustomPaintSite method
		public void reset() {
			setBackground(defaultBackground);
			setForeground(defaultForeground);
			setFont(defaultFont);
			if (textComponent instanceof JTextField) {
				hAlignment = defaultAlignment;
			}
			margin = defaultMargins;
		}

		// com.borland.dx.dataset.CustomPaintSite method
		public void setBackground(Color background) {
			this.background = background;
		}

		// com.borland.dx.dataset.CustomPaintSite method
		public Color getBackground() {
			return background;
		}

		// com.borland.dx.dataset.CustomPaintSite method
		public void setForeground(Color foreground) {
			this.foreground = foreground;
		}

		// com.borland.dx.dataset.CustomPaintSite method
		public Color getForeground() {
			return foreground;
		}

		// com.borland.dx.dataset.CustomPaintSite method
		public void setFont(Font font) {
			this.font = font;
		}

		// com.borland.dx.dataset.CustomPaintSite method
		public Font getFont() {
			return font;
		}

		// com.borland.dx.dataset.CustomPaintSite method
		public void setAlignment(int alignment) {
			this.alignment = alignment;
			convertAlignment(alignment);
		}

		// com.borland.dx.dataset.CustomPaintSite method
		public int getAlignment() {
			return alignment;
		}

		private void convertAlignment(int alignment) {
			hAlignment = DBUtilities.convertJBCLToSwingAlignment(alignment, true);
		}

		// com.borland.dx.dataset.CustomPaintSite method
		public void setItemMargins(Insets margin) {
			this.margin = margin;
		}

		// com.borland.dx.dataset.CustomPaintSite method
		public Insets getItemMargins() {
			return margin;
		}

		// com.borland.dx.dataset.CustomPaintSite method
		public boolean isTransparent() {
			return false;
		}

		public Component getSiteComponent() {
			return textComponent;
		}

		@Override
		public Border getBorderOvrrd() {
			return null;
		}

		@Override
		public void setBorderOvrrd(Border border) {}

		@Override
		public void addColorStripe(Color color, int width, boolean isLeftAligned) {}

		@Override
		public void addLabel(CustomPaintSiteLabel paintSiteLabel) {}

	}

	/** JTextComponent to be bound to DataSet */
	private JTextComponent textComponent;

	/** document to monitor for state changes */
	private Document document;

	/** support for ColumnAware implementation */
	DBColumnAwareSupport columnAwareSupport = new DBColumnAwareSupport(this);

	/** flag indicating we should ignore model change because we caused it */
	// private boolean ignoreValueChange;
	boolean ignoreValueChange;

	/** flag indicating model changed */
	private boolean textModified = false;

	/** popup menu for cut, copy, and paste operations */
	private JPopupMenu menu;

	/** whether or not to display the popup menu */
	private boolean enablePopup = true;

	/** whether or not to allow clear all */
	private boolean enableClearAll = true;

	/** whether or not to allow font changes */
	private boolean enableFontChange = true;

	/** whether or not to allow color changes */
	private boolean enableColorChange = true;

	/** whether or not to allow URL loading */
	private boolean enableURLLoading = true;

	/** whether or not to allow file loading */
	private boolean enableFileLoading = true;

	/** whether or not to allow file saving */
	private boolean enableFileSaving = true;

	/** whether or not to allow undo/redo */
	private boolean enableUndoRedo = true;

	/** whether newly fetched HTML pages should be added to dataset */
	private boolean enableURLCache = true;

	/** whether or not to post current field data on focus lost event */
	private boolean postOnFocusLost = true;

	/** whether or not to post current field data on dataset row posted event */
	private boolean postOnRowPosted = true;

	/** undo manager to which edits are delegated */
	UndoManager undoManager = new UndoManager();

	/** last focused undoManager */
	static UndoManager focusedUndoManager;

	/** last focused DBTextDataBinder bound component */
	static DBTextDataBinder focusedDataBinder;

	/** DBTextDataBinder's keymap name */
	private static final String keymapName = "DBTextDataBinder keymap";

	private static IntlSwingSupport intlSwingSupport = new IntlSwingSupport();

	private static JFileChooser fileChooser;

	private Point point = new Point();

	private static boolean ignoreURLChange = false;

	private String columnNameURL;

	private DataRow locateRow;

	private boolean rebuildMenu = true;

	private Column oldURLColumn;

	private Component oldTextComponent;

	private boolean nextFocusOnEnter = true;

	private boolean rebindColumnProperties;

	static boolean ignoreModelChange = false;

	private ItemEditMask ems;
	private ItemEditMaskState state;
	private boolean displayMode;
	private Variant maskVariant;
	private boolean focusLost;

	private ColumnPaintListener columnPaintListener;
	private DBCustomPaintSite customPaintSite;

	// for DataSet.enableDataSetEvents() support
	private boolean dataSetEventsEnabled = true;
}
