package com.josvaldor.module.mbean.statemachine;

import com.josvaldor.module.mbean.MBeanMBean;

public abstract interface StateMachineMBean
  extends MBeanMBean
{
  public abstract int getState();
  
  public abstract String getState(int paramInt);
}
