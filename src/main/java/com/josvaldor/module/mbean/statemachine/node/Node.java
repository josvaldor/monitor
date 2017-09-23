package com.josvaldor.module.mbean.statemachine.node;

import com.josvaldor.module.Module;
import com.josvaldor.module.mbean.statemachine.StateMachine;
import com.josvaldor.protocol.Protocol;
import com.josvaldor.utility.Utility;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.management.MBeanServer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.extras.DOMConfigurator;

public class Node
  extends StateMachine
  implements NodeMBean
{
  public static final int LINUX = 1;
  public static final int WINDOWS = 2;
  public static final int WAIT_FOR_INPUT = 2;
  public static final int WAIT_FOR_CONNECTION = 1;
  protected double waitForConnectionDelay;
  protected double waitForInputDelay;
  protected Protocol protocol = newProtocol();
  protected Runtime runtime;
  protected Process process;
  protected Input input = null;
  protected Output output = null;
  protected Delay delay = null;
  protected Properties idProperties = null;
  protected Set<Object> idPropertiesKeySet = new HashSet();
  protected String configurationPropertiesPath = null;
  protected Properties configurationProperties = null;
  protected Set<Object> configurationPropertiesKeySet = new HashSet();
  protected String log4JPath = null;
  protected SerialPort serialPort = null;
  protected int operatingSystem = 0;
  protected byte address;
  protected String device;
  protected int dataBits;
  protected int speed;
  protected int stopBits;
  protected int parity;
  protected int flowControl;
  protected boolean log4JEncrypt;
  protected String connection = "";
  protected int tryMin = 0;
  protected int tryMax = 0;
  protected int timeout = -1;
  private boolean container = true;
  protected boolean poll = false;
  protected boolean node = true;
  protected boolean add = true;
  protected double acknowledgeDelay = 0.0D;
  
  public static void main(String[] args){
	  
	  Node node = new Node(0);
	  CountDownLatch countDownLatch;
	  node.setCountDownLatch(countDownLatch = new CountDownLatch(1));
	  node.start();
  }
  
  public Node() {}
  
  public Node(int id)
  {
    super(id);
  }
  
  public Node(URL[] urlArray, MBeanServer mBeanServer)
  {
    super(urlArray, mBeanServer);
  }
  
  public Node(Integer id, Module module)
  {
    super(id.intValue(), module);
  }
  
  @Override
  public void initialize()
  {
    super.initialize();
    this.idProperties = idPropertiesLoadFromXML(this.id.intValue());
    this.configurationProperties = configurationPropertiesLoadFromXML(this.idProperties);
    this.log4JPath = getProperty("@log4JPath");
    if (StringUtils.isNotBlank(this.log4JPath))
    {
      File log4JFile = new File(FilenameUtils.normalize(this.log4JPath));
      if (log4JFile.exists())
      {
        DOMConfigurator.configure(this.log4JPath);
        logger = Logger.getLogger(getClass());
      }
      else
      {
        byte[] log4JByteArray = readFile(getClass().getResourceAsStream(log4JFile.getName()));
        if (log4JByteArray.length > 0)
        {
          if (!writeFile(log4JFile, log4JByteArray)) {
            log4JFile.delete();
          }
        }
        else {
          logger.warn("initialize() (log4JByteArray.length = 0)");
        }
      }
    }
    else
    {
      logger = Logger.getLogger(getClass());
    }
    this.idSet.addAll(Utility.stringToIntegerSet(getProperty("idSet"), ",", "-"));
    //this.container = (Utility.stringToInteger(getProperty("container", Utility.booleanToInt(this.container))) == 1);
    //this.test = (Utility.stringToInteger(getProperty("@test", Utility.booleanToInt(this.test))) == 1);
    this.stateMap.put(Integer.valueOf(2), "WAIT_FOR_INPUT");
    this.waitForInputDelay = Utility.stringToDouble(getProperty("@waitForInputDelay"));
    this.runtime = Runtime.getRuntime();
    this.tryMax = Utility.stringToInteger(getProperty("@tryMax"));
    if (this.node)
    {
      this.operatingSystem = newOperatingSystem();
      this.timeout = Utility.stringToInteger(getProperty("@timeout"));
      this.connection = getProperty("@connection", null);
      this.acknowledgeDelay = Utility.stringToDouble(getProperty("acknowledgeDelay"));
      this.stateMap.put(Integer.valueOf(1), "WAIT_FOR_CONNECTION");
      this.waitForConnectionDelay = Utility.stringToDouble(getProperty("@waitForConnectionDelay"));
      if ((StringUtils.isNotBlank(this.connection)) && (this.connection.equals("serial")))
      {
        this.device = getProperty("#device");
        this.address = ((byte)Utility.stringToInteger(getProperty("#address")));
        this.dataBits = Utility.stringToInteger(getProperty("@dataBits"));
        this.speed = Utility.stringToInteger(getProperty("@speed"));
        this.stopBits = Utility.stringToInteger(getProperty("@stopBits"));
        this.parity = Utility.stringToInteger(getProperty("@parity"));
        this.flowControl = Utility.stringToInteger(getProperty("@flowControl"));
      }
    }
  }
  
  public void destroy()
  {
    if (!this.destroy)
    {
      this.interrupt = false;
      super.destroy();
      if (this.process != null) {
        this.process.destroy();
      }
      if (this.serialPort != null)
      {
        this.serialPort.removeEventListener();
        this.serialPort.close();
      }
      if (this.thread != null) {
        this.thread.interrupt();
      }
    }
  }
  
  public void inputObjectListAdd(Object object)
  {
    if (this.add) {
      if (this.container)
      {
        if ((object instanceof Container))
        {
          Container container = (Container)object;
          if (container.getDestinationID() == this.id.intValue())
          {
            if (logger.isDebugEnabled()) {
              logger.trace("inputObjectListAdd(" + object + ") ((container.getDestinationID()==this.id)");
            }
            if (this.idSet.contains(Integer.valueOf(container.getSourceID())))
            {
              if (logger.isDebugEnabled()) {
                logger.trace("inputObjectListAdd(" + object + ") (this.idSet.contains(container.getSourceID()))");
              }
              super.inputObjectListAdd(container);
            }
          }
        }
      }
      else {
        super.inputObjectListAdd(object);
      }
    }
  }
  
  public String getConfigurationPropertiesPath()
  {
    if (logger.isDebugEnabled()) {
      logger.trace("getConfigurationPropertiesPath() (this.configurationPropertiesPath = " + 
        this.configurationPropertiesPath + ")");
    }
    return this.configurationPropertiesPath;
  }
  
  public Properties getConfigurationProperties()
  {
    return this.configurationProperties;
  }
  
  @Override
  protected void machine(int state, Object object)
  {
    switch (state)
    {
    case 2: 
      waitForInput(object);
      break;
    case 1: 
      waitForConnection(object);
      break;
    default: 
      super.machine(state, object);
    }
  }
  
  protected void waitForConnection(Object object)
  {
    if ((object instanceof Container))
    {
      Container container = (Container)object;
      switch (container.getType())
      {
      case 10: 
        poll(container, false);
      }
    }
    if (delayExpired())
    {
      setDelayExpiration(newDelayExpiration(this.waitForConnectionDelay));
      if (getTry())
      {
        if (connection())
        {
          poll(null, true);
          setDelayExpiration(newDelayExpiration(this.waitForInputDelay));
          setState(2);
        }
      }
      else {
        setState(0);
      }
    }
  }
  
  protected void waitForInput(Object object)
  {
    if (StringUtils.isNotBlank(this.connection))
    {
      if (input())
      {
        if ((object instanceof Container))
        {
          if (logger.isDebugEnabled()) {
            logger.trace("waitForInput(" + object + ") (object instanceof Container)");
          }
          Container container = (Container)object;
          object = container.getObject();
          switch (container.getType())
          {
          case 1: 
            output(object);
            break;
          case 2: 
            input(object);
            break;
          case 1233: 
            acknowledge(object);
          case 10: 
            poll(container, this.poll);
          }
        }
      }
      else {
        setState(0);
      }
    }
    else if ((object instanceof Container))
    {
      Container container = (Container)object;
      object = container.getObject();
      switch (container.getType())
      {
      case 1: 
        output(object);
        break;
      case 2: 
        input(object);
        break;
      case 10: 
        poll(container, this.poll);
      }
    }
  }
  
  protected boolean input()
  {
    boolean input = true;
    if (this.moduleMap.size() < this.moduleMapSize)
    {
      if (logger.isDebugEnabled()) {
        logger.debug("input() (this.moduleMap.size()<this.moduleMapSize)");
      }
      input = false;
    }
    return input;
  }
  
  protected Object test(int state, Object object)
  {
    switch (state)
    {
    case 2: 
      object = waitForInputTest(object);
      break;
    case 1: 
      object = waitForConnectionTest(object);
      break;
    default: 
      object = super.test(state, object);
    }
    return object;
  }
  
  protected Object waitForConnectionTest(Object object)
  {
    return object;
  }
  
  protected Object waitForInputTest(Object object)
  {
    return object;
  }
  
  protected boolean connection()
  {
    if (logger.isDebugEnabled()) {
      logger.debug("connection()");
    }
    boolean connection = false;
    if (this.connection.equalsIgnoreCase("serial"))
    {
      CommPortIdentifier commPortIdentifier = newCommPortIdentifier(this.device);
      CommPort commPort = newCommPort(commPortIdentifier, this.timeout);
      if ((commPort instanceof SerialPort))
      {
        this.serialPort = ((SerialPort)commPort);
        this.serialPort.setDTR(false);
        serialPortEnableReceiveTimeout(this.serialPort, this.timeout);
        if (serialPortSetSerialPortParams(this.serialPort, this.speed, this.dataBits, this.stopBits, this.parity, this.flowControl)) {
          connection = connection(this.serialPort);
        }
      }
    }
    return connection;
  }
  
  protected boolean connection(Object object)
  {
    if ((object instanceof SerialPort))
    {
      SerialPort serialPort = (SerialPort)object;
      this.input = newInput(this.id.intValue(), this, getSerialPortInputStream(serialPort));
      this.output = newOutput(this.id.intValue(), this, getSerialPortOutputStream(serialPort));
      this.delay = newDelay(this.id.intValue(), this);
    }
    return connectionStart();
  }
  
  protected boolean connectionStart()
  {
    boolean flag = false;
    int count = 0;
    if (this.input != null)
    {
      count++;
      if (logger.isDebugEnabled()) {
        logger.debug("connectionStart() (this.input == " + this.input + ")");
      }
    }
    if (this.output != null)
    {
      count++;
      if (logger.isDebugEnabled()) {
        logger.debug("connectionStart() (this.output == " + this.output + ")");
      }
    }
    if (this.delay != null)
    {
      count++;
      if (logger.isDebugEnabled()) {
        logger.debug("connectionStart() (this.delay == " + this.delay + ")");
      }
    }
    CountDownLatch countDownLatch = new CountDownLatch(count);
    if (this.input != null) {
      this.input.setCountDownLatch(countDownLatch);
    }
    if (this.output != null) {
      this.output.setCountDownLatch(countDownLatch);
    }
    if (this.delay != null) {
      this.delay.setCountDownLatch(countDownLatch);
    }
    this.moduleMapSize = this.moduleMap.size();
    moduleMapStart(this.moduleMap);
    try
    {
      if (logger.isDebugEnabled()) {
        logger.debug("connectionStart() (countDownLatch.await())");
      }
      countDownLatch.await();
      flag = true;
    }
    catch (InterruptedException ie)
    {
      logger.error("connectionStart() InterruptedException");
    }
    return flag;
  }
  
  protected Input newInput(int id, Module module, InputStream inputStream)
  {
    return new Input(id, module, inputStream);
  }
  
  protected Output newOutput(int id, Module module, OutputStream outputStream)
  {
    return new Output(id, module, outputStream);
  }
  
  protected Delay newDelay(int id, Module module)
  {
    return new Delay(id, module);
  }
  
  protected Protocol newProtocol()
  {
    return new Protocol();
  }
  
  protected void output(Object object)
  {
    if (logger.isDebugEnabled()) {
      logger.debug("output(" + object + ")");
    }
    if ((object instanceof Protocol))
    {
      Protocol protocol = (Protocol)object;
      if (this.delay != null)
      {
        if (protocol.getMessageOffset() < this.protocol.getMessageOffset())
        {
          logger.warn("output(" + object + ") ((protocol.getMessageOffset() = " + protocol.getMessageOffset() + ") < (this.messegeOffset = " + this.protocol.getMessageOffset() + "))");
        }
        else if (protocol.getTryCount() > this.tryMax)
        {
          logger.warn("output(" + object + ") ((protocol.getTryCount() = " + protocol.getTryCount() + ") > (this.MAX_TRIES = " + this.tryMax + "))");
          setState(0);
        }
        else
        {
          this.output.inputObjectListAdd(new Container(this.id.intValue(), this.id.intValue(), 1, 0.0D, protocol, null));
          int protocolDataLength;
          if ((protocolDataLength = protocol.getDataLength()) > 0)
          {
            this.protocol.setMessageOffset(this.protocol.getMessageOffset() + protocolDataLength);
            protocol.setTryCount(protocol.getTryCount() + 1);
            this.delay.inputObjectListAdd(new Container(this.id.intValue(), this.id.intValue(), 1, protocol.getTimeout() * protocol.getTryCount(), protocol, this.inputObjectList));
          }
        }
      }
      else {
        this.output.inputObjectListAdd(new Container(this.id.intValue(), this.id.intValue(), 1, 0.0D, protocol, null));
      }
    }
  }
  
  protected void input(Object object)
  {
    if ((object instanceof Protocol))
    {
      Protocol protocol = (Protocol)object;
      switch (protocol.getType())
      {
      case 2: 
        protocolSetMessageAcknowledged(object);
        break;
      case 3: 
        if (protocolSetMessageAcknowledged(object)) {
          delayAcknowledge(object);
        }
        outputProtocolAdvertisement();
        break;
      case 5: 
        if (logger.isDebugEnabled()) {
          logger.debug("input(" + object + ") Protocol.DISCONNECT");
        }
        setState(0);
      }
    }
  }
  
  protected void acknowledge(Object object)
  {
    if (((object instanceof Integer)) && 
      (shouldAcknowledge(((Integer)object).intValue()))) {
      outputProtocolAdvertisement();
    }
  }
  
  protected void protocol(Protocol protocol)
  {
    inputContainer(protocol.getObject());
  }
  
  protected boolean shouldAcknowledge(int messageAcknowledged)
  {
    boolean flag = false;
    if (messageAcknowledged > this.protocol.getMessageAcknowledged()) {
      flag = true;
    }
    return flag;
  }
  
  protected void delayAcknowledge(Object object)
  {
    if ((object instanceof Protocol))
    {
      Protocol protocol = (Protocol)object;
      if (protocol.getDataLength() > 0)
      {
        protocol(protocol);
        this.delay.inputObjectListAdd(new Container(this.id.intValue(), this.id.intValue(), 1233, this.acknowledgeDelay, Integer.valueOf(protocol.getMessageOffset() + protocol.getDataLength()), this.inputObjectList));
      }
    }
  }
  
  protected boolean protocolSetMessageAcknowledged(Object object)
  {
    boolean flag = false;
    if ((object instanceof Protocol))
    {
      Protocol protocol = (Protocol)object;
      if (protocol.getMessageAcknowledged() > this.protocol.getMessageOffset()) {
        this.protocol.setMessageOffset(protocol.getMessageAcknowledged());
      }
      if (protocol.getMessageOffset() == this.protocol.getMessageAcknowledged())
      {
        this.protocol.setMessageAcknowledged(this.protocol.getMessageAcknowledged() + protocol.getDataLength());
        flag = true;
      }
    }
    return flag;
  }
  
  protected void outputProtocolDisconnect()
  {
    Protocol protocol = new Protocol();
    protocol.serialize(5, this.protocol.getMessageOffset(), this.protocol.getMessageAcknowledged(), "");
    Container container = new Container(this.id.intValue(), this.id.intValue(), 1, 0.0D, protocol, null);
    this.output.inputObjectListAdd(container);
  }
  
  protected void outputProtocolAdvertisement()
  {
    Protocol protocol = new Protocol();
    protocol.serialize(2, this.protocol.getMessageOffset(), this.protocol.getMessageAcknowledged(), "");
    this.output.inputObjectListAdd(new Container(this.id.intValue(), this.id.intValue(), 1, 0.0D, protocol, null));
  }
  
  protected void poll(Object object, boolean flag)
  {
    if (logger.isDebugEnabled()) {
      logger.debug("poll(" + object + ", " + flag + ")");
    }
    if ((object instanceof Container))
    {
      Container container = (Container)object;
      int sourceID = container.getSourceID();
      if ((sourceID != this.id.intValue()) && 
        (!container.outputObjectListAdd(new Container(sourceID, this.id.intValue(), 2, 0.0D, Boolean.valueOf(flag), null)))) {
        container = null;
      }
    }
    else
    {
      inputContainer(Boolean.valueOf(flag));
    }
  }
  
  protected void inputContainer(Object object)
  {
    if (object != null)
    {
      if (logger.isDebugEnabled()) {
        logger.trace("inputContainer(" + object + ")");
      }
      Iterator<Integer> idSetIterator = this.idSet.iterator();
      Integer id = null;
      while (idSetIterator.hasNext())
      {
        id = (Integer)idSetIterator.next();
        if (this.id != id) {
          outputObjectListAdd(new Container(id.intValue(), this.id.intValue(), 2, 0.0D, object, null));
        }
      }
    }
  }
  
  protected boolean getTry()
  {
    boolean flag = true;
    if (this.tryMax > 0) {
      if (this.tryMin < this.tryMax)
      {
        if (logger.isDebugEnabled()) {
          logger.debug("getTry() ((tryMin = " + this.tryMin + ") < (tryMax = " + this.tryMax + "))");
        }
        this.tryMin += 1;
      }
      else
      {
        if (logger.isDebugEnabled()) {
          logger.warn("getTry(" + this.tryMin + ", " + this.tryMax + ") (tryMin>=tryMax)");
        }
        flag = false;
      }
    }
    return flag;
  }
  
  protected Properties idPropertiesLoadFromXML(int id)
  {
    Properties properties = propertiesLoadFromXML(getClass().getResourceAsStream(id + ".xml"));
    if (properties == null) {
      properties = new Properties();
    } else {
      this.idPropertiesKeySet = properties.keySet();
    }
    return properties;
  }
  
  protected Properties configurationPropertiesLoadFromXML(Properties properties)
  {
    if (logger.isDebugEnabled()) {
      logger.trace("configurationPropertiesLoadFromXML(" + properties + ")");
    }
    Properties configurationProperties = new Properties();
    if (properties != null)
    {
      this.configurationPropertiesPath = properties.getProperty("configurationPropertiesPath");
      if (logger.isDebugEnabled()) {
        logger.debug("configurationPropertiesLoadFromXML(properties) (this.configurationPropertiesPath = " + 
          this.configurationPropertiesPath + ")");
      }
      if (StringUtils.isNotBlank(this.configurationPropertiesPath))
      {
        File configurationPropertiesFile = new File(this.configurationPropertiesPath);
        if (!configurationPropertiesFile.exists())
        {
          if (logger.isDebugEnabled()) {
            logger.debug("configurationPropertiesLoadFromXML(properties) (!configurationPropertiesFile.exists())");
          }
          propertiesStoreToXML(configurationProperties, this.configurationPropertiesPath, "");
        }
        else
        {
          if (logger.isDebugEnabled()) {
            logger.debug("configurationPropertiesLoadFromXML(properties) (configurationPropertiesFile.exists())");
          }
          configurationProperties = propertiesLoadFromXML(configurationPropertiesFile);
          this.configurationPropertiesKeySet = configurationProperties.keySet();
        }
      }
      else if ((this.rootModule instanceof Node))
      {
        Node node = (Node)this.rootModule;
        if (node.getConfigurationProperties() != null)
        {
          configurationProperties = node.getConfigurationProperties();
          this.configurationPropertiesKeySet = configurationProperties.keySet();
          this.configurationPropertiesPath = node.getConfigurationPropertiesPath();
        }
        else
        {
          configurationProperties = node.configurationPropertiesLoadFromXML(node.getIDProperties());
        }
      }
    }
    return configurationProperties;
  }
  
  protected int newOperatingSystem()
  {
    String osName = System.getProperty("os.name");
    int operatingSystem = -1;
    if (StringUtils.isNotBlank(osName))
    {
      osName = osName.toLowerCase();
      if (osName.startsWith("linux"))
      {
        if (logger.isDebugEnabled()) {
          logger.debug("newOperatingSystem() Node.LINUX");
        }
        operatingSystem = 1;
      }
      else if (osName.startsWith("windows"))
      {
        if (logger.isDebugEnabled()) {
          logger.debug("newOperatingSystem() Node.WINDOWS");
        }
        operatingSystem = 2;
      }
      else
      {
        logger.warn("newOperatingSystem() (osName = " + osName + ")");
      }
    }
    else
    {
      logger.error("newOperatingSystem() (StringUtils.isNotBlank(osName)==false)");
    }
    return operatingSystem;
  }
  
  public String getHostAddress()
  {
    String hostAddress = null;
    try
    {
      hostAddress = InetAddress.getLocalHost().getHostAddress();
    }
    catch (UnknownHostException e)
    {
      logger.warn("getHostAddress() UnknownHostException");
    }
    return hostAddress;
  }
  
  public String getHostName()
  {
    String hostName = null;
    try
    {
      hostName = InetAddress.getLocalHost().getHostName();
    }
    catch (UnknownHostException e)
    {
      logger.warn("getHostName() UnknownHostException");
    }
    return hostName;
  }
  
  public Properties getIDProperties()
  {
    return this.idProperties;
  }
  
  public boolean newDirectory(String directoryPath)
  {
    return newDirectory(new File(directoryPath));
  }
  
  public boolean newDirectory(File directory)
  {
    logger.info("newDirectory(" + directory + ")");
    boolean success = false;
    File parentDirectory = directory.getParentFile();
    if (!directory.exists())
    {
      if (!parentDirectory.exists()) {
        parentDirectory.mkdirs();
      }
      success = directory.mkdir();
    }
    return success;
  }
  
  public boolean newFile(String fileName)
  {
    boolean flag = false;
    if (StringUtils.isNotBlank(fileName)) {
      flag = newFile(new File(fileName));
    }
    return flag;
  }
  
  public boolean newFile(File file)
  {
    if (logger.isDebugEnabled()) {
      logger.trace("newFile(" + file + ")");
    }
    boolean success = false;
    String newFileAbsolutePath = FilenameUtils.normalize(file.getAbsolutePath());
    File newFile = new File(newFileAbsolutePath);
    if (!newFile.exists())
    {
      File parentDirectory = newFile.getParentFile();
      if ((parentDirectory != null) && 
        (!parentDirectory.exists())) {
        parentDirectory.mkdirs();
      }
      try
      {
        success = newFile.createNewFile();
      }
      catch (IOException e)
      {
        logger.error("newFile(" + file + ") IOException");
      }
    }
    else
    {
      success = true;
    }
    return success;
  }
  
  public void copyDirectory(File sourceDirectory, File destinationDirectory)
  {
    logger.info("copyDirectory(" + sourceDirectory + ", " + destinationDirectory + ")");
    File[] fileArray = getDirectoryFileArray(sourceDirectory);
    File file = null;
    String fileName = null;
    String destinationDirectoryAbsolutePath = null;
    byte[] byteArray = null;
    if ((fileArray != null) && 
      (destinationDirectory.isDirectory())) {
      for (int i = 0; i < fileArray.length; i++)
      {
        file = fileArray[i];
        fileName = file.getName();
        byteArray = readFile(file);
        destinationDirectoryAbsolutePath = destinationDirectory.getAbsolutePath();
        writeFile(destinationDirectoryAbsolutePath + File.separator + fileName, byteArray);
      }
    }
  }
  
  public boolean deleteDirectory(String directoryName)
  {
    logger.info("deleteDirectory(" + directoryName + ")");
    return deleteDirectory(new File(directoryName));
  }
  
  public boolean deleteDirectory(File directory)
  {
    logger.info("deleteDirectory(" + directory + ")");
    boolean success = false;
    if (directory.isDirectory())
    {
      String[] children = directory.list();
      for (int i = 0; i < children.length; i++)
      {
        success = deleteDirectory(new File(directory, children[i]));
        if (!success) {
          return false;
        }
      }
    }
    return directory.delete();
  }
  
  public File[] getDirectoryFileArray(String directoryName)
  {
    logger.info("getDirectoryFileArray(" + directoryName + ")");
    return getDirectoryFileArray(new File(directoryName));
  }
  
  public File[] getDirectoryFileArray(File directory)
  {
    logger.info("getDirectoryFileArray(" + directory + ")");
    File[] fileArray = null;
    if (directory.isDirectory()) {
      fileArray = directory.listFiles();
    }
    return fileArray;
  }
  
  public boolean writeFile(String fileName, byte[] byteArray)
  {
    return writeFile(new File(fileName), byteArray);
  }
  
  public boolean writeFile(File file, byte[] byteArray)
  {
    logger.info("writeFile(" + file + ", " + byteArray + ")");
    FileOutputStream fileOutputStream = null;
    boolean success = false;
    newFile(file);
    try
    {
      fileOutputStream = new FileOutputStream(file);
      BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
      bufferedOutputStream.write(byteArray, 0, byteArray.length);
      bufferedOutputStream.flush();
      fileOutputStream.flush();
      bufferedOutputStream.close();
      fileOutputStream.close();
      success = true;
    }
    catch (FileNotFoundException e)
    {
      logger.error("writeFile(" + file + ", " + byteArray + ") FileNotFoundException");
      success = false;
    }
    catch (IOException e)
    {
      logger.error("writeFile(" + file + ", " + byteArray + ") IOException");
      success = false;
    }
    return success;
  }
  
  public boolean writeEncryptedFile(File file, byte[] byteArray, char[] password)
  {
    logger.info("writeEncryptedFile(" + file + ", " + byteArray + ", password)");
    
    boolean flag = false;
    try
    {
      SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
      SecureRandom secureRandom = new SecureRandom();
      byte[] encryptedByteArray = new byte[0];
      byte[] salt = new byte[8];
      secureRandom.nextBytes(salt);
      KeySpec keySpec = new PBEKeySpec(password, salt, 1024, 256);
      SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
      SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(1, secretKeySpec);
      AlgorithmParameters algorithmParameters = cipher.getParameters();
      byte[] initializationVector = ((IvParameterSpec)algorithmParameters.getParameterSpec(IvParameterSpec.class)).getIV();
      logger.info(Integer.valueOf(initializationVector.length));
      byte[] cipherText = cipher.doFinal(byteArray);
      
      encryptedByteArray = appendByteArrays(encryptedByteArray, salt);
      encryptedByteArray = appendByteArrays(encryptedByteArray, initializationVector);
      encryptedByteArray = appendByteArrays(encryptedByteArray, cipherText);
      flag = writeFile(file, encryptedByteArray);
    }
    catch (NoSuchAlgorithmException e)
    {
      logger.error("writeEncryptedFile(" + file + ", " + byteArray + ", password) NoSuchAlgorithmException");
    }
    catch (InvalidKeySpecException e)
    {
      logger.error("writeEncryptedFile(" + file + ", " + byteArray + ", password) InvalidKeySpecException");
    }
    catch (InvalidKeyException e)
    {
      logger.error("writeEncryptedFile(" + file + ", " + byteArray + ", password) InvalidKeyException");
    }
    catch (NoSuchPaddingException e)
    {
      logger.error("writeEncryptedFile(" + file + ", " + byteArray + ", password) NoSuchPaddingException");
    }
    catch (IllegalBlockSizeException e)
    {
      logger.error("writeEncryptedFile(" + file + ", " + byteArray + ", password) IllegalBlockSizeException");
    }
    catch (BadPaddingException e)
    {
      logger.error("writeEncryptedFile(" + file + ", " + byteArray + ", password) BadPaddingException");
    }
    catch (InvalidParameterSpecException e)
    {
      logger.error("writeEncryptedFile(" + file + ", " + byteArray + ", password) InvalidParameterSpecException");
    }
    return flag;
  }
  
  public byte[] readFile(String fileName)
  {
    return readFile(new File(fileName));
  }
  
  public byte[] readFile(File file)
  {
    logger.info("readFile(" + file + ")");
    byte[] byteArray = new byte[0];
    if (file.isFile())
    {
      FileInputStream fileInputStream = null;
      try
      {
        fileInputStream = new FileInputStream(file);
        byteArray = readFile(fileInputStream, (int)file.length());
      }
      catch (FileNotFoundException e)
      {
        logger.error("readFile(" + file + ") FileNotFoundException");
      }
    }
    else
    {
      logger.error("readFile(" + file + ") (file.isFile() == false)");
    }
    return byteArray;
  }
  
  public byte[] readFile(InputStream inputStream, int fileLength)
  {
    logger.info("readFile(" + inputStream + ", " + fileLength + ")");
    byte[] byteArray = new byte[0];
    if ((inputStream != null) && (fileLength > -1))
    {
      byteArray = new byte[fileLength];
      try
      {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        bufferedInputStream.read(byteArray);
      }
      catch (IOException e)
      {
        logger.error("readFile(" + inputStream + ", " + fileLength + ") IOException");
      }
    }
    return byteArray;
  }
  
  public byte[] readFile(InputStream inputStream)
  {
    logger.info("readFile(" + inputStream + ")");
    byte[] byteArray = new byte[0];
    if (inputStream != null)
    {
      List<Byte> byteList = new ArrayList();
      byte b = -1;
      try
      {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        int integer;
        while ((integer = bufferedInputStream.read()) != -1)
        {
          byteList.add(Byte.valueOf((byte)integer));
        }
        int byteListSize = byteList.size();
        byteArray = new byte[byteListSize];
        for (int i = 0; i < byteListSize; i++) {
          byteArray[i] = ((Byte)byteList.get(i)).byteValue();
        }
      }
      catch (IOException e)
      {
        logger.error("readFile(" + inputStream + ") IOException");
      }
    }
    return byteArray;
  }
  
  public InputStream readEncryptedFile(File file, char[] password)
  {
    logger.info("readEncryptedFile(" + file + ", password)");
    
    InputStream inputStream = null;
    try
    {
      SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
      byte[] byteArray = readFile(file);
      byte[] salt = Arrays.copyOfRange(byteArray, 0, 8);
      byte[] initializationVector = Arrays.copyOfRange(byteArray, 8, 24);
      byte[] cipherText = Arrays.copyOfRange(byteArray, 24, byteArray.length);
      KeySpec keySpec = new PBEKeySpec(password, salt, 1024, 256);
      SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
      SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(2, secretKeySpec, new IvParameterSpec(initializationVector));
      String plainText = new String(cipher.doFinal(cipherText), "UTF-8");
      inputStream = new ByteArrayInputStream(plainText.getBytes());
    }
    catch (NoSuchAlgorithmException e)
    {
      logger.error("readEncryptedFile(" + file + ", password) NoSuchAlgorithmException");
    }
    catch (InvalidKeySpecException e)
    {
      logger.error("readEncryptedFile(" + file + ", password) InvalidKeySpecException");
    }
    catch (InvalidKeyException e)
    {
      logger.error("readEncryptedFile(" + file + ", password) InvalidKeyException");
    }
    catch (NoSuchPaddingException e)
    {
      logger.error("readEncryptedFile(" + file + ", password) NoSuchPaddingException");
    }
    catch (IllegalBlockSizeException e)
    {
      logger.error("readEncryptedFile(" + file + ", password) IllegalBlockSizeException");
    }
    catch (BadPaddingException e)
    {
      logger.error("readEncryptedFile(" + file + ", password) BadPaddingException");
    }
    catch (UnsupportedEncodingException e)
    {
      logger.error("readEncryptedFile(" + file + ", password) UnsupportedEncodingException");
    }
    catch (InvalidAlgorithmParameterException e)
    {
      logger.error("readEncryptedFile(" + file + ", password) InvalidAlgorithmParameterException");
    }
    return inputStream;
  }
  
  public Properties propertiesLoadFromXML(String fileName)
  {
    Properties properties = null;
    if (StringUtils.isNotBlank(fileName))
    {
      File file = new File(fileName);
      if ((file.exists()) && 
        (!file.isDirectory())) {
        properties = propertiesLoadFromXML(new File(fileName));
      }
    }
    return properties;
  }
  
  public Properties propertiesLoadFromXML(File fileName)
  {
    Properties properties = null;
    if (fileName != null) {
      try
      {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        properties = propertiesLoadFromXML(fileInputStream);
      }
      catch (FileNotFoundException e)
      {
        logger.error("propertiesLoadFromXML(" + fileName + ") FileNotFoundException");
        properties = null;
      }
    }
    return properties;
  }
  
  public Properties propertiesLoadFromXML(InputStream inputStream)
  {
    Properties properties = null;
    if (inputStream != null) {
      try
      {
        properties = new Properties();
        properties.loadFromXML(inputStream);
      }
      catch (InvalidPropertiesFormatException e)
      {
        logger.error("propertiesLoadFromXML(" + inputStream + ") InvalidPropertiesFormatException");
        properties = null;
      }
      catch (IOException e)
      {
        logger.error("propertiesLoadFromXML(" + inputStream + ") IOException");
        properties = null;
      }
    }
    return properties;
  }
  
  public synchronized boolean propertiesStoreToXML(Properties properties, String fileName, String comment)
  {
    if ((logger != null) && (logger.isDebugEnabled())) {
      logger.trace("propertiesStoreToXML(" + properties + ", " + fileName + ", " + comment + ")");
    }
    boolean success = false;
    if (newFile(fileName))
    {
      Properties sortedProperties = new Properties()
      {
        private static final long serialVersionUID = 1L;
        
        public Set<Object> keySet()
        {
          return Collections.unmodifiableSet(new TreeSet(super.keySet()));
        }
      };
      sortedProperties.putAll(properties);
      try
      {
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        sortedProperties.storeToXML(fileOutputStream, comment);
        fileOutputStream.close();
        success = true;
      }
      catch (FileNotFoundException e)
      {
        logger.error("propertiesStoreToXML(" + properties + ", " + fileName + ", " + comment + ") FileNotFoundException");
      }
      catch (IOException e)
      {
        logger.error("propertiesStoreToXML(" + properties + ", " + fileName + ", " + comment + ") IOException");
      }
    }
    return success;
  }
  
  protected String getIDProperty(String key)
  {
    return getProperty(this.idProperties, key);
  }
  
  protected String getConfigurationProperty(String key)
  {
    return getProperty(this.configurationProperties, key);
  }
  
  protected String getProperty(String key)
  {
    return getProperty(key, null);
  }
  
  protected String getProperty(String key, String defaultValue)
  {
    String property = null;
    if (StringUtils.isNotBlank(key)) {
      if (this.idPropertiesKeySet.contains(key))
      {
        property = getIDProperty(key);
      }
      else if ((this.configurationPropertiesKeySet != null) && (this.configurationPropertiesKeySet.contains(key)))
      {
        property = getConfigurationProperty(key);
      }
      else
      {
        String keyDelta = null;
        String keyClassName = Utility.getKeyClassName(getClass());
        String keyAttributeName = null;
        switch (key.charAt(0))
        {
        case '@': 
          key = key.substring(1, key.length());
          if ((this.idPropertiesKeySet != null) && (this.idPropertiesKeySet.contains(key)))
          {
            keyAttributeName = Utility.getKeyAttributeName(key);
            keyDelta = keyClassName + keyAttributeName;
          }
          break;
        case '#': 
          key = key.substring(1, key.length());
          if ((this.idPropertiesKeySet != null) && (this.idPropertiesKeySet.contains(key)))
          {
            keyAttributeName = Utility.getKeyAttributeName(key);
            keyDelta = keyClassName + this.id + keyAttributeName;
          }
          break;
        }
        if (keyDelta != null) {
          if ((this.configurationPropertiesKeySet != null) && (this.configurationPropertiesKeySet.contains(keyDelta)))
          {
            if (logger.isDebugEnabled()) {
              logger.trace("getProperty(" + key + ", " + defaultValue + ") (this.configurationPropertiesKeySet.contains(" + 
                keyDelta + "))");
            }
            property = getConfigurationProperty(keyDelta);
          }
          else
          {
            if (logger.isDebugEnabled()) {
              logger.debug("getProperty(" + key + ", " + defaultValue + 
                ") (!this.configurationPropertiesKeySet.contains(" + keyDelta + "))");
            }
            String value = getIDProperty(key);
            setConfigurationProperty(keyDelta, value);
            property = value;
          }
        }
      }
    }
    if (property == null) {
      property = defaultValue;
    }
    if (logger.isDebugEnabled()) {
      if (property != null) {
        logger.debug("getProperty(" + key + ", " + defaultValue + ") (" + key + ", " + property + ")");
      } else {
        logger.trace("getProperty(" + key + ", " + defaultValue + ") (" + key + ", " + property + ")");
      }
    }
    return property;
  }
  
  protected String getProperty(Properties properties, String key)
  {
    return properties.getProperty(key);
  }
  
  protected boolean setConfigurationProperty(String key, String valueBeta)
  {
    return setConfigurationProperty(this.configurationProperties, this.configurationPropertiesPath, key, valueBeta);
  }
  
  protected boolean setConfigurationProperty(Properties properties, String propertiesPath, String key, String value)
  {
    if (logger.isDebugEnabled()) {
      logger.debug("setConfigurationProperty(properties, " + propertiesPath + ", " + key + ", " + value + ")");
    }
    String valueAlpha = properties.getProperty(key);
    boolean flag = false;
    if (value != null)
    {
      if (valueAlpha != null)
      {
        if (!value.equals(valueAlpha))
        {
          properties.setProperty(key, value);
          flag = propertiesStoreToXML(properties, propertiesPath, Utility.formatDate("GMT", "yyyyMMddHHmmss", new Date()));
        }
      }
      else
      {
        properties.setProperty(key, value);
        flag = propertiesStoreToXML(properties, propertiesPath, Utility.formatDate("GMT", "yyyyMMddHHmmss", new Date()));
      }
    }
    else if (properties.containsKey(key))
    {
      properties.remove(key);
      flag = propertiesStoreToXML(properties, propertiesPath, Utility.formatDate("GMT", "yyyyMMddHHmmss", new Date()));
    }
    return flag;
  }
  
  protected boolean serialPortSetSerialPortParams(SerialPort serialPort, int speed, int dataBits, int stopBits, int parity, int flowControl)
  {
    boolean flag = true;
    try
    {
      serialPort.setFlowControlMode(flowControl);
      serialPort.setSerialPortParams(speed, dataBits, stopBits, parity);
    }
    catch (UnsupportedCommOperationException e)
    {
      logger.error("serialPortSetSerialPortParams(" + serialPort + ", " + speed + ", " + dataBits + ", " + stopBits + ", " + parity + ", " + 
        flowControl + ")");
      flag = false;
    }
    return flag;
  }
  
  protected void serialPortEnableReceiveTimeout(SerialPort serialPort, int milliseconds)
  {
    if (serialPort != null) {
      try
      {
        serialPort.enableReceiveTimeout(milliseconds);
      }
      catch (UnsupportedCommOperationException e)
      {
        logger.error("serialPortEnableReceiveTimeout(" + milliseconds + ") UnsupportedCommOperationException");
      }
    }
  }
  
  protected InputStream getSerialPortInputStream(SerialPort serialPort)
  {
    InputStream inputStream = null;
    try
    {
      inputStream = serialPort.getInputStream();
    }
    catch (IOException e)
    {
      logger.error("getSerialPortInputStream(" + serialPort + ") IOException");
    }
    return inputStream;
  }
  
  protected OutputStream getSerialPortOutputStream(SerialPort serialPort)
  {
    OutputStream outputStream = null;
    try
    {
      outputStream = serialPort.getOutputStream();
    }
    catch (IOException e)
    {
      logger.error("getSerialPortOutputStream(" + serialPort + ") IOException");
    }
    return outputStream;
  }
  
  protected CommPortIdentifier newCommPortIdentifier(String device)
  {
    CommPortIdentifier commPortIdentifier = null;
    if (StringUtils.isNotBlank(device))
    {
      switch (this.operatingSystem)
      {
      case 1: 
        if (logger.isDebugEnabled()) {
          logger.debug("newCommPortIdentifier(" + device + ") LINUX");
        }
        deleteLinuxLockFile(device);
        device = "/dev/" + device;
        break;
      case 2: 
        if (logger.isDebugEnabled()) {
          logger.debug("newCommPortIdentifier(" + device + ") WINDOWS");
        }
        break;
      }
      if (logger.isDebugEnabled()) {
        logger.debug("newCommPortIdentifier(" + device + ") default");
      }
      Enumeration<?> portIdentifiers = CommPortIdentifier.getPortIdentifiers();
      while (portIdentifiers.hasMoreElements())
      {
        CommPortIdentifier newCommPortIdentifier = (CommPortIdentifier)portIdentifiers.nextElement();
        if ((newCommPortIdentifier.getPortType() == 1) && 
          (newCommPortIdentifier.getName().equals(device)))
        {
          commPortIdentifier = newCommPortIdentifier;
          break;
        }
      }
    }
    return commPortIdentifier;
  }
  
  protected CommPort newCommPort(CommPortIdentifier commPortIdentifier, int timeout)
  {
    CommPort commPort = null;
    if (commPortIdentifier != null) {
      try
      {
        commPort = commPortIdentifier.open(toString(), timeout);
      }
      catch (PortInUseException e)
      {
        logger.error("newCommPort(" + commPortIdentifier + "," + timeout + ") PortInUseException");
      }
    }
    return commPort;
  }
  
  protected String newFileURL(String path, String fileName)
  {
    String fileURL = "";
    if ((!StringUtils.isBlank(path)) && (!StringUtils.isBlank(fileName))) {
      fileURL = "file:" + path + "/" + fileName;
    }
    return fileURL;
  }
  
  protected static InputStream getProcessInputStream(Process process)
  {
    InputStream inputStream = null;
    inputStream = process.getInputStream();
    return inputStream;
  }
  
  protected static OutputStream getProcessOutputStream(Process process)
  {
    OutputStream outputStream = null;
    outputStream = process.getOutputStream();
    return outputStream;
  }
  
  protected static InputStream getProcessErrorStream(Process process)
  {
    InputStream inputStream = null;
    inputStream = process.getErrorStream();
    return inputStream;
  }
  
  protected Process getProcess(String command)
  {
    return newProcess(command);
  }
  
  protected Process newProcess(String command)
  {
    if (logger.isDebugEnabled()) {
      logger.trace("newProcess(" + command + ")");
    }
    Process process = null;
    if (StringUtils.isNotBlank(command)) {
      try
      {
        process = this.runtime.exec(command);
      }
      catch (IOException e)
      {
        logger.error("newProcess(" + command + ") IOException");
        process = null;
      }
    }
    return process;
  }
  
  protected int processWaitFor(Process process)
  {
    int exitValue = -1;
    try
    {
      exitValue = process.waitFor();
      if (logger.isDebugEnabled()) {
        logger.trace("processWaitFor(" + process + ") (exitValue = " + exitValue + ")");
      }
    }
    catch (InterruptedException e)
    {
      logger.error("processWaitFor(" + process + ") InterruptedException");
      exitValue = -1;
    }
    return exitValue;
  }
  
  protected void inputStreamClose(InputStream inputStream)
  {
    if (logger.isDebugEnabled()) {
      logger.trace(this + ".inputStreamClose(" + inputStream + ")");
    }
    if (inputStream != null) {
      try
      {
        inputStream.close();
      }
      catch (IOException e)
      {
        logger.warn(this + ".inputStreamClose(" + inputStream + ") IOException");
      }
    } else {
      logger.warn(this + ".inputStreamClose(" + inputStream + ") (inputStream = " + inputStream + ")");
    }
  }
  
  protected void outputStreamClose(OutputStream outputStream)
  {
    if (logger.isDebugEnabled()) {
      logger.trace(this + ".outputStreamClose(" + outputStream + ")");
    }
    if (outputStream != null) {
      try
      {
        outputStream.close();
      }
      catch (IOException e)
      {
        logger.warn(this + ".outputStreamClose(" + outputStream + ") IOException");
      }
    } else {
      logger.warn(this + ".outputStreamClose(" + outputStream + ") (outputStream = null)");
    }
  }
  
  protected int execute(String command)
  {
    if (logger.isDebugEnabled()) {
      logger.trace("execute(" + command + ")");
    }
    int processWaitFor = -1;
    this.process = getProcess(command);
    if (this.process != null)
    {
      BufferedReader bufferedReaderInputStream = new BufferedReader(new InputStreamReader(getProcessInputStream(this.process)));
      BufferedReader bufferedReaderErrorStream = new BufferedReader(new InputStreamReader(getProcessErrorStream(this.process)));
      try
      {
        String line;
        while (!StringUtils.isBlank(line = bufferedReaderInputStream.readLine()))
        {
          
          if (logger.isDebugEnabled()) {
            logger.trace("execute(" + command + ") (line = " + line + ")");
          }
        }
        while (!StringUtils.isBlank(line = bufferedReaderErrorStream.readLine())) {
          logger.error("execute(" + command + ") (line = " + line + ")");
        }
      }
      catch (IOException e)
      {
        logger.error("execute(" + command + ") IOException");
      }
      processWaitFor = processWaitFor(this.process);
      if (processWaitFor > 0) {
        logger.warn("execute(" + command + ") (processWaitFor = " + processWaitFor + ")");
      } else if (logger.isDebugEnabled()) {
        logger.trace("execute(" + command + ") (processWaitFor = " + processWaitFor + ")");
      }
    }
    return processWaitFor;
  }
  
  private void deleteLinuxLockFile(String device)
  {
    String fileName = "/var/lock/LCK.." + device;
    File file = new File(fileName);
    file.setWritable(true);
    file.setExecutable(true);
    file.setReadable(true);
    if (file.exists())
    {
      if (logger.isDebugEnabled()) {
        logger.debug("deleteLinuxLockFile(" + device + ") (lockFile.exists())");
      }
      file.delete();
    }
    else if (logger.isDebugEnabled())
    {
      logger.info("deleteLinuxLockFile(" + device + ") (!this.lockFile.exists())");
    }
  }
  
  private byte[] appendByteArrays(byte[] byteArray, byte[] postByteArray)
  {
    byte[] one = byteArray;
    byte[] two = postByteArray;
    byte[] combined = new byte[one.length + two.length];
    for (int i = 0; i < combined.length; i++) {
      combined[i] = (i < one.length ? one[i] : two[(i - one.length)]);
    }
    return combined;
  }
}
