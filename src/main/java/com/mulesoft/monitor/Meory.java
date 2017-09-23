package com.mulesoft.monitor;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.MemoryPoolMXBean;


public class Meory {
	public static void main(String[] args){
		Meory m = new Meory();
		m.getMemory();
	}

	public void getMemory(){

		ClassLoadingMXBean clBean = ManagementFactory.getClassLoadingMXBean();
		ThreadMXBean tBean = ManagementFactory.getThreadMXBean();
		List<MemoryPoolMXBean> mpBeanList = ManagementFactory.getMemoryPoolMXBeans();
		MemoryMXBean memBean = ManagementFactory.getMemoryMXBean(); 
		MemoryUsage heap = memBean.getHeapMemoryUsage();
		MemoryUsage nonHeap = memBean.getNonHeapMemoryUsage();
		
		OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
		RuntimeMXBean rtBean = ManagementFactory.getRuntimeMXBean();
		
		// Retrieve the four values stored within MemoryUsage:
		// init: Amount of memory in bytes that the JVM initially requests from the OS.
		// used: Amount of memory used.
		// committed: Amount of memory that is committed for the JVM to use.
		// max: Maximum amount of memory that can be used for memory management.
		System.err.println(String.format("Heap: Init: %d, Used: %d, Committed: %d, Max.: %d",
		  heap.getInit(), heap.getUsed(), heap.getCommitted(), heap.getMax()));
		System.err.println(String.format("Non-Heap: Init: %d, Used: %d, Committed: %d, Max.: %d",
		  nonHeap.getInit(), nonHeap.getUsed(), nonHeap.getCommitted(), nonHeap.getMax()));
		
//		tBean.getThreadCount();

		for(MemoryPoolMXBean mpBean: mpBeanList){
//			mpBean.
		}
		
		
//		clBean.getTotalLoadedClassCount()
//		clBean.getLoadedClassCount();
//		clBean.getUnloadedClassCount();
		
//		osBean.getAvailableProcessors();
//		osBean.getSystemLoadAverage();
		
		rtBean.getUptime();
	}
	
	public void query(){
		
		
		
		
		
		
		
		
	}
}
