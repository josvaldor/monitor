/* 
 * Date 2014-2017
 * Author Joaquin Rodriguez
 * Copyright (c) Joaquin Osvaldo Rodriguez  All rights reserved.
 */
package com.josvaldor.module.mbean;

import javax.management.ObjectName;

public abstract interface MBeanInterface
{
  public abstract String getObjectNameString();
  
  public abstract ObjectName newObjectName(String paramString);
  
  public abstract ObjectName getObjectName();
  
  public abstract void mBeanServerRegisterMBean(Object paramObject);
  
  public abstract void mBeanServerUnregisterMBean(Object paramObject);
}
