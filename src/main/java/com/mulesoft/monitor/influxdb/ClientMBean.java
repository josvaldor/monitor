package com.mulesoft.monitor.influxdb;

import com.josvaldor.module.mbean.statemachine.StateMachineMBean;

public interface ClientMBean extends StateMachineMBean{

	public void setWaitForOutputDelay(double waitForOutputDelay);
}
