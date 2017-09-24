/* 
 * Date 2014-2017
 * Author Joaquin Rodriguez
 * Copyright (c) Joaquin Osvaldo Rodriguez  All rights reserved.
 */
package com.josvaldor.module.mbean.statemachine;

import com.josvaldor.module.mbean.MBeanMBean;

public abstract interface StateMachineMBean
  extends MBeanMBean
{
  public abstract int getState();
  
  public abstract String getState(int paramInt);
}
