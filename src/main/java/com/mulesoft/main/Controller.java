/* Controller.java
 * Date 201709
 * Author Joaquin Rodriguez
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 */
package com.mulesoft.main;


import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mulesoft.JMXBean;
import com.mulesoft.SupportedJMXBean;

@RestController
public class Controller {
    private MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
    
    @RequestMapping("/object")
    public JMXBean object(@RequestParam(value="query", defaultValue="null") String query, @RequestParam(value="attribute", defaultValue="null") String attribute ) {
    	Object value = null;
    	if(query != null && !query.equals("null") && attribute != null && !attribute.equals("null")){
    		value = this.getValue(query+"", attribute+"");
    	}
        return new JMXBean(query+"",attribute+"",value+"",System.currentTimeMillis()+"");
    }
    
	public Object getValue(String beanQuery, String attribute){
		Object value = null;
		try {
			ObjectName objectName = new ObjectName(beanQuery);
			if (mBeanServer.isRegistered(objectName)) {
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
}