package com.josvaldor.module.mbean.statemachine.node;

import com.josvaldor.module.Module;
import com.josvaldor.utility.Utility;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class Delay
  extends Node
{
  private List<Object> objectList;
  private double delayMax = 999.0D;
  private double delay;
  
  public Delay(int id, Module module)
  {
    super(Integer.valueOf(id), module);
  }
  
  public void initialize()
  {
    this.node = false;
    super.initialize();
    this.objectList = new ArrayList();
    this.delay = this.delayMax;
    this.delayMax = Utility.stringToDouble(this.idProperties.getProperty("delayMax"));
  }
  
  protected double send()
  {
    Date nowDate = new Date(System.currentTimeMillis());
    double nowDateDouble = nowDate.getTime();
    double now = nowDateDouble / 1000.0D;
    int index = 0;
    Object object;
    do
    {
      if (((object = objectListGet(index)) instanceof Container))
      {
        Container container = (Container)object;
        if (0.0D >= container.getExpirationTime() - now)
        {
          objectListRemove(index);
          container.outputObjectListAdd(container);
        }
      }
      index++;
    } while (object != null);
    return getDelayMin();
  }
  
  protected double getDelayMin()
  {
    double delayMin = this.delayMax;
    Date nowDate = new Date(System.currentTimeMillis());
    double nowDateDouble = nowDate.getTime();
    double now = nowDateDouble / 1000.0D;
    int pendingObjectListIndex = 0;
    Object object;
    do
    {
      if (((object = objectListGet(pendingObjectListIndex)) instanceof Container))
      {
        Container container = (Container)object;
        if (container.getExpirationTime() - now < delayMin) {
          delayMin = container.getExpirationTime() - now;
        }
      }
      pendingObjectListIndex++;
    } while (object != null);
    return delayMin;
  }
  
  protected Object objectListGet(int index)
  {
    Object object = null;
    if (index < this.objectList.size()) {
      object = this.objectList.get(index);
    }
    return object;
  }
  
  protected Object objectListRemove(int index)
  {
    Object object = null;
    if (index < this.objectList.size()) {
      object = this.objectList.remove(index);
    }
    return object;
  }
  
  protected void objectListAdd(Object object)
  {
    this.objectList.add(object);
  }
  
  protected void waitForInput(Object object)
  {
    while ((0.0D >= this.delay) && (this.run)) {
      this.delay = send();
    }
    if ((object instanceof Container))
    {
      Container container;
      if ((container = (Container)object) != null) {
        objectListAdd(container);
      }
    }
    this.delay = getDelayMin();
  }
  
  protected void inputContainer(Object object)
  {
    outputObjectListAdd(new Container(this.id.intValue(), this.id.intValue(), 2, 0.0D, object, null));
  }
}
