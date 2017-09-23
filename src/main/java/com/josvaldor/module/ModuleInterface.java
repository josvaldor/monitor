package com.josvaldor.module;

public abstract interface ModuleInterface
  extends Runnable
{
  public abstract void start();
  
  public abstract void initialize();
  
  public abstract void run();
  
  public abstract void stop();
  
  public abstract void destroy();
  
  public abstract boolean getStart();
  
  public abstract boolean getRun();
  
  public abstract boolean getDestroy();
  
  public abstract boolean getProtect();
  
  public abstract int getID();
  
  public abstract void inputObjectListAdd(Object paramObject);
  
  public abstract Object inputObjectListRemove(int paramInt);
  
  public abstract void outputObjectListAdd(Object paramObject);
  
  public abstract Object load(Integer paramInteger, Object paramObject);
}
