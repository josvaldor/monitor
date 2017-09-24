/* 
 * Date 2014-2017
 * Author Joaquin Rodriguez
 * Copyright (c) Joaquin Osvaldo Rodriguez  All rights reserved.
 */
package com.josvaldor.module.mbean;

import com.josvaldor.module.Module;
import com.josvaldor.utility.Utility;
import java.io.PrintStream;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class MBean extends Module implements MBeanMBean, MBeanInterface {
	public static void main(String[] args) {
		int id = 0;
		MBean mBean = new MBean(Integer.valueOf(id), new Module());
		CountDownLatch countDownLatch;
		mBean.setCountDownLatch(countDownLatch = new CountDownLatch(1));
		mBean.start();
	}

	protected MBeanServer mBeanServer = null;
	protected ObjectName objectName = null;
	protected String objectNameString = null;

	public MBean() {
	}

	public MBean(int id) {
		super(id);
	}

	public MBean(URL[] urlArray, MBeanServer mBeanServer) {
		super(urlArray);
		this.mBeanServer = mBeanServer;
		this.objectName = newObjectName(getObjectNameString());
		mBeanServerRegisterMBean(this);
	}

	public MBean(Integer id, Module module) {
		super(id.intValue(), module);
		if (((module instanceof MBean)) && (this.rootModule != null)) {
			this.mBeanServer = ((MBean) this.rootModule).getMBeanServer();
			this.objectName = newObjectName(getObjectNameString());
			mBeanServerRegisterMBean(this);
		}
	}

	public void start() {
		super.start();
	}

	public void initialize() {
		super.initialize();
	}

	public void run() {
		super.run();
	}

	public void stop() {
		super.stop();
	}

	public void destroy() {
		if (!this.destroy) {
			super.destroy();
			mBeanServerUnregisterMBean(this);
		}
	}

	public MBeanServer getMBeanServer() {
		return this.mBeanServer;
	}

	public String getObjectNameString() {
		return getClass().getPackage().getName() + ":type=" + this + "_" + this.id + "_"
				+ Utility.formatDate("GMT", "yyyyMMddhhmmss", new Date());
	}

	public ObjectName newObjectName(String objectNameString) {
		ObjectName objectName = null;
		if ((objectNameString instanceof String)) {
			try {
				objectName = new ObjectName(objectNameString);
			} catch (MalformedObjectNameException e) {
				System.err.println("newObjectName(" + objectNameString + ") MalformedObjectNameException");
			}
		}
		return objectName;
	}

	public ObjectName getObjectName() {
		return this.objectName;
	}

	public void mBeanServerRegisterMBean(Object object) {
		if ((object instanceof MBean)) {
			mBeanServerRegisterMBean(((MBean) object).getMBeanServer(), (MBean) object,
					((MBean) object).getObjectName());
		}
	}

	public void mBeanServerUnregisterMBean(Object object) {
		if ((object instanceof MBean)) {
			mBeanServerUnregisterMBean(((MBean) object).getMBeanServer(), ((MBean) object).getObjectName());
		}
	}

	private void mBeanServerRegisterMBean(MBeanServer mBeanServer, Object object, ObjectName objectName) {
		if ((mBeanServer != null) && (object != null) && (objectName != null)) {
			try {
				mBeanServer.registerMBean(object, objectName);
			} catch (InstanceAlreadyExistsException e) {
				System.err.println("mBeanServerRegisterMBean(mBeanServer, object, " + objectName
						+ ") InstanceAlreadyExistsException");
			} catch (MBeanRegistrationException e) {
				System.err.println(
						"mBeanServerRegisterMBean(mBeanServer, object, " + objectName + ") MBeanRegistrationException");
			} catch (NotCompliantMBeanException e) {
				System.err.println(
						"mBeanServerRegisterMBean(mBeanServer, object, " + objectName + ") NotCompliantMBeanException");
			}
		}
	}

	private void mBeanServerUnregisterMBean(MBeanServer mBeanServer, ObjectName objectName) {
		if ((mBeanServer != null) && (objectName != null)) {
			try {
				mBeanServer.unregisterMBean(objectName);
			} catch (MBeanRegistrationException e) {
				System.err.println(
						"mBeanServerUnregisterMBean(mBeanServer, " + objectName + ") MBeanRegistrationException");
			} catch (InstanceNotFoundException e) {
				System.err.println(
						"mBeanServerUnregisterMBean(mBeanServer, " + objectName + ") InstanceNotFoundException");
			}
		}
	}

	public int getModuleMapSize() {
		return this.moduleMap.size();
	}
}
