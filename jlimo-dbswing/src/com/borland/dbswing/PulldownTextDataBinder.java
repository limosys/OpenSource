package com.borland.dbswing;

import com.borland.dbswing.DBTextDataBinder;
import javax.swing.text.JTextComponent;
import javax.swing.text.Document;

@SuppressWarnings("serial")
public class PulldownTextDataBinder extends DBTextDataBinder {

  private PulldownColumnAwareSupport pulldownColSupport;
  private boolean cancelUpdateText = false;

  public PulldownTextDataBinder(JTextComponent textComponent) {
    super(textComponent);
    //NOTE: I would like to set culumnAwareSupport here, but it causes J-Builder Designer problems.
    //      That's why I created setPulldownColumnAwareSupport() method.
  }

  public PulldownColumnAwareSupport getColumnAwareSupport() {
    return pulldownColSupport;
  }

  public void setPulldownColumnAwareSupport() {
    if (pulldownColSupport==null) {
      pulldownColSupport = new PulldownColumnAwareSupport(this);
      columnAwareSupport = pulldownColSupport;
    }
  }

  @Override
	public void updateText() {
  	JTextComponent tf = getJTextComponent();
    Document doc = tf.getDocument();
    if (doc!=null && doc instanceof DBPlainDocument) {
      DBPlainDocument d = (DBPlainDocument)doc;
      d.setMaxLength(500);
    }
    if (!cancelUpdateText) {
    	super.updateText();
    }
    if (tf.hasFocus() && tf.isEditable()) tf.setCaretPosition(tf.getText().length());
  }
  
  public void setCancelUpdateText(boolean cancelUpdateText) {
  	this.cancelUpdateText = cancelUpdateText;
  }

}
