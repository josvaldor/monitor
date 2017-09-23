package com.josvaldor.module.mbean.statemachine;

import com.josvaldor.module.Module;
import com.josvaldor.module.mbean.MBean;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import javax.management.MBeanServer;
import org.apache.log4j.Logger;

public class StateMachine extends MBean implements StateMachineMBean {
	public static final int DEFAULT = 0;
	protected Map<Integer, String> stateMap;
	protected int state = 0;
	protected Integer previousState = null;

	public static void main(String[] args) {
		StateMachine stateMachine = new StateMachine(0);
		CountDownLatch countDownLatch;
	    stateMachine.setCountDownLatch(countDownLatch = new CountDownLatch(1));
		stateMachine.start();
	}

	public StateMachine() {
	}

	public StateMachine(int id) {
		super(id);
	}

	public StateMachine(int id, Module module) {
		super(Integer.valueOf(id), module);
	}

	public StateMachine(URL[] urlArray, MBeanServer mBeanServer) {
		super(urlArray, mBeanServer);
	}

	public StateMachine(MBeanServer mBeanServer) {
		super(new URL[0], mBeanServer);
	}

	@Override
	public void initialize() {
		super.initialize();
		this.stateMap = Collections.synchronizedMap(new ConcurrentHashMap());
		this.stateMap.put(Integer.valueOf(0), "DEFAULT");
	}

	public void run() {
		super.run();
		while (this.run) {
			machine();
		}
	}

	public int getState() {
		return this.state;
	}

	public String getState(int state) {
		return (String) this.stateMap.get(Integer.valueOf(state));
	}

	protected void machine() {
		Object object = inputObjectListRemove(0);
		machine(this.state, object);
	}

	protected void machine(int state, Object object) {
//		if (logger.isDebugEnabled()) {
//			logger.debug("machine(" + getState(state) + ", " + object + ")");
//		}
	}

	protected Object test(int state, Object object) {
		return object;
	}

	protected void setState(int state) {
		logger.info(this + ".setState(" + getState(state) + ")");
		setState(state, false);
	}

	protected void setState(int state, boolean flag) {
		if (flag) {
			this.previousState = Integer.valueOf(this.state);
		} else {
			this.previousState = null;
		}
		this.state = state;
	}
}
