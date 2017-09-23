package com.josvaldor.module.mbean;

public abstract interface MBeanMBean
{
  public abstract void destroy();
  
  public abstract boolean getStart();
  
  public abstract boolean getRun();
  
  public abstract boolean getDestroy();
  
  public abstract boolean getProtect();
  
  public abstract int getID();
  
  public abstract int getModuleMapSize();
}
