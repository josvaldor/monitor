package com.mulesoft.main;

import com.mulesoft.client.Client;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main
{
  private static MBeanServer mBeanServer;
  private static Client module;
  
  public static void main(String[] arguments)
  {
	    mBeanServer = ManagementFactory.getPlatformMBeanServer();
	    module = new Client(mBeanServer);
	    module.start();
	    SpringApplication.run(Main.class, arguments);
	    
  }
}
