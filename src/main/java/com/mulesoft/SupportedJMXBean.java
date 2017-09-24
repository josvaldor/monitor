package com.mulesoft;


/**
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */



/**
 * Monitoring JMX Metric Beans Definitions.
 *
 */
public enum SupportedJMXBean
{

    /**
     * Heap Usage.
     */
    HEAP_USAGE("java.lang:type=Memory", "HeapMemoryUsage.used", "heap used"),

    /**
     * Heap Total.
     */
    HEAP_TOTAL("java.lang:type=Memory", "HeapMemoryUsage.max", "heap total"),

    /**
     * Heap Committed.
     */
    HEAP_COMMITTED("java.lang:type=Memory", "HeapMemoryUsage.committed", "heap committed"),

    /**
     * Par Eden Usage.
     */
    EDEN_USAGE("java.lang:type=MemoryPool,name=Par Eden Space", "Usage.used", "par eden used"),

    /**
     * Par Eden Total.
     */
    EDEN_TOTAL("java.lang:type=MemoryPool,name=Par Eden Space", "Usage.max", "par eden total"),

    /**
     * Par Eden Committed.
     */
    EDEN_COMMITTED("java.lang:type=MemoryPool,name=Par Eden Space", "Usage.committed", "par eden committed"),

    /**
     * Par Survivor Usage.
     */
    SURVIVOR_USAGE("java.lang:type=MemoryPool,name=Par Survivor Space", "Usage.used", "par survivor used"),

    /**
     * Par Survivor Total.
     */
    SURVIVOR_TOTAL("java.lang:type=MemoryPool,name=Par Survivor Space", "Usage.max", "par survivor total"),

    /**
     * Par Survivor Committed.
     */
    SURVIVOR_COMMITTED("java.lang:type=MemoryPool,name=Par Survivor Space", "Usage.committed", "par survivor committed"),

    /**
     * Tenured Gen Usage.
     */
    TENURED_GEN_USAGE("java.lang:type=MemoryPool,name=Tenured Gen", "Usage.used", "tenured gen used"),

    /**
     * Tenured Gen Total.
     */
    TENURED_GEN_TOTAL("java.lang:type=MemoryPool,name=Tenured Gen", "Usage.max", "tenured gen total"),

    /**
     * Tenured Gen Committed.
     */
    TENURED_GEN_COMMITTED("java.lang:type=MemoryPool,name=Tenured Gen", "Usage.committed", "tenured gen committed"),

    /**
     * Code Cache Usage.
     */
    CODE_CACHE_USAGE("java.lang:type=MemoryPool,name=Code Cache", "Usage.used", "code cache used"),

    /**
     * Code Cache Total.
     */
    CODE_CACHE_TOTAL("java.lang:type=MemoryPool,name=Code Cache", "Usage.max", "code cache total"),

    /**
     * Code Cache Committed.
     */
    CODE_CACHE_COMMITTED("java.lang:type=MemoryPool,name=Code Cache", "Usage.committed", "code cache committed"),

    /**
     * Compressed Class Space Usage. Only Available in JDK 8+.
     */
    COMPRESSED_CLASS_SPACE_USAGE("java.lang:type=MemoryPool,name=Compressed Class Space", "Usage.used", "compressed class space used"),

    /**
     * Compressed Class Space Total. Only Available in JDK 8+.
     */
    COMPRESSED_CLASS_SPACE_TOTAL("java.lang:type=MemoryPool,name=Compressed Class Space", "Usage.max", "compressed class space total"),

    /**
     * Compressed Class Space Committed. Only Available in JDK 8+
     */
    COMPRESSED_CLASS_SPACE_COMMITTED("java.lang:type=MemoryPool,name=Compressed Class Space", "Usage.committed", "compressed class space committed"),

    /**
     * Metaspace Usage. Only Available in JDK 8+.
     */
    METASPACE_USAGE("java.lang:type=MemoryPool,name=Metaspace", "Usage.used", "metaspace used"),

    /**
     * Metaspace Total. Only Available in JDK 8+.
     */
    METASPACE_TOTAL("java.lang:type=MemoryPool,name=Metaspace", "Usage.max", "metaspace total"),

    /**
     * Metaspace Commited. Only Available in JDK 8+.
     */
    METASPACE_COMMITTED("java.lang:type=MemoryPool,name=Metaspace", "Usage.committed", "metaspace committed"),

    /**
     * Total classes loaded since JVM start.
     */
    CLASS_LOADING_TOTAL("java.lang:type=ClassLoading", "TotalLoadedClassCount", "classes loaded total"),

    /**
     * Currently loaded classes.
     */
    CLASS_LOADING_LOADED("java.lang:type=ClassLoading", "LoadedClassCount", "classes loaded"),

    /**
     * Unloaded Classes.
     */
    CLASS_LOADING_UNLOADED("java.lang:type=ClassLoading", "UnloadedClassCount", "classes unloaded"),

    /**
     * Thread Count.
     */
    THREADING_COUNT("java.lang:type=Threading", "ThreadCount", "thread count"),

    /**
     * CPU Usage.
     */
    CPU_USAGE("java.lang:type=OperatingSystem", "ProcessCpuLoad", "CPU"),

    /**
     * Mark Sweep Garbage Collection Time.
     */
    GC_MARK_SWEEP_TIME("java.lang:type=GarbageCollector,name=MarkSweepCompact", "CollectionTime", "gc markSweep collection time"),

    /**
     * Mark Sweep Garbage Collection Count.
     */
    GC_MARK_SWEEP_COUNT("java.lang:type=GarbageCollector,name=MarkSweepCompact", "CollectionCount", "gc markSweep collection count"),

    /**
     * Par New Garbage Collection Time.
     */
    GC_PAR_NEW_TIME("java.lang:type=GarbageCollector,name=ParNew", "CollectionTime", "gc par new collection time"),

    /**
     * Par New Garbage Collection Count.
     */
    GC_PAR_NEW_COUNT("java.lang:type=GarbageCollector,name=ParNew", "CollectionCount", "gc par new collection count"),

    /**
     * System Available Processors.
     */
    AVAILABLE_PROCESSORS("java.lang:type=OperatingSystem", "AvailableProcessors", "available processors"),

    /**
     * System Load Average.
     */
    LOAD_AVERAGE("java.lang:type=OperatingSystem", "SystemLoadAverage", "load average"),

    /**
     * Total system RAM. This bean will be used as a fall back for the border case where the metaspace is reported as -1.
     */
    TOTAL_PHYSICAL_MEMORY("java.lang:type=OperatingSystem", "TotalPhysicalMemorySize", "total physical memory size"),

    /**
     * JVM Uptime. This bean will be used to calculate garbage collection spent time percentage.
     */
    JVM_UPTIME("java.lang:type=Runtime", "Uptime", "jvm uptime");

    /**
     * JMX Bean Query.
     */
    private final String beanQuery;

    /**
     * JMX Bean Attribute.
     */
    private final String attribute;

    /**
     * JMX Bean Monitor Message.
     */
    private final String monitorMessage;

    /**
     * Metric Name.
     */
    private final String metricName;

    /**
     *
     * @param beanQuery JMX Bean Query
     * @param attribute JMX Bean Attribute
     * @param monitorMessage JMX Bean Monitor Message
     */
    SupportedJMXBean(String beanQuery, String attribute, String monitorMessage)
    {
        this.beanQuery = beanQuery;
        this.attribute = attribute;
        this.monitorMessage = monitorMessage;
        this.metricName = beanQuery + ":" + monitorMessage;
    }

    /**
     *
     * @return JMX Bean Attribute.
     */
    public String getAttribute()
    {
        return attribute;
    }

    /**
     *
     * @return JMX Bean Query.
     */
    public String getBeanQuery()
    {
        return beanQuery;
    }

    /**
     *
     * @return JMX Bean Attribute.
     */
    public String getMonitorMessage()
    {
        return monitorMessage;
    }

    /**
     *
     * @return Metric Name.
     */
    public String getMetricName()
    {
        return metricName;
    }

//    /**
//     *
//     * @return Represented JMX Bean.
//     */
//    public JMXBean getJMXBean()
//    {
//        return new JMXBean(this.beanQuery, this.attribute, this.monitorMessage);
//    }
}