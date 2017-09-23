package com.mulesoft.main;

import com.josvaldor.module.Module;
import com.mulesoft.client.Client;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;

public class Main
{
  private static MBeanServer mBeanServer;
  private static Client module;
  
  public static void main(String[] arguments)
  {
    mBeanServer = ManagementFactory.getPlatformMBeanServer();
    module = new Client(mBeanServer);
    module.start();
  }
}
