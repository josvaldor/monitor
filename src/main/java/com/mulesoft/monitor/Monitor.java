package com.mulesoft.monitor;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import com.josvaldor.module.Module;
import com.josvaldor.module.mbean.statemachine.StateMachine;

public class Monitor extends StateMachine {

	InfluxDB influxDB; 
	String databaseName = "monitor";
	public Monitor() {
	}

	public Monitor(MBeanServer mBeanServer) {
		super(new URL[0], mBeanServer);
	}

	public Monitor(int id, Module module) {
		super(Integer.valueOf(id), module);
	}
	
	public void initialize(){
//		super.initialize();
		influxDB = InfluxDBFactory.connect("http://localhost:8086","root","root");
//		influxDB.deleteDatabase(databaseName);
		influxDB.createDatabase(databaseName);
//		influxDB.setRetentionPolicy("autogen");
//		 BatchPoints batchPoints = BatchPoints
//	                .database(databaseName)
//	                .tag("async", "true")
//	                .retentionPolicy("autogen")
//	                .consistency(InfluxDB.ConsistencyLevel.ALL)
//	                .tag("BatchTag", "BatchTagValue") // tag each point in the batch
//	                .build();
//		 influxDB.write(batchPoints);
	}

	public void machine() {
		if (delayExpired()) {
//			System.out.println("Hello World");
//
//			ClassLoadingMXBean clBean = ManagementFactory.getClassLoadingMXBean();
//
//			int lcCount = clBean.getLoadedClassCount();
//
//			SupportedJMXBean.CLASS_LOADING_LOADED.getAttribute();

			
			
			BatchPoints batchPoints = BatchPoints
	                .database(databaseName)
	                .tag("async", "true")
	                .retentionPolicy("autogen")
	                .consistency(InfluxDB.ConsistencyLevel.ALL)
	                .tag("BatchTag", "BatchTagValue") // tag each point in the batch
	                .build();
	        Point point1 = Point.measurement("cpu")
	                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
	                .addField("idle", 90L)
	                .addField("user", 9L)
	                .addField("system", 1L)
	                .tag("CpuTag", "CpuTagValue") // tag the individual point
	                .build();
	        Point point2 = Point.measurement("disk")
	                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
	                .addField("used", 80L)
	                .addField("free", 1L)
	                .build();
	        batchPoints.point(point1);
	        batchPoints.point(point2);
	        // Write them to InfluxDB
	        influxDB.write(batchPoints);
	        Query query = new Query("SELECT * FROM cpu", databaseName);
	        QueryResult queryResult = influxDB.query(query);
	        // iterate the results and print details
	        for (QueryResult.Result result : queryResult.getResults()) {
	            // print details of the entire result
	            System.out.println(result.toString());
	            // iterate the series within the result
	            for (QueryResult.Series series : result.getSeries()) {
	                System.out.println("series.getName() = " + series.getName());
	                System.out.println("series.getColumns() = " + series.getColumns());
	                System.out.println("series.getValues() = " + series.getValues());
	                System.out.println("series.getTags() = " + series.getTags());
	            }
	        }
			
			
			setDelayExpiration(newDelayExpiration(2.0));
		}
	}
	
	public void destroy(){
		super.destroy();
		influxDB.deleteDatabase(databaseName);
	}

}
