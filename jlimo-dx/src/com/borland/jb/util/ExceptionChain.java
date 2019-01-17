//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/util/ExceptionChain.java,v 7.1 2003/06/19 21:36:17 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.util;

import com.borland.jb.util.Trace;


/*
 * The ExceptionChain component represents the node of a
 * {@link util.ChainedException} object (the linked list
 * of exceptions) that can be traversed.
 */
public class ExceptionChain implements java.io.Serializable {

/**
 * Constructs a new <code>ExceptionChain</code>.
 */
  public ExceptionChain() {
  }

  private ExceptionChain(ExceptionChain next, Throwable ex) {
    this.next = next;
    this.ex = ex;
  }


  /**
   * Appends the given exception to the end of the
   * {@link com.borland.jb.util.ChainedException}
   * object (the linked list of exceptions).
   * @param ex an exception that occurred
   */
  public void append(Throwable ex) {
    if (this.ex == null)
      this.ex = ex;
    else
      next = new ExceptionChain(next, ex);
  }

  /**
   * Prints all exception stack traces in the chain.
   * @param out   The chain to print.
   */
  public void printStackTrace(java.io.PrintStream out) {
    java.io.PrintWriter writer = new java.io.PrintWriter(out);
    printStackTrace(this, writer);
    writer.flush();
  }

  public void printStackTrace(java.io.PrintWriter writer) {
    printStackTrace(this, writer);
  }

  /**
   * Prints all diagnostic stack traces in the chain.
   */
  public void printDiagnosticStackTrace() {
    if (next != null)
      next.printDiagnosticStackTrace();
    if (ex != null) {
      DiagnosticJLimo.printStackTrace(ex);
    }
  }

  private void printStackTrace(ExceptionChain exChain, java.io.PrintWriter writer) {
    if (exChain != null && exChain.next != null)
      exChain.printStackTrace(exChain.next, writer);
    if (ex != null)
      ex.printStackTrace(writer);
  }

  /**
   * Returns <b>true</b> if there is an exception instance in the chain.
   * @return
   */
  public boolean        hasExceptions() { return ex != null; }

  /**
   * Moves to the next <code>ExceptionChain</code> instance.
   * @return null last instance has been reached
   */
  public ExceptionChain getNext() { return next; }

  /**
   * Places an exception at this node.
   * @return
   */
  public Throwable      getException() { return ex; }

  /**
   * Gets the original exception in the
   * {@link com.borland.jb.util.ChainedException} object
   * (the linked list of exceptions).
   * @param ex an exception that occurred
   */
  public static String getOriginalMessage(Throwable ex) {
    while (ex instanceof ChainedException) {
      ExceptionChain chain = ((ChainedException)ex).getExceptionChain();
      if (chain == null)
        break;
      while (chain.getNext() != null)
        chain = chain.getNext();
      ex = chain.getException();
    }
    String message = ex.getMessage();
    if (message == null || message.length() == 0 || ex instanceof ArrayIndexOutOfBoundsException) {
      String name = ex.getClass().getName();
      message = (message == null ? name : name + " " + message);
    }
    return message;
  }

  private ExceptionChain next;
  private Throwable      ex;
}
