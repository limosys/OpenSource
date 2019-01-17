//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/sql/dataset/TaskRunner.java,v 7.0 2002/08/08 18:39:59 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.jb.util.DiagnosticJLimo;

class TaskRunner extends java.lang.Thread {

  Task    task;
  static  int count;
  boolean completed;
  boolean hasWaiter;

  TaskRunner(Task task) {
    super("TaskRunner-"+count++);  //NORES
    this.task = task;
  }

  public void run() {
    try {
      task.executeTask();
      synchronized(this) {
        completed = true;
        if (hasWaiter)
          notifyAll();
      }
    }
    catch (Throwable ex) {
      DiagnosticJLimo.printStackTrace(ex);
    }
  }

  final synchronized void waitFor() {
    if (!completed) {
      hasWaiter = true;
      try {
        wait();
      }
      catch(Exception ex){
        DiagnosticJLimo.printStackTrace();
      }
    }
  }
}
