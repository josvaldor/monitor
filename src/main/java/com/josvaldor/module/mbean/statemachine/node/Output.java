package com.josvaldor.module.mbean.statemachine.node;

import com.josvaldor.module.Module;
import com.josvaldor.protocol.Protocol;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.log4j.Logger;

public class Output
  extends Node
{
  protected OutputStream outputStream = null;
  
  public Output(int id, Module module, OutputStream outputStream)
  {
    super(Integer.valueOf(id), module);
    this.outputStream = outputStream;
  }
  
  public void initialize()
  {
    this.node = false;
    super.initialize();
  }
  
  public void destroy()
  {
    if (!this.destroy)
    {
      super.destroy();
      outputStreamClose(this.outputStream);
    }
  }
  
  public void output(Object object)
  {
    if ((object instanceof Protocol))
    {
      Protocol protocol = (Protocol)object;
      byte[] byteArray = protocol.getByteArray();
      try
      {
        this.outputStream.write(byteArray);
        this.outputStream.flush();
      }
      catch (IOException e)
      {
        logger.error("output(" + object + ") IOException");
        setState(0);
      }
    }
  }
  
  protected void waitForInput(Object object)
  {
    if ((object instanceof Container))
    {
      Container container = (Container)object;
      object = container.getObject();
      switch (container.getType())
      {
      case 1: 
        output(object);
      }
    }
  }
}
