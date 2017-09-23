package com.mulesoft.main;

import com.josvaldor.module.Module;
import com.mulesoft.monitor.Monitor;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;

public class Main
{
  private static MBeanServer mBeanServer;
  private static Monitor module;
  
  public static void main(String[] arguments)
  {
    mBeanServer = ManagementFactory.getPlatformMBeanServer();
    module = new Monitor(mBeanServer);
    module.start();
  }
}
