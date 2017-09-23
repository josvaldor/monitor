package com.josvaldor.module.mbean.statemachine.node;

import com.josvaldor.module.Module;
import com.josvaldor.protocol.Protocol;
import com.josvaldor.utility.Utility;
import java.io.IOException;
import java.io.InputStream;
import org.apache.log4j.Logger;

public class Input
  extends Node
{
  protected InputStream inputStream = null;
  protected int byteArrayLength = 0;
  protected byte[] byteArray = new byte[0];
  protected double waitForInputMinDelay = 2.0D;
  protected double waitForInputMaxDelay = 10.0D;
  
  public Input(int id, Module module, InputStream inputStream)
  {
    super(Integer.valueOf(id), module);
    this.inputStream = inputStream;
  }
  
  public void initialize()
  {
    this.node = false;
    super.initialize();
    this.waitForInputMinDelay = Utility.stringToDouble(getProperty("@waitForInputMinDelay"));
    this.waitForInputMaxDelay = Utility.stringToDouble(getProperty("@waitForInputMaxDelay"));
    this.byteArrayLength = Utility.stringToInteger(getProperty("@byteArrayLength"));
  }
  
  public void destroy()
  {
    if (!this.destroy)
    {
      super.destroy();
      inputStreamClose(this.inputStream);
    }
  }
  
  public void input(Object object)
  {
    try
    {
      if (this.inputStream.read(this.byteArray = new byte[this.byteArrayLength]) != -1)
      {
        setDelayExpiration(newDelayExpiration(this.waitForInputMinDelay));
        object = this.protocol.deserialize(this.byteArray);
        if ((object instanceof Protocol))
        {
          this.protocol = ((Protocol)object);
          switch (this.protocol.getState())
          {
          case 8: 
            this.poll = true;
            inputContainer(this.protocol);
            break;
          case 9: 
            this.protocol = newProtocol();
          }
        }
      }
    }
    catch (IOException e)
    {
      logger.fatal("input(object) IOException");
      setState(0);
    }
    if (delayExpired()) {
      switch (this.state)
      {
      case 2: 
        if (this.poll)
        {
          this.poll = false;
          inputContainer(Boolean.valueOf(this.poll));
          setDelayExpiration(newDelayExpiration(this.waitForInputMaxDelay));
          logger.warn("input() (this.setDelayExpiration(this.newDelayExpiration(" + this.waitForInputMaxDelay + ")))");
        }
        else
        {
          logger.warn("input() this.delayExpired()");
          setState(0);
        }
        break;
      }
    }
  }
  
  protected void waitForInput(Object object)
  {
    input(object);
  }
  
  protected void inputContainer(Object object)
  {
    if (object != null)
    {
      if (logger.isDebugEnabled()) {
        logger.trace("inputContainer(" + object + ")");
      }
      outputObjectListAdd(new Container(this.id.intValue(), this.id.intValue(), 2, 0.0D, object, null));
    }
  }
}
