package com.josvaldor.module;

import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Module
  extends URLClassLoader
  implements ModuleInterface
{
  public static Logger logger;
  protected List<Object> inputObjectList = Collections.synchronizedList(new ArrayList());
  protected Set<Integer> idSet = Collections.synchronizedSet(new HashSet());
  protected Map<String, Module> moduleMap = Collections.synchronizedMap(new ConcurrentHashMap());
  protected int moduleMapSize = 0;
  public Thread thread = null;
  protected Module rootModule = null;
  protected Integer id = Integer.valueOf(0);
  protected volatile boolean start = true;
  protected volatile boolean run = true;
  protected volatile boolean destroy = false;
  protected volatile boolean protect = false;
  protected double now = 0.0D;
  protected double delayExpiration = 0.0D;
  public boolean interrupt = true;
  public boolean test = false;
  protected CountDownLatch countDownLatch = null;
  
  public static void main(String[] args) {
		Module module = new Module(0);
		CountDownLatch countDownLatch;
	    module.setCountDownLatch(countDownLatch = new CountDownLatch(1));
		module.start();
  }
  
  public Module()
  {
    super(new URL[0], Module.class.getClassLoader());
    setID(0);
    if (logger == null)
    {
      BasicConfigurator.configure();
      logger = Logger.getLogger(Module.class);
      logger.setLevel(Level.DEBUG);
    }
  }
  
  public Module(int id)
  {
    super(new URL[0], Module.class.getClassLoader());
    setID(id);
    if (logger == null)
    {
      BasicConfigurator.configure();
      logger = Logger.getLogger(Module.class);
      logger.setLevel(Level.DEBUG);
    }
  }
  
  public Module(URL[] urlArray)
  {
    super(urlArray, Module.class.getClassLoader());
    setID(0);
    if (logger == null)
    {
      BasicConfigurator.configure();
      logger = Logger.getLogger(Module.class);
      logger.setLevel(Level.DEBUG);
    }
  }
  
  public Module(int id, Module module)
  {
    super(module.getURLs(), Module.class.getClassLoader());
    setID(id);
    setRootModule(module);
    if (logger == null)
    {
      BasicConfigurator.configure();
      logger = Logger.getLogger(Module.class);
    }
  }
  
  public Module(Module module)
  {
    super(module.getURLs(), Module.class.getClassLoader());
    setID(0);
    setRootModule(module);
    if (logger == null)
    {
      BasicConfigurator.configure();
      logger = Logger.getLogger(Module.class);
    }
  }
  
  private void setID(int id)
  {
    this.id = Integer.valueOf(id);
    this.idSet.add(this.id);
  }
  
  public void start()
  {
    if (this.start)
    {
      this.start = false;
      if (logger.isDebugEnabled()) {
        logger.trace(this + ".start()");
      }
      this.thread = new Thread(this);
      this.thread.setName(toString());
      this.thread.start();
    }
  }
  
  public void initialize()
  {
    if (logger.isDebugEnabled()) 
	  {
      logger.trace(this + ".initialize()");
    }
  }
  
  public void run()
  {
    initialize();
    if (logger.isDebugEnabled())
    {
      logger.trace(this + ".run()");
    }
    countDownLatchCountDown();
  }
  
  public void stop()
  {
    if (logger.isDebugEnabled()) {
      logger.trace(this + ".stop()");
    }
    this.run = false;
  }
  
  public void destroy()
  {
    if (!this.destroy)
    {
      stop();
      if (logger.isDebugEnabled()) {
        logger.trace(this + ".destroy()");
      }
      this.destroy = true;
      if ((this.thread != null) && (this.interrupt)) {
        this.thread.interrupt();
      }
      moduleMapDestroy(this.moduleMap);
      if (this.rootModule != null) {
        this.rootModule.moduleMapRemove(this);
      }
    }
  }
  
  public void setCountDownLatch(CountDownLatch countDownLatch)
  {
    if (logger.isDebugEnabled()) {
      logger.debug(this + ".setCountDownLatch(" + countDownLatch.getCount() + ")");
    }
    this.countDownLatch = countDownLatch;
  }
  
  public void countDownLatchCountDown()
  {
    if (this.countDownLatch != null)
    {
      if (logger.isDebugEnabled()) {
        logger.debug(this + ".countDownLatchCountDown() (this.countDownLatch.getCount() = " + this.countDownLatch.getCount() + ")");
      }
      this.countDownLatch.countDown();
      if (logger.isDebugEnabled()) {
        logger.debug(this + ".countDownLatchCountDown() (this.countDownLatch.getCount() = " + this.countDownLatch.getCount() + ")");
      }
    }
  }
  
  public boolean getStart()
  {
    return this.start;
  }
  
  public boolean getRun()
  {
    return this.run;
  }
  
  public boolean getProtect()
  {
    return this.protect;
  }
  
  public boolean getDestroy()
  {
    return this.destroy;
  }
  
  public int getID()
  {
    return this.id.intValue();
  }
  
  public void inputObjectListAdd(Object object)
  {
    synchronized (this.inputObjectList)
    {
      if ((object instanceof List))
      {
        this.inputObjectList.addAll((List)object);
        if (logger.isDebugEnabled()) {
          logger.trace("inputObjectListAdd(" + object + ") (this.inputObjectList.size()=" + this.inputObjectList.size() + ")");
        }
      }
      else
      {
        this.inputObjectList.add(object);
      }
      this.inputObjectList.notify();
    }
  }
  
  public Object inputObjectListRemove(int index)
  {
    Object object = null;
    synchronized (this.inputObjectList)
    {
      if (index < this.inputObjectList.size()) {
        try
        {
          object = this.inputObjectList.remove(index);
        }
        catch (NoSuchElementException e)
        {
          System.err.println("inputObjectListRemove(" + index + ") NoSuchElementException");
        }
      }
      this.inputObjectList.notify();
    }
    return object;
  }
  
  public boolean inputObjectListRemoveAll()
  {
    boolean delta = false;
    synchronized (this.inputObjectList)
    {
      delta = this.inputObjectList.removeAll(this.inputObjectList);
      this.inputObjectList.notify();
    }
    return delta;
  }
  
  public void outputObjectListAdd(Object object)
  {
    if (this.rootModule != null)
    {
      if (logger.isDebugEnabled()) {
        logger.trace("outputObjectListAdd(" + object + ")");
      }
      this.rootModule.inputObjectListAdd(object);
    }
  }
  
  public Object load(Integer id, Object object)
  {
    return null;
  }
  
  public void moduleMapPut(Object object)
  {
    if ((object instanceof Module)) {
      this.moduleMap.put(((Module)object).toString(), (Module)object);
    }
  }
  
  public void moduleMapRemove(Object object)
  {
    if ((object instanceof Module)) {
      this.moduleMap.remove(((Module)object).toString());
    }
  }
  
  public boolean moduleMapContains(Object object)
  {
    boolean flag = false;
    if ((object instanceof Module)) {
      flag = this.moduleMap.containsKey(((Module)object).toString());
    }
    return flag;
  }
  
  public void setRootModule(Module rootModule)
  {
    this.rootModule = rootModule;
    if (this.rootModule != null) {
      this.rootModule.moduleMapPut(this);
    }
  }
  
  public Class<?> getURLClass(String className)
  {
    Class<?> clazz = null;
    try
    {
      clazz = loadClass(className);
    }
    catch (NoClassDefFoundError e)
    {
      System.err.println("getURLClass(" + className + ") NoClassDefFoundError");
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("getURLClass(" + className + ") ClassNotFoundException");
    }
    catch (SecurityException e)
    {
      System.err.println("getURLClass(" + className + ") SecurityException");
    }
    return clazz;
  }
  
  public List<Object> getInputObjectList()
  {
    return this.inputObjectList;
  }
  
  public Module getRootModule()
  {
    return this.rootModule;
  }
  
  public Map<String, Module> getModuleMap()
  {
    return this.moduleMap;
  }
  
  public Set<Integer> getIDSet()
  {
    return this.idSet;
  }
  
  public String toString()
  {
    String string = super.toString();
    String stringPackage = getClass().getPackage().getName();
    if (stringPackage != null) {
      string = string.replaceFirst("^" + stringPackage + ".", "");
    }
    return string;
  }
  
  protected boolean delayExpired()
  {
    boolean flag = (this.now = newNow()) > this.delayExpiration;
    return flag;
  }
  
  protected boolean delayExpired(double delayExpiration)
  {
    boolean flag = (this.now = newNow()) > delayExpiration;
    return flag;
  }
  
  protected void moduleMapInputObjectListAdd(Map<String, Module> moduleMap, Object object)
  {
    if ((moduleMap != null) && (object != null))
    {
      Set<String> moduleHashMapKeySet = moduleMap.keySet();
      Iterator<String> moduleHashMapIterator = moduleHashMapKeySet.iterator();
      while (moduleHashMapIterator.hasNext())
      {
        String string;
        Module module;
        if (((string = (String)moduleHashMapIterator.next()) != null) && 
          ((module = (Module)moduleMap.get(string)) != null) && 
          (!module.getDestroy())) {
          module.inputObjectListAdd(object);
        }
      }
    }
  }
  
  protected void moduleMapStart(Map<String, Module> moduleMap)
  {
    Set<String> moduleMapKeySet = moduleMap.keySet();
    Iterator<String> moduleHashMapIterator = moduleMapKeySet.iterator();
    while (moduleHashMapIterator.hasNext())
    {
      String string;
      if ((string = (String)moduleHashMapIterator.next()) != null)
      {
        Module module;
        if (((module = (Module)moduleMap.get(string)) != null) && 
          (module.getStart())) {
          module.start();
        }
      }
    }
  }
  
  protected void moduleMapDestroy(Map<String, Module> moduleMap)
  {
    if ((moduleMap != null) && 
      (!moduleMap.isEmpty()))
    {
      Set<String> moduleHashMapKeySet = moduleMap.keySet();
      Iterator<String> moduleHashMapIterator = moduleHashMapKeySet.iterator();
      while (moduleHashMapIterator.hasNext())
      {
        String string;
        Module module;
        if (((string = (String)moduleHashMapIterator.next()) != null) && 
          ((module = (Module)moduleMap.get(string)) != null) && 
          (!module.getDestroy())) {
          module.destroy();
        }
      }
    }
  }
  
  protected Set<Integer> moduleMapGetDestroy(Map<String, Module> moduleMap, Set<Integer> idSet)
  {
    Set<Integer> loadIDSet = Collections.synchronizedSet(new HashSet());
    if ((moduleMap != null) && (idSet != null))
    {
      loadIDSet.addAll(idSet);
      loadIDSet.remove(this.id);
      if (!moduleMap.isEmpty())
      {
        Iterator<String> moduleMapKeySetIterator = moduleMap.keySet().iterator();
        while (moduleMapKeySetIterator.hasNext())
        {
          String string;
          Module module;
          if (((string = (String)moduleMapKeySetIterator.next()) != null) && 
            ((module = (Module)moduleMap.get(string)) != null)) {
            if (!module.getDestroy())
            {
              loadIDSet.remove(Integer.valueOf(module.getID()));
            }
            else
            {
              moduleMap.remove(string);
              moduleMapKeySetIterator.remove();
            }
          }
        }
      }
    }
    return loadIDSet;
  }
  
  protected boolean moduleMapGetProtect(Map<String, Module> moduleMap)
  {
    Set<String> moduleHashMapKeySet = moduleMap.keySet();
    Iterator<String> moduleHashMapIterator = moduleHashMapKeySet.iterator();
    
    boolean flag = false;
    while (moduleHashMapIterator.hasNext())
    {
      String string;
      Module module;
      if (((string = (String)moduleHashMapIterator.next()) != null) && 
        ((module = (Module)moduleMap.get(string)) != null) && 
        (module.getProtect())) {
        flag = true;
      }
    }
    return flag;
  }
  
  protected Set<Integer> moduleMapGetIDSet(Map<String, Module> moduleMap)
  {
    Set<Integer> idSet = Collections.synchronizedSet(new HashSet());
    if ((moduleMap != null) && 
      (!moduleMap.isEmpty()))
    {
      Set<String> moduleMapKeySet = moduleMap.keySet();
      Iterator<String> moduleMapKeySetIterator = moduleMapKeySet.iterator();
      while (moduleMapKeySetIterator.hasNext())
      {
        String string;
        Module module;
        if (((string = (String)moduleMapKeySetIterator.next()) != null) && 
          ((module = (Module)moduleMap.get(string)) != null) && 
          (!module.getDestroy())) {
          idSet.add(Integer.valueOf(module.getID()));
        }
      }
    }
    return idSet;
  }
  
  protected double newDelayExpiration(double delay)
  {
    if (logger.isDebugEnabled()) {
      logger.trace("newDelayExpiration(" + delay + ")");
    }
    Date nowDate = new Date();
    double nowDateDouble = nowDate.getTime();
    double now = nowDateDouble / 1000.0D;
    return now + delay;
  }
  
  protected double newNow()
  {
    Date nowDate = new Date(System.currentTimeMillis());
    double nowDateDouble = nowDate.getTime();
    double now = nowDateDouble / 1000.0D;
    return now;
  }
  
  protected double newDate(Date date)
  {
    double dateDouble = date.getTime();
    return dateDouble / 1000.0D;
  }
  
  protected void setDelayExpiration(double delayExpiration)
  {
    this.delayExpiration = delayExpiration;
  }
  
  protected void sleep(double seconds)
  {
    if (logger.isDebugEnabled()) {
      logger.trace("sleep(" + seconds + ")");
    }
    if (seconds > 0.001D)
    {
      long milliseconds = (long)(seconds * 1000.0);
      try
      {
        Thread.sleep(milliseconds);
      }
      catch (InterruptedException e)
      {
        logger.error("sleep(" + seconds + ") InterruptedException");
      }
    }
  }
}
