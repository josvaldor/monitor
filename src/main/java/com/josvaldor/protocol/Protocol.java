package com.josvaldor.protocol;

import com.josvaldor.utility.Utility;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Protocol
  implements ProtocolInterface
{
  public static final Logger logger = Logger.getLogger(Protocol.class);
  public static final int START = 0;
  public static final int LENGTH = 1;
  public static final int VERSION = 2;
  public static final int TYPE = 3;
  public static final int OFFSET = 4;
  public static final int ACKNOWLEDGE = 5;
  public static final int DATA = 6;
  public static final int CRC = 7;
  public static final int GOOD = 8;
  public static final int BAD = 9;
  public static final int UNUSED = 1;
  public static final int ADVERTISEMENT = 2;
  public static final int MESSAGE = 3;
  public static final int CONNECT = 4;
  public static final int DISCONNECT = 5;
  public static final int RESEND_REQUEST = 6;
  protected int[] intArray = new int[0];
  protected final int startLength = 2;
  protected final int lengthLength = 2;
  protected final int versionLength = 3;
  protected final int typeLength = 1;
  protected final int offsetLength = 4;
  protected final int acknowledgeLength = 4;
  protected int dataLength = 0;
  protected final int crcLength = 2;
  protected int[] startIntArray = new int[2];
  protected int[] lengthIntArray = new int[2];
  protected int[] versionIntArray = new int[3];
  protected int[] typeIntArray = new int[1];
  protected int[] offsetIntArray = new int[4];
  protected int[] acknowledgeIntArray = new int[4];
  protected int[] dataIntArray = new int[this.dataLength];
  protected int[] crcIntArray = new int[2];
  protected int start = 18502;
  protected int length;
  protected int type;
  protected int offset = 0;
  protected int acknowledge = 0;
  protected int version = 2;
  protected int crc;
  protected int tryCount = 0;
  protected double timeout = 0.5D;
  protected int index = 0;
  protected Object object = null;
  protected int byteCount;
  protected int byteArrayLength;
  protected byte[] byteArray = new byte[0];
  protected byte[] dataByteArray = new byte[0];
  protected int state;
  protected byte address;
  protected boolean checkSum = false;
  
  public boolean getCheckSum()
  {
    return this.checkSum;
  }
  
  public void setCheckSum(boolean checkSum)
  {
    this.checkSum = checkSum;
  }
  
  public byte getAddress()
  {
    return this.address;
  }
  
  public void setAddress(byte address)
  {
    this.address = address;
  }
  
  public Object serialize(int type, int offset, int acknowledge, String data)
  {
    this.intArray = new int[0];
    this.byteArray = new byte[0];
    if ((type == 3) && 
      (data != null))
    {
      this.dataLength = data.length();
      this.length += this.dataLength;
      this.dataIntArray = Utility.byteArrayToIntArray(data.getBytes());
    }
    this.length += 2;
    this.length += 2;
    this.length += 3;
    this.length += 1;
    this.length += 4;
    this.length += 4;
    this.length += 2;
    this.startIntArray = Utility.intForwardMaskShifter(this.start, 255, 8, 2);
    this.lengthIntArray = Utility.intForwardMaskShifter(this.length, 255, 8, 2);
    this.versionIntArray = Utility.intForwardMaskShifter(this.version, 255, 8, 3);
    this.typeIntArray = Utility.intForwardMaskShifter(this.type = type, 255, 8, 1);
    this.offsetIntArray = Utility.intForwardMaskShifter(this.offset = offset, 255, 8, 4);
    this.acknowledgeIntArray = Utility.intForwardMaskShifter(this.acknowledge = acknowledge, 255, 8, 4);
    this.intArray = Utility.appendIntArrays(this.intArray, this.startIntArray);
    this.intArray = Utility.appendIntArrays(this.intArray, this.lengthIntArray);
    this.intArray = Utility.appendIntArrays(this.intArray, this.versionIntArray);
    this.intArray = Utility.appendIntArrays(this.intArray, this.typeIntArray);
    this.intArray = Utility.appendIntArrays(this.intArray, this.offsetIntArray);
    this.intArray = Utility.appendIntArrays(this.intArray, this.acknowledgeIntArray);
    if (type == 3) {
      this.intArray = Utility.appendIntArrays(this.intArray, this.dataIntArray);
    }
    this.crcIntArray = Utility.intForwardMaskShifter(computeCRC16One(this.intArray), 255, 8, 2);
    this.intArray = Utility.appendIntArrays(this.intArray, this.crcIntArray);
    this.byteArray = Utility.intArrayToByteArray(this.intArray);
    return this.byteArray;
  }
  
  public Object serialize(Object object)
  {
    if ((object instanceof byte[])) {
      setByteArray((byte[])object);
    } else if ((object instanceof String)) {
      setByteArray(((String)object).getBytes());
    }
    return object;
  }
  
  public Object deserialize(Object object)
  {
    if ((object instanceof byte[]))
    {
      object = new String(this.byteArray);
      this.state = 8;
    }
    return object;
  }
  
  public int getMessageOffset()
  {
    return this.offset;
  }
  
  public int getMessageAcknowledged()
  {
    return this.acknowledge;
  }
  
  public Object getObject()
  {
    return this.object;
  }
  
  public int getTryCount()
  {
    return this.tryCount;
  }
  
  public int getType()
  {
    return this.type;
  }
  
  public int getDataLength()
  {
    return this.dataLength;
  }
  
  public int getState()
  {
    return this.state;
  }
  
  public double getTimeout()
  {
    return this.timeout;
  }
  
  public byte[] getByteArray()
  {
    return this.byteArray;
  }
  
  public int getIndex()
  {
    if (logger.isDebugEnabled()) {
      logger.trace("getIndex() (this.index = " + this.index + ")");
    }
    return this.index;
  }
  
  public byte[] getDataByteArray()
  {
    return this.dataByteArray;
  }
  
  public List<String> parse(int[] intArray)
  {
    ArrayList<String> stringList = new ArrayList();
    int index = 0;
    
    String string = "";
    while (index < intArray.length)
    {
      int integer = intArray[index];
      if ((32 <= integer) && (integer <= 126))
      {
        string = string + (char)integer;
      }
      else if (integer == 0)
      {
        if (StringUtils.isNotBlank(string)) {
          stringList.add(string);
        }
        string = "";
      }
      index++;
    }
    return stringList;
  }
  
  public void setObject(Object object)
  {
    this.object = object;
  }
  
  public void setTryCount(int tryCount)
  {
    this.tryCount = tryCount;
  }
  
  public void setMessageOffset(int messageOffset)
  {
    this.offset = messageOffset;
  }
  
  public void setMessageAcknowledged(int messageAcknowledge)
  {
    this.acknowledge = messageAcknowledge;
  }
  
  public void setState(int state)
  {
    this.state = state;
  }
  
  public void setIndex(int index)
  {
    this.index = index;
  }
  
  public void setByteArray(byte[] byteArray)
  {
    this.byteArray = byteArray;
    if (logger.isDebugEnabled()) {
      logger.trace(this + ".setByteArray(" + byteArray + ") (byteArray = " + Utility.byteArrayToByteArrayString(this.byteArray) + ")");
    }
  }
  
  public void setDataByteArray(byte[] dataByteArray)
  {
    this.dataByteArray = dataByteArray;
    if (logger.isDebugEnabled()) {
      logger.trace(this + ".setDataByteArray(" + dataByteArray + ") (dataByteArray = " + Utility.byteArrayToByteArrayString(this.dataByteArray) + ")");
    }
  }
  
  public int setByteCount(int byteCount)
  {
    if (logger.isDebugEnabled()) {
      logger.trace("setByteCount(" + byteCount + ")");
    }
    return this.byteCount = byteCount;
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
  
  public static int computeCRC8(int[] intArray)
  {
    int crc = 0;
    
    int[] crcIntArray = { 0, 94, 188, 226, 97, 63, 221, 131, 194, 156, 126, 32, 163, 253, 31, 65, 157, 195, 33, 127, 252, 162, 64, 30, 95, 1, 227, 189, 62, 96, 130, 220, 35, 125, 159, 193, 66, 28, 254, 160, 225, 191, 93, 3, 128, 222, 60, 98, 190, 224, 2, 92, 223, 129, 99, 61, 124, 34, 192, 158, 29, 67, 161, 255, 70, 24, 250, 164, 39, 121, 155, 197, 132, 218, 56, 102, 229, 187, 89, 7, 219, 133, 103, 57, 186, 228, 6, 88, 25, 71, 165, 251, 120, 38, 196, 154, 101, 59, 217, 135, 4, 90, 184, 230, 167, 249, 27, 69, 198, 152, 122, 36, 248, 166, 68, 26, 153, 199, 37, 123, 58, 100, 134, 216, 91, 5, 231, 185, 140, 210, 48, 110, 237, 179, 81, 15, 78, 16, 242, 172, 47, 113, 147, 205, 17, 79, 173, 243, 112, 46, 204, 146, 211, 141, 111, 49, 178, 236, 14, 80, 175, 241, 19, 77, 206, 144, 114, 44, 109, 51, 209, 143, 12, 82, 176, 238, 50, 108, 142, 208, 83, 13, 239, 177, 240, 174, 76, 18, 145, 207, 45, 115, 202, 148, 118, 40, 171, 245, 23, 73, 8, 86, 180, 234, 105, 55, 213, 139, 87, 9, 235, 181, 54, 104, 138, 212, 149, 203, 41, 119, 244, 170, 72, 22, 233, 183, 85, 11, 136, 214, 52, 106, 43, 117, 151, 201, 74, 20, 246, 168, 116, 42, 200, 150, 21, 75, 169, 247, 182, 232, 10, 84, 215, 137, 107, 53 };
    for (int i = 0; i < intArray.length; i++) {
      crc = crcIntArray[(crc ^ intArray[i] & 0xFF)];
    }
    return crc;
  }
  
  public static int computeCRC8Two(int[] intArray)
  {
    int crc = 0;
    for (int i = 0; i < intArray.length; i++)
    {
      int byteInt = intArray[i];
      int bit;
      for (int j = 8; j > 0; j--)
      {
        bit = (crc ^ byteInt) & 0x1;
        crc >>= 1;
        if (bit > 0) {
          crc ^= 0x8C;
        }
        byteInt >>= 1;
      }
    }
    String intArrayString = "";
    int[] arrayOfInt = intArray;int bit = intArray.length;
    for (int j = 0; j < bit; j++)
    {
      int i = arrayOfInt[j];
      intArrayString = intArrayString + "0x" + Utility.getZeroFilledRightJustifiedString(Integer.toHexString(i & 0xFF), 2) + ",";
    }
    if (logger.isDebugEnabled()) {
      logger.debug("computeCRC8Two(" + intArray + ") (intArray = " + intArrayString + ") (crc = " + "0x" + Utility.getZeroFilledRightJustifiedString(Integer.toHexString(crc), 2) + " = " + Utility.getZeroFilledRightJustifiedString(Integer.toBinaryString(crc), 8) + "))");
    }
    return crc;
  }
  
  public static int computeCRC16Two(int[] intArray)
  {
    int crc = 65535;
    int intArrayLength = intArray.length;
    for (int i = 0; i < intArrayLength; i++)
    {
      int integer = intArray[i];
      for (int j = 0; j < 8; j++)
      {
        int feedback;
       
        if (((integer ^ crc) & 0x1) == 1) {
          feedback = 40961;
        } else {
          feedback = 0;
        }
        integer >>= 1;
        crc >>= 1;
        crc ^= feedback;
      }
    }
    return crc;
  }
  
  public static int computeCRC16One(int[] intArray)
  {
    int crc = 65535;
    int intArrayLength = intArray.length;
    for (int i = 0; i < intArrayLength; i++)
    {
      int integer = intArray[i];
      integer &= 0xFF;
      integer = (integer ^ crc) & 0xFF;
      integer = (integer ^ integer << 4) & 0xFF;
      crc = crc >> 8 ^ integer << 8 ^ integer << 3 ^ integer >> 4;
    }
    return crc;
  }
}
