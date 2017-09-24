package com.mulesoft.client;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import com.josvaldor.module.Module;
import com.josvaldor.module.mbean.statemachine.StateMachine;

public class Client extends StateMachine {

	InfluxDB influxDB;
	String databaseName = "monitor";

	public Client() {
	}

	public Client(MBeanServer mBeanServer) {
		super(new URL[0], mBeanServer);
	}

	public Client(int id, Module module) {
		super(Integer.valueOf(id), module);
	}

	public void initialize() {
		super.initialize();
		influxDB = InfluxDBFactory.connect("http://localhost:8086", "root", "root");
		influxDB.createDatabase(databaseName);
		// influxDB.setRetentionPolicy("autogen");
		// BatchPoints batchPoints = BatchPoints
		// .database(databaseName)
		// .tag("async", "true")
		// .retentionPolicy("autogen")
		// .consistency(InfluxDB.ConsistencyLevel.ALL)
		// .tag("BatchTag", "BatchTagValue") // tag each point in the batch
		// .build();
		// influxDB.write(batchPoints);
	}

	public void machine() {
		if (delayExpired()) {
//			logger.info("Data Written to InfluxDB");
			BatchPoints batchPoints = BatchPoints.database(databaseName).tag("async", "true").retentionPolicy("autogen")
					.consistency(InfluxDB.ConsistencyLevel.ALL).tag("BatchTag", "BatchTagValue").build();

			Point p = null;
			for (SupportedJMXBean b : SupportedJMXBean.values()) {
				Object value = this.getValue(b);
				p = Point.measurement(b.getAttribute()).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
						.addField("attribute", b.getAttribute()).addField("value", value + "")
						.addField("monitorMessage", b.getMonitorMessage()).tag("CpuTag", "CpuTagValue").build();
				batchPoints.point(p);
			}
			influxDB.write(batchPoints);
			setDelayExpiration(newDelayExpiration(1.0));
		}

	}
	
	public Object getValue(SupportedJMXBean bean){
		return getValue(bean.getBeanQuery(),bean.getAttribute());
	}
	
	public Object getValue(String beanQuery, String attribute){
		Object value = null;
		try {
			ObjectName objectName = new ObjectName(beanQuery);
			if (this.mBeanServer.isRegistered(objectName)) {
				value = mBeanServer.getAttribute(objectName, attribute);
			}
		} catch (MalformedObjectNameException | AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException e) {
			MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
			MemoryUsage heap = memBean.getHeapMemoryUsage();
			MemoryUsage nonHeap = memBean.getNonHeapMemoryUsage();
			if (attribute.equals("HeapMemoryUsage.used")) {
				value = heap.getUsed();
			} else if (attribute.equals("HeapMemoryUsage.max")) {
				value = heap.getMax();
			} else if (attribute.equals("HeapMemoryUsage.committed")) {
				value = heap.getCommitted();
			} else if (attribute.equals("Usage.used")) {
				value = nonHeap.getUsed();
			} else if (attribute.equals("Usage.max")) {
				value = nonHeap.getMax();
			} else if (attribute.equals("Usage.committed")) {
				value = nonHeap.getCommitted();
			}
		}
		return value;
	}

	public void destroy() {
		super.destroy();
		influxDB.deleteDatabase(databaseName);
	}
}
