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
			try {
				ObjectName test = new ObjectName(SupportedJMXBean.AVAILABLE_PROCESSORS.getBeanQuery());
				if (this.mBeanServer.isRegistered(test)) {
					Object value = mBeanServer.getAttribute(test, SupportedJMXBean.AVAILABLE_PROCESSORS.getAttribute());
					System.out.println(value);

				}

			} catch (MalformedObjectNameException | AttributeNotFoundException | InstanceNotFoundException
					| MBeanException | ReflectionException e) {
				e.printStackTrace();
			}
			BatchPoints batchPoints = BatchPoints.database(databaseName).tag("async", "true").retentionPolicy("autogen")
					.consistency(InfluxDB.ConsistencyLevel.ALL).tag("BatchTag", "BatchTagValue").build();

			Point p = null;
			for (SupportedJMXBean b : SupportedJMXBean.values()) {
				try {
					ObjectName test = new ObjectName(b.getBeanQuery());
					if (this.mBeanServer.isRegistered(test)) {
						Object value = mBeanServer.getAttribute(test, b.getAttribute());
						p = Point.measurement(b.getAttribute()).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
								.addField("attribute", b.getAttribute()).addField("value", value + "")
								.addField("monitorMessage", b.getMonitorMessage()).tag("CpuTag", "CpuTagValue")
								.build();
						batchPoints.point(p);
					}

				} catch (MalformedObjectNameException | AttributeNotFoundException | InstanceNotFoundException
						| MBeanException | ReflectionException e) {
					MemoryMXBean memBean = ManagementFactory.getMemoryMXBean(); 
					MemoryUsage heap = memBean.getHeapMemoryUsage();
					MemoryUsage nonHeap = memBean.getNonHeapMemoryUsage();
					
					if(b.getAttribute().equals("HeapMemoryUsage.used")){
						Object value = heap.getUsed();
						p = Point.measurement(b.getAttribute()).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
								.addField("attribute", b.getAttribute()).addField("value", value + "")
								.addField("monitorMessage", b.getMonitorMessage()).tag("CpuTag", "CpuTagValue")
								.build();
						batchPoints.point(p);
					}else if(b.getAttribute().equals("HeapMemoryUsage.max")){
						Object value = heap.getMax();
						p = Point.measurement(b.getAttribute()).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
								.addField("attribute", b.getAttribute()).addField("value", value + "")
								.addField("monitorMessage", b.getMonitorMessage()).tag("CpuTag", "CpuTagValue")
								.build();
						batchPoints.point(p);
					}else if(b.getAttribute().equals("HeapMemoryUsage.committed")){
						Object value = heap.getCommitted();
						p = Point.measurement(b.getAttribute()).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
								.addField("attribute", b.getAttribute()).addField("value", value + "")
								.addField("monitorMessage", b.getMonitorMessage()).tag("CpuTag", "CpuTagValue")
								.build();
						batchPoints.point(p);
					}else if(b.getAttribute().equals("Usage.used")){
						Object value = nonHeap.getUsed();
						p = Point.measurement(b.getAttribute()).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
								.addField("attribute", b.getAttribute()).addField("value", value + "")
								.addField("monitorMessage", b.getMonitorMessage()).tag("CpuTag", "CpuTagValue")
								.build();
						batchPoints.point(p);
					}else if(b.getAttribute().equals("Usage.max")){
						Object value = nonHeap.getMax();
						p = Point.measurement(b.getAttribute()).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
								.addField("attribute", b.getAttribute()).addField("value", value + "")
								.addField("monitorMessage", b.getMonitorMessage()).tag("CpuTag", "CpuTagValue")
								.build();
						batchPoints.point(p);
					}else if(b.getAttribute().equals("Usage.committed")){
						Object value = nonHeap.getCommitted();
						p = Point.measurement(b.getAttribute()).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
								.addField("attribute", b.getAttribute()).addField("value", value + "")
								.addField("monitorMessage", b.getMonitorMessage()).tag("CpuTag", "CpuTagValue")
								.build();
						batchPoints.point(p);
					}

				}
			}
			influxDB.write(batchPoints);
			setDelayExpiration(newDelayExpiration(1.0));
		}

	}

	public void destroy() {
		super.destroy();
		influxDB.deleteDatabase(databaseName);
	}
}
