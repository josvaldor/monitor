package com.mulesoft.monitor;

import java.net.URL;

import javax.management.MBeanServer;

import com.josvaldor.module.Module;
import com.josvaldor.module.mbean.statemachine.StateMachine;

public class Monitor extends StateMachine{
	  public Monitor() {}
	  
	  public Monitor(MBeanServer mBeanServer)
	  {
	    super(new URL[0], mBeanServer);
	  }
	  
	  public Monitor(int id, Module module)
	  {
	    super(Integer.valueOf(id), module);
	  }
	  
	  @Override 
	  public void initialize()
	  {
		  setDelayExpiration(newDelayExpiration(20.0));
	  }
	  
	   
	  public void machine(){
		  if(delayExpired()){
//			  System.out.println("Hello World");
			  setDelayExpiration(newDelayExpiration(10.0));
			  
		  }
	  }
	  
}



