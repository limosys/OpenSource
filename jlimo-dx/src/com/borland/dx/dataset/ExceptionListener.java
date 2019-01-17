//--------------------------------------------------------------------------------------------------
// $Header: /usr/local/cvsrepository/JDataStore/JDS/src/java/com/borland/dx/dataset/ExceptionListener.java,v 7.0 2002/08/08 18:39:25 jlaurids Exp $
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.EventListener;

public interface ExceptionListener extends EventListener
{
  public void exception(ExceptionEvent event);
}
