/* 
 * Date 2014-2017
 * Author Joaquin Rodriguez
 * Copyright (c) Joaquin Osvaldo Rodriguez  All rights reserved.
 */
package com.josvaldor.entity;

//import org.apache.cayenne.CayenneDataObject;
//import org.apache.cayenne.DataObjectUtils;
import org.apache.log4j.Logger;

public class Entity
  
//extends CayenneDataObject
{
  private static final long serialVersionUID = 1L;
  public static Logger logger = Logger.getLogger(Entity.class);
  protected Object object = null;
  
//  public Integer getID()
//  {
//    Integer id = Integer.valueOf(0);
//    Object object = null;
//    if (this != null) {
//      object = DataObjectUtils.pkForObject(this);
//    }
//    if ((object instanceof Integer)) {
//      id = (Integer)object;
//    }
//    return id;
//  }
  
  public Object getObject()
  {
    logger.trace(this + ".getObject() (this.object = " + this.object + ")");
    return this.object;
  }
  
  public void setObject(Object object)
  {
    logger.trace("setObject(" + object + ")");
    this.object = object;
  }
//  
//  public boolean isNew()
//  {
//    return (getPersistenceState() == 1) || (getPersistenceState() == 2);
//  }
//  
  public String toString()
  {
    String string = getClass().getName();
    String stringPackage = getClass().getPackage().getName();
    if (stringPackage != null) {
      string = string.replaceFirst("^" + stringPackage + ".", "");
    }
    return string;
  }
}
