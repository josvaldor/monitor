package com.josvaldor.module.mbean.statemachine.node.network;

import com.josvaldor.module.Module;

public class Ping
  extends Network
{
  private String command;
  
  public Ping(int id, Module module)
  {
    super(Integer.valueOf(id), module);
  }
  
  public void initialize()
  {
    this.node = false;
    super.initialize();
    this.command = null;
    switch (this.operatingSystem)
    {
    case 1: 
      this.command = ("ping -c " + this.tryMax + " " + this.hostAddress);
      break;
    case 2: 
      this.command = ("ping -n " + this.tryMax + " " + this.hostAddress);
    }
  }
  
  public void waitForInput(Object object)
  {
    if (delayExpired())
    {
      if (execute(this.command) > 0) {
        setState(0);
      }
      setDelayExpiration(newDelayExpiration(this.waitForInputDelay));
    }
  }
}
