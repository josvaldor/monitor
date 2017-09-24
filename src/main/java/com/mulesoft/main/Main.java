/* Main.java
 * Date 201709
 * Author Joaquin Rodriguez
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 */
package com.mulesoft.main;

import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.mulesoft.influxdb.Client;

@SpringBootApplication
public class Main {
	private static MBeanServer mBeanServer;
	private static Client module;

	public static void main(String[] arguments) {
		mBeanServer = ManagementFactory.getPlatformMBeanServer();
		module = new Client(mBeanServer);
		module.start();
		SpringApplication.run(Main.class, arguments);
	}
}
