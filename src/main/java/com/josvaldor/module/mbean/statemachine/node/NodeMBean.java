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
