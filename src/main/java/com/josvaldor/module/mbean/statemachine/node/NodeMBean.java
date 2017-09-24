/* 
 * Date 2014-2017
 * Author Joaquin Rodriguez
 * Copyright (c) Joaquin Osvaldo Rodriguez  All rights reserved.
 */
package com.josvaldor.module.mbean.statemachine.node;

import java.util.Properties;

import com.josvaldor.module.mbean.statemachine.StateMachineMBean;

public abstract interface NodeMBean
  extends StateMachineMBean
{
  public abstract Properties getIDProperties();
  
  public abstract String getHostAddress();
  
  public abstract String getHostName();
}
