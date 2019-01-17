//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/util/Diagnostic.java,v 7.1 2003/06/19 21:36:17 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

//NOTTRANSLATABLE

package com.borland.jb.util;

import java.io.PrintStream;
import java.util.Vector;

/**
 *  The Diagnostic component collects useful diagnostic functions
 *  for debugging program flow and output.
 *  <p>All calls to <code>Diagnostic</code> methods with a <strong>void</strong> return type
 *  can be removed from the compiled classes by using the compiler's
 *  <code>exclude</code> class option:
 *   <pre>-exclude com.borland.jb.util.Diagnostic</pre>
 *   
 *  JLimo modification: standard java compiler does not support 'exclude' option, so IS_DEBUG variable was introduced
 *  This class was renamed from Diagnostic to DiagnosticJLimo, because it was compiled into multiple Borland library files.
 */
public class DiagnosticJLimo {
	
	public static final boolean IS_DEBUG = (System.getProperty("jb.util.diagnostic", "off").equals("on"));

	/**
	 * Specifies whether output logging is initially enabled or disabled.
	 */
	// public static boolean outputEnabled = (System.getProperty("jb.util.diagnostic", "on").equals("on"));
	public static boolean outputEnabled = IS_DEBUG;

	/**
	 * Enables or disables output of diagnostic messages to <CODE>System.err</CODE>.
	 */
	public static PrintStream out = System.err;

	private static final String indent = "                                  ";

	private static int outLevel = parseInt((System.getProperty("jb.util.trace.level", "0")));
	// private static boolean checking = true;
	private static boolean checking = IS_DEBUG;
	private static Vector traceCategories = initTraceCategories();

    /*
     * !doc Not sure which example is better.
     *
     * Diagnostic.println(++Diagnostic.count+"\tdebug message");
     * or (same output)
     * Diagnostic.printlnc("debug message");
     */

    /**
     * A common counter variable used used to add line numbers to
     * debug output messages. For example:
     * <PRE><CODE>Diagnostic.out.println(++Diagnostic.count+"\tdebug message");</CODE></PRE>
     */
    public static int count = 0;

    /**
     * Enables or disables all output of diagnostic messages to <code>System.err</code>.
     */
    public static void enableOutput(boolean enable) {
        outputEnabled = enable;
    }

    /**
     * Explicitly sets the stream to which diagnostic
     * messages should be sent.
     * @param log     The stream to send messages to.
     */
    public static void setLogStream(PrintStream log) {
        out = log;
    }

    /**
     * Enables or disables the checking of conditions in
     * <code>precondition()</code> and <code>check()</code>.
     * @see #check(boolean)
     * @see #check(boolean, java.lang.Object)
     * @see #precondition(boolean)
     * @see #precondition(boolean, java.lang.String)
     */
    public static void enableChecking(boolean enable) {
        checking = enable;
    }

    /**
     * Sets the minimum threshold for trace and warning output.
     * <p><strong>Note:</strong> This does not affect direct
     * access to <code>out</code>, nor does it affect output in
     * the checking functions. Use <code>enableOutput()</code> instead.
     *
     * @param level The trace level. <code>0</code> is the highest
     *              level and <code>+maxint</code> is the lowest
     *              level. Setting this level to -1 effectively
     *              turns off traces and warnings.
     * @see         #getTraceLevel()
     */
    public static void setTraceLevel(int level) {
        outLevel = level;
    }

    /**
     * Gets the minimum threshold for trace and warning output.
     * <code>0</code> is highest level and <code>+maxint</code> is lowest level.
     * @return  The minimum threshold for trace and warning output.
     * @see #setTraceLevel(int)
     */
    public static int getTraceLevel() {
        return outLevel;
    }

    /**
     * A category-based tracing or warning method.
     *
     * <P>To set up a category-based tracing or warning, pass in a unique <CODE>
     * String</CODE>, <CODE>Class</CODE> or other object that supports a
     * meaningful <CODE>toString</CODE> operation. When a call to a trace or warn
     * method that takes a category is made (for example, <CODE>trace(Object
     * category, String description)</CODE>), the trace is displayed if an <CODE>
     * addTraceCategory()</CODE> call was made with the same category object.
     *
     * @param category The category object.
     * @see #removeTraceCategory(Object)
     */
    public static void addTraceCategory(Object category) {
        traceCategories.addElement(category);
    }

    /**
     * Removes a trace added with {@link #addTraceCategory(Object)}.
     * @param category  The category object.
     * @see #addTraceCategory(Object)
     */
    public static void removeTraceCategory(Object category) {
        traceCategories.removeElement(category);
    }

    /**
     * Checks a condition. Same as {@link #check(boolean, java.lang.Object)},
     * but typically placed at the start of the method body.
     * @param condition     The boolean condition, either true or false.
     * @param description   The description to print.
     * @throws IllegalStateException     The given condition is false.
     */
    public static void precondition(boolean condition, String description) throws IllegalStateException {
        if (checking && !condition) {
            throw new IllegalStateException(description);
        }
    }

    /**
     * Checks a condition.
     * Same as {@link #check(boolean)}, but typically placed at the
     * start of the method body.
     * @param condition                 The boolean condition, either true or false.
     * @throws IllegalStateException    The given condition is false.
     */
    public static void precondition(boolean condition) throws IllegalStateException {
        if (checking && !condition) {
            throw new IllegalStateException();
        }
    }

    /**
     * Check a condition within a method body. Use this to describe
     * assumed results and state after internal operations. A check
     * is raised if the given condition is not true.
     *
     * <p>An error here usually indicates an internal problem with the class.
     */
    public static void check(boolean condition, Object description) throws IllegalStateException {
        if (checking && !condition) {
            throw new IllegalStateException(Thread.currentThread()+" check failed: " + description); //NORES
        }
    }

    /**
     * Checks a condition within a method body. It throws an <CODE>IllegalStateException</CODE>
     * if the given condition is <STRONG>false</STRONG>.
     *
     * @param condition               The boolean condition.
     * @throws IllegalStateException  The given condition is false.
     */
    public static void check(boolean condition) throws IllegalStateException {
        if (checking && !condition) {
            throw new IllegalStateException(Thread.currentThread()+" check failed"); //NORES
        }
    }

    /**
     * Fails if description is not null and displays the description.
     */
    public static void check(Object description) throws IllegalStateException {
        if (checking && description != null) {
            throw new IllegalStateException(Thread.currentThread()+" check failed: " + description); //NORES
        }
    }

    /**
     *  Causes a check exception if the code reaches an unexpected location.
     */
    public static void fail(Object description) throws IllegalStateException {
        check(false, description);
    }

    /**
     * Calls <code>check(false)</code> to force a failure.
     * @throws IllegalStateException
     */
    public static void fail() throws IllegalStateException {
        check(false);
    }

    /**
     * Calls <code>check(false)</code> to force a failure
     * but prints the exception message on the stack trace first.
     * @param ex An exception that occurred.
     * @throws IllegalStateException
     */
    public static void fail(Exception ex) throws IllegalStateException {
        if(outputEnabled) {
            println(ex.getMessage());
            ex.printStackTrace(out);
            check(false);
        }
    }
    /**
     * Calls System.gc()
     */
    public static void gc() {
      System.gc();
    }

    /**
     * Calls <code>System.exit(int code)</code>.
     * @param code  The method body to exit.
     */
    public static void exit(int code) {
        println("Diagnostic.exit:");
        out.flush();
        System.exit(code);
    }

    /**
     * Use to mark places where an exception is needed.
     * @exception IllegalStateException   An error occurred.
     */
    public static void needException() throws IllegalStateException {
        check(false);
    }

    /**
      * Outputs a warning if the threshold level is high enough,
      * and a condition is <b>true</b>, and general
      * output is enabled.
      * @param level  The trace level. <code>0</code> is highest level
      *                and <code>+maxint</code> is lowest level.
      *                Setting this level to -1
      *                effectively turns off traces and warnings.
      * @param condition    The boolean condition, either <b>true</b> or <b>false.</b>
      * @param description  The string to trace.
      * @see                #addTraceCategory(java.lang.Object)
     */
    public static void warn(int level, boolean condition, String description) {
        if (condition && level <= outLevel)
            println("wn[" + level + "] " + indent.substring(0,level*2) + description);  //NORES
    }

    /**
      * Outputs a warning if the category object is enabled, the boolean
      * condition is <b>true</b>, and general
      * output is enabled.
      * @param category       The category object to trace.
      * @param condition      The boolean condition, either <b>true</b> or <b>false.</b>
      * @param description    The string to trace.
      * @see                 #addTraceCategory(java.lang.Object)
      */
    public static void warn(Object category, boolean condition, String description) {
        if (condition && traceCategories.contains(category))
            println("wn[" + category.toString() + "] " + description);  //NORES
    }

    /**
     * Outputs a warning if the category and general output are both enabled.
     * @param category     The category object to trace.
     * @param description  The string to trace.
     * @see                #addTraceCategory(java.lang.Object)
     */
    static public void warn(Object category, String description) {
        warn(category, true, description);
    }

    /**
     * Outputs a warning if the category object is enabled, the
     * boolean condition is <b>true</b>, and general
     * output is enabled
     * @param category       The category object to trace.
     * @param level  The trace level. <code>0</code> is highest level
     *                and <code>+maxint</code> is lowest level.
     *                Setting this level to -1
     *                effectively turns off traces and warnings.
     * @param condition      The boolean condition, either <b>true</b> or <b>false.</b>
     * @param description    The string to trace.
     */
    public static void warn(Object category, int level, boolean condition, String description) {
        if (condition && level <= outLevel && traceCategories.contains(category))
            println("wn[" + category.toString() + "." + level +  "] " + description);  //NORES
    }

    /**
     *  Outputs a trace if the threshold level is high enough
     *  and general output is enabled.
     *  @param level  The trace level. <code>0</code> is highest level
     *                and <code>+maxint</code> is lowest level.
     *                Setting this level to -1
     *                effectively turns off traces and warnings.
     *  @param description  The string to trace.
     */
    public static void trace(int level, String description) {
        if (level <= outLevel)
            println("[" + level + "]" + indent.substring(0,level*2) + description);
    }

    /**
     * Outputs a trace if the category is enabled and general output is enabled
     * @param       category    The category object to trace.
     * @param       description The string to trace.
     * @see         #addTraceCategory(java.lang.Object)
     */
    public static void trace(Object category, String description) {
        if (traceCategories.contains(category))
            println("[" + category.toString() + "] " + description);
    }

    /**
     * Outputs a trace if the category and general output are
     * both enabled, and the threshold level is high enough.
     * @param category      The category object to trace.
     * @param level         The trace level. <code>0</code> is the highest level and
     *                      <code>+maxint</code> is the lowest level. Setting this level to
     *                      -1 effectively turns off traces and warnings.
     * @param description    The string to trace.
     * @see                 #addTraceCategory(java.lang.Object)
     */
    public static void trace(Object category, int level, String description) {
        if (level <= outLevel && traceCategories.contains(category))
            println("[" + category.toString() + "." + level + "] " + indent.substring(0,level*2) + description);
    }

    /**
     * Print a diagnostic stack trace of the current thread to the diagnostic out stream
     */
    public static void printStackTrace() {
        if (outputEnabled) {
            synchronized(out) {
              new Exception("Diagnostic Stack Trace").printStackTrace(out);  //NORES
            }
        }
    }

    /**
     * Prints a diagnostic stack trace of
     * the current thread to the diagnostic out stream. Throws an exception.
     * @param ex
     */
    public static void printStackTrace(Throwable ex) {
        if(outputEnabled) {
            synchronized(out) {
              ex.printStackTrace(out);
            }
        }
    }

    /**
     * Prints a conditional message to the diagnostic out stream.
     * @param condition   Only print if this condition is true.
     * @param message     The message to print.
     */
    public static void println(boolean condition, String message) {
        if(condition && outputEnabled) {
          synchronized(out) {
            out.println(message);
          }
        }
    }

    /**
     * Prints a message to the diagnostic out stream.
     * @param message     The message to print.
     */
    public static void println(String message) {
        if(outputEnabled) {
          synchronized(out) {
            out.println(message);
          }
        }
    }

    /**
     * Prints a message to the diagnostic out stream;
     * each line is preceded by a line number (incremented count).
     * @param message     The message to print.
     */
    public static void print(String message) {
        if(outputEnabled)
            out.print(message);
    }

    /**
     * Prints a message to the diagnostic out stream; each lines
     * is preceded by a line number (incremented count) and a tab character.
     * @param message     The message to print.
     */
    public static void printlnc(String message) {
        if(outputEnabled)
            out.println(++count + "\t" + message); // NORES
    }

    /**
     * Flushes the diagnostic <code>out</code> Stream buffer
     */
    public static void flush() {
        if(outputEnabled)
            out.flush();
    }

    /*
     * Convert a String to an intager. Returns 0 if it cannot
     * parse the String
     */
    private static int parseInt(String s) {
        int value;
        try {
            value = Integer.parseInt(s);
        } catch(NumberFormatException e) {
            value = 0;
        }
        return value;
    }

    /*
     * Parse the comma seperated list of trace categories
     * and add each category to the Vector of traceCategories
     */
    private static final char sep = ',';
    private static Vector initTraceCategories() {
        Vector v = new Vector();
        String s = System.getProperty("jb.util.trace.categories");
        if(s != null) {
            int l = s.length();
            if (l > 0) {
                if (s.charAt(l - 1) != sep)
                    s = s + sep;

                int i = 0;
                while (i < l) {
                    int j = s.indexOf(sep, i);
                    if (j < 0) j = l;
                    if (i < j) {
                        String category = s.substring(i, j);
                        v.addElement(category);
                        println("Diagnostic: Adding trace category " + category);
                    }
                    i = j + 1;
                }
            }
        }
        return v;
    }
//!   WARNING!  This is a bad method since it references another borland package.
//!   Note that this source file is compiled with Diagnostics enabled, so if another
//!   dependent package is compiled implicitly, it will be compiled with Diagnostics enabled!!!
//!   See bug 13536
//!
//!  /**
//!   * A useful debugging function to display ItemPainter state bits as text
//!   */
//!  public static final String getStateAsText(int state) {
//!    String stateText = ""; // NORES
//!    for (int i = 0; i < com.borland.jbcl.model.ItemPainter.states.length; i++) {
//!      if ((state & com.borland.jbcl.model.ItemPainter.states[i]) != 0)
//!        stateText += "|" + com.borland.jbcl.model.ItemPainter.stateNames[i]; // NORES
//!    }
//!    if ("".equals(stateText)) // NORES
//!      return "<none>"; // NORES
//!    return stateText.substring(1, stateText.length());
//!  }
//  private static OrderedSet     traceCategories = new OrderedSet(); // more overhead, but faster with many items
}

