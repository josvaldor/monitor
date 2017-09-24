package com.mulesoft.main;

public class JMXBean {
	 private final String beanQuery;

	    /**
	     * JMX Bean Attribute.
	     */
	    private final String attribute;
	
	    
	    private final String value;
	    
	    private final String time;

	    /**
	     *
	     * @param beanQuery JMX Bean Query
	     * @param attribute JMX Bean Attribute
	     * @param monitorMessage JMX Bean Monitor Message
	     */
	    public JMXBean(String beanQuery, String attribute, String value, String time)
	    {
	        this.beanQuery = beanQuery;
	        this.attribute = attribute;
	        this.value = value;
	        this.time = time;
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

	    
	    public String getValue()
	    {
	    	return value;
	    }
	    
	    public String getTime()
	    {
	    	return time;
	    }

}
