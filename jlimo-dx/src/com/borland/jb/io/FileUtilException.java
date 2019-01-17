//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/jb/io/FileUtilException.java,v 7.0 2002/08/08 18:40:47 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.io;

import com.borland.dx.text.InvalidFormatException;
import com.borland.jb.util.ExceptionChain;
import com.borland.jb.util.ChainedException;

import java.io.IOException;



/**
*/

public class FileUtilException extends RuntimeException
  implements ChainedException
{
  protected int             errorCode;
  protected ExceptionChain  exceptionChain;

  public ExceptionChain getExceptionChain() {
    return exceptionChain;
  }

  /*  Error codes are reserved in ranges of 1024.
  */
  private static final int BASE = 0*1000;

  /** Generic error.
  */
  public static final int GENERIC_ERROR                             = BASE+0;

  /** Chained exception.  To access the lower level exception(s) call
      getExceptionChain().  printStackTrace will display all exceptions in
      the chain.
  */
  public static final int IOEXCEPTION                               = BASE+1;


  public FileUtilException(int errorCode, String message, Exception ex) {
    super(message);
    if (ex != null) {
      exceptionChain = new ExceptionChain();
      exceptionChain.append(ex);
    }
    this.errorCode = errorCode;
  }

  public FileUtilException(IOException ex) {
    this(IOEXCEPTION, ex.getMessage(), ex);
  }

  public FileUtilException(String message) {
    this(GENERIC_ERROR, message, null);
  }

  /**
      Overrides base class implementaiton of printStackTrace() to display
      the error code and any exceptions that may be chained to this one.
  */
  public void printStackTrace(java.io.PrintStream out) {
    out.println(Res.bundle.format( ResIndex.ErrorCode, getClass().getName(), Integer.toString(errorCode%1000)));
    super.printStackTrace(out);
    if (exceptionChain != null) {
      out.println(Res.bundle.getString(ResIndex.ChainedException));
      exceptionChain.printStackTrace(out);
    }
  }

  public void printStackTrace(java.io.PrintWriter writer) {
    writer.println(Res.bundle.format( ResIndex.ErrorCode, getClass().getName(), Integer.toString(errorCode%1000)));
    super.printStackTrace(writer);
    if (exceptionChain != null) {
      writer.println(Res.bundle.getString(ResIndex.ChainedException));
      exceptionChain.printStackTrace(writer);
    }
  }

  public void printStackTrace() {
    printStackTrace(System.err);
  }
}
