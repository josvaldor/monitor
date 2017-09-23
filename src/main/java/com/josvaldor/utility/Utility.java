package com.josvaldor.utility;

import com.josvaldor.entity.Entity;
import com.josvaldor.protocol.Protocol;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Utility
{
  protected static Logger logger = Logger.getLogger(Utility.class);
  
  public static String formatDate(String timeZone, String pattern, Date date)
  {
    String string = "";
    if ((StringUtils.isNotBlank(timeZone)) && (StringUtils.isNotBlank(pattern)) && (date != null))
    {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
      simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
      string = simpleDateFormat.format(date);
    }
    return string;
  }
  
  public static String getBlankFilledRightJustifiedString(String value, int length)
  {
    String string = "";
    if (length > 0) {
      if (value != null) {
        string = String.format("%" + length + "s", new Object[] { value });
      } else {
        string = String.format("%" + length + "s", new Object[] { "" });
      }
    }
    return string;
  }
  
  public static String getBlankFilledLeftJustifiedString(String value, int length)
  {
    String string = "";
    if (length > 0) {
      if (value != null) {
        string = String.format("%-" + length + "s", new Object[] { value });
      } else {
        string = String.format("%-" + length + "s", new Object[] { "" });
      }
    }
    return string;
  }
  
  public static String getZeroFilledRightJustifiedString(String value, int length)
  {
    String string = "";
    if (length > 0) {
      if (value != null) {
        string = String.format("%" + length + "s", new Object[] { value }).replace(' ', '0');
      } else {
        string = String.format("%" + length + "s", new Object[] { "" }).replace(' ', '0');
      }
    }
    return string;
  }
  
  public static String getZeroFilledLeftJustifiedString(String value, int length)
  {
    String returnString = "";
    if (length > 0) {
      if (value != null) {
        returnString = String.format("%-" + length + "s", new Object[] { value }).replace(' ', '0');
      } else {
        returnString = String.format("%-" + length + "s", new Object[] { "" }).replace(' ', '0');
      }
    }
    return returnString;
  }
  
  public static String getBlankFilledRightJustifiedString(Integer value, int length)
  {
    String string = getBlankFilledRightJustifiedString(value, length);
    return string;
  }
  
  public static String getBlankFilledLeftJustifiedString(Integer value, int length)
  {
    String string = getBlankFilledLeftJustifiedString(value, length);
    return string;
  }
  
  public static String getZeroFilledRightJustifiedString(Integer value, int length)
  {
    String string = getZeroFilledRightJustifiedString(value, length);
    return string;
  }
  
  public static String getZeroFilledLeftJustifiedString(Integer value, int length)
  {
    String string = getZeroFilledLeftJustifiedString(value, length);
    return string;
  }
  
  public static String getBlankFilledRightJustifiedString(double value, int length)
  {
    String string = getBlankFilledRightJustifiedString(value, length);
    return string;
  }
  
  public static String getBlankFilledLeftJustifiedString(double value, int length)
  {
    String string = getBlankFilledLeftJustifiedString(value, length);
    return string;
  }
  
  public static String getZeroFilledRightJustifiedString(double value, int length)
  {
    String string = getZeroFilledRightJustifiedString(value, length);
    return string;
  }
  
  public static String getZeroFilledLeftJustifiedString(double value, int length)
  {
    String string = getZeroFilledLeftJustifiedString(value, length);
    return string;
  }
  
  public static String stringArrayToString(String[] stringArray)
  {
    String string = "";
    String[] arrayOfString = stringArray;int j = stringArray.length;
    for (int i = 0; i < j; i++)
    {
      String s = arrayOfString[i];
      
      string = string + s;
    }
    return string;
  }
  
  public static List<String> stringToStringList(String string, String delimeter)
  {
    StringTokenizer stringTokenizer = new StringTokenizer(string, delimeter);
    int countTokens = stringTokenizer.countTokens();
    List<String> stringList = new ArrayList(stringTokenizer.countTokens());
    for (int i = 0; i < countTokens; i++) {
      if (stringTokenizer.hasMoreTokens()) {
        stringList.add(stringTokenizer.nextToken());
      }
    }
    return stringList;
  }
  
  public static String stringListToString(List<String> stringList, String delimeter)
  {
    String string = "";
    for (int i = 0; i < stringList.size(); i++) {
      if (i == 0) {
        string = string + (String)stringList.get(i);
      } else {
        string = string + delimeter + (String)stringList.get(i);
      }
    }
    return string;
  }
  
  public static String[] stringToStringArray(String string, String delimeter)
  {
    String[] stringArray = new String[0];
    if (string != null)
    {
      StringTokenizer stringTokenizer = new StringTokenizer(string, delimeter);
      int countTokens = stringTokenizer.countTokens();
      stringArray = new String[stringTokenizer.countTokens()];
      Arrays.fill(stringArray, " ");
      for (int i = 0; i < countTokens; i++) {
        if (stringTokenizer.hasMoreTokens()) {
          stringArray[i] = stringTokenizer.nextToken();
        }
      }
    }
    return stringArray;
  }
  
  public static Set<Integer> stringToIntegerSet(String string, String delimeter, String rangeDelimeter)
  {
    String[] valueArray = stringToStringArray(string, delimeter);
    Set<Integer> integerSet = new HashSet();
    String[] arrayOfString1;
    int j = (arrayOfString1 = valueArray).length;
    for (int i = 0; i < j; i++)
    {
      String v = arrayOfString1[i];
      if (v.matches("\\d+" + rangeDelimeter + "\\d+"))
      {
        String[] hyphenArray = stringToStringArray(v, "-");
        if (hyphenArray.length == 2)
        {
          int a = stringToInteger(hyphenArray[0]);
          int b = stringToInteger(hyphenArray[1]);
          if ((a > 0) && (a < b)) {
            for (int z = a; z <= b; z++) {
              integerSet.add(Integer.valueOf(z));
            }
          }
        }
      }
      else if (v.matches("\\d+"))
      {
        int a = stringToInteger(v);
        integerSet.add(Integer.valueOf(a));
      }
    }
    return integerSet;
  }
  
  public static short stringToShort(String string)
  {
    short s = 0;
    try
    {
      s = (short)Integer.decode(string).intValue();
    }
    catch (NumberFormatException e)
    {
      logger.error("stringToShort(" + string + ") NumberFormatException");
    }
    return s;
  }
  
  public static int stringToInt(String string)
  {
    int s = 0;
    try
    {
      s = Integer.decode(string).intValue();
    }
    catch (NumberFormatException e)
    {
      logger.error("stringToInt(" + string + ") NumberFormatException");
    }
    return s;
  }
  
  public static byte stringToByte(String string)
  {
    byte b = 0;
    Matcher matcher = Pattern.compile("((?:0x)?\\d+)").matcher(string);
    if (matcher.matches()) {
      if (matcher.group(1) != null) {
        try
        {
          b = Integer.decode(matcher.group(1)).byteValue();
        }
        catch (NumberFormatException e)
        {
          logger.error("stringToByte(" + string + ") NumberFormatException");
        }
      }
    }
    return b;
  }
  
  public static String stringAddDelimeter(String string, String delimeter, int charCount)
  {
    logger.info("stringAddDelimeter(" + string + "," + delimeter + "," + charCount + ")");
    int i = 0;
    if ((string.length() > charCount) && (charCount > 0))
    {
      int length = string.length() + (string.length() / charCount - 1);
      while (string.length() < length) {
        if (i + charCount < string.length())
        {
          string = string.substring(0, i + charCount) + ":" + string.substring(i + charCount, string.length());
          i += charCount + 1;
        }
      }
    }
    return string;
  }
  
  public static String[] stringRemoveDelimeter(String string, String delimeter)
  {
    logger.info("stringRemoveDelimeter(" + string + "," + delimeter + ")");
    StringTokenizer stringTokenizer = new StringTokenizer(string, delimeter);
    int countTokens = stringTokenizer.countTokens();
    String[] stringArray = new String[stringTokenizer.countTokens()];
    Arrays.fill(stringArray, " ");
    for (int i = 0; i < countTokens; i++) {
      if (stringTokenizer.hasMoreTokens()) {
        stringArray[i] = stringTokenizer.nextToken();
      }
    }
    return stringArray;
  }
  
  public static String stringAddZeroPrefix(String string, int length)
  {
    logger.info("stringAddZeroPrefix(" + string + "," + length + ")");
    while (string.length() < length) {
      string = "0" + string;
    }
    return string;
  }
  
  public static String stringRemoveZeroPrefix(String string)
  {
    logger.info("stringRemoveZeroPrefix(" + string + ")");
    while (string.indexOf('0') == 0) {
      string = string.substring(1, string.length());
    }
    return string;
  }
  
  public static List<String> stringArrayToList(String[] stringArray)
  {
    return Arrays.asList(stringArray);
  }
  
  public static Set<Short> stringToShortSet(String string, String delimeter, String rangeDelimeter)
  {
    String[] valueArray = stringToStringArray(string, delimeter);
    Set<Short> integerSet = new HashSet();
    String[] arrayOfString1;
    int j = (arrayOfString1 = valueArray).length;
    for (int i = 0; i < j; i++)
    {
      String v = arrayOfString1[i];
      if (v.matches("\\d+" + rangeDelimeter + "\\d+"))
      {
        String[] hyphenArray = stringToStringArray(v, "-");
        if (hyphenArray.length == 2)
        {
          short a = stringToShort(hyphenArray[0]);
          short b = stringToShort(hyphenArray[1]);
          if ((a > 0) && (a < b)) {
            for (short z = a; z <= b; z = (short)(z + 1)) {
              integerSet.add(Short.valueOf(z));
            }
          }
        }
      }
      else if (v.matches("\\d+"))
      {
        short a = stringToShort(v);
        integerSet.add(Short.valueOf(a));
      }
    }
    return integerSet;
  }
  
  public static String formatNumber(Integer value, String format)
  {
    String valueString = null;
    DecimalFormat decimalFormat = new DecimalFormat(format);
    valueString = decimalFormat.format(value);
    return valueString;
  }
  
  public static int stringToInteger(String string)
  {
    int value = 0;
    if (StringUtils.isNotBlank(string))
    {
      string = string.trim();
      try
      {
        value = Integer.parseInt(string);
      }
      catch (NumberFormatException e)
      {
        if ((logger != null) && 
          (logger.isDebugEnabled())) {
          logger.debug("stringToInteger(" + string + ") NumberFormatException");
        }
      }
    }
    return value;
  }
  
  public static BigDecimal stringToBigDecimal(String string)
  {
    return new BigDecimal(string);
  }
  
  public static int booleanToInt(boolean bool)
  {
    return bool ? 1 : 0;
  }
  
  public static int[] byteArrayToIntArray(byte[] byteArray)
  {
    int[] intArray = new int[0];
    if (byteArray != null)
    {
      intArray = new int[byteArray.length];
      for (int i = 0; i < byteArray.length; i++) {
        intArray[i] = byteToInteger(byteArray[i]);
      }
    }
    return intArray;
  }
  
  public static String formatDouble(double value, String format)
  {
    String string = "";
    DecimalFormat decimalFormat = new DecimalFormat(format);
    string = decimalFormat.format(value);
    return string;
  }
  
  public static String formatDoubleNoDecimal(double value, String format)
  {
    String string = null;
    String valueStringNoDecimal = "";
    DecimalFormat decimalFormat = new DecimalFormat(format);
    string = decimalFormat.format(value);
    int indexOfDecimal = string.indexOf('.');
    int valueStringLength = string.length();
    for (int i = 0; i < valueStringLength; i++) {
      if (i != indexOfDecimal) {
        valueStringNoDecimal = valueStringNoDecimal + string.charAt(i);
      }
    }
    return valueStringNoDecimal;
  }
  
  public static double stringToDouble(String string)
  {
    double value = -1.0D;
    if (string != null)
    {
      string = string.trim();
      try
      {
        value = Double.parseDouble(string);
        if (value < 0.0D) {
          value = -1.0D;
        }
      }
      catch (NumberFormatException e)
      {
        if (Entity.logger != null) {
          Entity.logger.trace("Entity.stringToDouble(" + string + ") NumberFormatException");
        }
      }
    }
    return value;
  }
  
  public static String newHashString()
  {
    String newHashString = newHashString(newUUIDString(), 15);
    return newHashString;
  }
  
  public static String newHashString(String seed, int length)
  {
    StringBuilder hashStringBuilder = new StringBuilder();
    String hash = "";
    if (length > -1) {
      try
      {
        while (hash.length() < length)
        {
          StringBuilder stringBuilder = new StringBuilder();
          MessageDigest messageDigest = MessageDigest.getInstance("MD5");
          byte[] messageDigestByteArray = messageDigest.digest((seed + hash).getBytes("UTF-8"));
          byte[] arrayOfByte1;
          int j = (arrayOfByte1 = messageDigestByteArray).length;
          for (int i = 0; i < j; i++)
          {
            byte b = arrayOfByte1[i];
            stringBuilder.append(b & 0xFF);
          }
          String string = stringBuilder.toString();
          hashStringBuilder.append(string);
          hash = hashStringBuilder.toString();
        }
        if (hash.length() > length) {
          hash = hash.substring(0, length);
        }
      }
      catch (NoSuchAlgorithmException e)
      {
        Entity.logger.error("newHashString(...) NoSuchAlgorithmException");
      }
      catch (UnsupportedEncodingException e)
      {
        Entity.logger.error("newHashString(...) UnsupportedEncodingException");
      }
    }
    return hash;
  }
  
  public static UUID newUUID()
  {
    return UUID.randomUUID();
  }
  
  public static String newUUIDString()
  {
    return newUUID().toString();
  }
  
  public static String getKeyAttributeName(String string)
  {
    if (StringUtils.isNotBlank(string)) {
      if (string.charAt(0) == '^')
      {
        if (string.length() > 1)
        {
          string = string.substring(1, string.length());
          string = string.toUpperCase();
        }
      }
      else
      {
        StringBuilder stringBuilder = new StringBuilder();
        boolean lowerCaseFlag = false;
        Character character = null;
        Character characterDelta = null;
        char[] charArray = string.toCharArray();
        int charArrayLength = charArray.length;
        int delta = 1;
        int sum = 0;
        for (int i = 0; i < charArrayLength; i++)
        {
          character = Character.valueOf(charArray[i]);
          if (!lowerCaseFlag) {
            if (Character.isLowerCase(character.charValue()))
            {
              sum = i + delta;
              if (sum < charArrayLength)
              {
                characterDelta = Character.valueOf(charArray[sum]);
                if (Character.isLowerCase(characterDelta.charValue())) {
                  lowerCaseFlag = true;
                }
                character = Character.valueOf(Character.toUpperCase(character.charValue()));
              }
              else
              {
                character = Character.valueOf(Character.toUpperCase(character.charValue()));
              }
            }
            else
            {
              lowerCaseFlag = true;
            }
          }
          stringBuilder.append(character);
        }
        string = stringBuilder.toString();
        if (Entity.logger.isDebugEnabled()) {
          Entity.logger.trace("getKeyAttributeName(" + string + ")");
        }
      }
    }
    return string;
  }
  
  public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value)
  {
    Set<T> keys = new HashSet();
    for (Map.Entry<T, E> entry : map.entrySet()) {
      if (value.equals(entry.getValue())) {
        keys.add(entry.getKey());
      }
    }
    return keys;
  }
  
  public static String getClassName(Class<?> clazz)
  {
    String string = clazz.getName();
    String stringPackage = clazz.getPackage().getName();
    if (stringPackage != null) {
      string = string.replaceFirst("^" + stringPackage + ".", "");
    }
    return string;
  }
  
  public static String getKeyClassName(Class<?> clazz)
  {
    String string = getClassName(clazz);
    StringBuilder stringBuilder = new StringBuilder();
    boolean lowerFlag = false;
    Character character = null;
    Character characterDelta = null;
    char[] charArray = string.toCharArray();
    int charArrayLength = charArray.length;
    int delta = 1;
    int sum = 0;
    for (int i = 0; i < charArrayLength; i++)
    {
      character = Character.valueOf(charArray[i]);
      if (!lowerFlag) {
        if (Character.isUpperCase(character.charValue()))
        {
          sum = i + delta;
          if (sum < charArrayLength)
          {
            characterDelta = Character.valueOf(charArray[sum]);
            if (Character.isLowerCase(characterDelta.charValue())) {
              lowerFlag = true;
            }
            character = Character.valueOf(Character.toLowerCase(character.charValue()));
          }
          else
          {
            character = Character.valueOf(Character.toLowerCase(character.charValue()));
          }
        }
        else
        {
          lowerFlag = true;
        }
      }
      stringBuilder.append(character);
    }
    string = stringBuilder.toString();
    if (Entity.logger.isDebugEnabled()) {
      Entity.logger.trace("getKeyClassName(" + clazz + ") (string = " + string + ")");
    }
    return string;
  }
  
  public static String getCurrencySymbol(Map<Integer, String> currencyCodeMap, Integer numericCurrencyCode)
  {
    String currencySymbol = null;
    if ((currencyCodeMap != null) && (!currencyCodeMap.isEmpty()) && 
      (numericCurrencyCode != null))
    {
      String alphabeticCurrencyCode = (String)currencyCodeMap.get(numericCurrencyCode);
      if (alphabeticCurrencyCode != null) {
        try
        {
          Currency currency = Currency.getInstance(alphabeticCurrencyCode);
          currencySymbol = currency.getSymbol();
        }
        catch (IllegalArgumentException e)
        {
          logger.error("getCurrencySymbol(" + numericCurrencyCode + ") IllegalArgumentException");
        }
      }
    }
    return currencySymbol;
  }
  
  public static byte[] intArrayToByteArray(int[] intArray)
  {
    byte[] byteArray = new byte[0];
    if ((intArray instanceof int[]))
    {
      byteArray = new byte[intArray.length];
      for (int j = 0; j < intArray.length; j++) {
        byteArray[j] = ((byte)intArray[j]);
      }
    }
    return byteArray;
  }
  
  public static String byteArrayToByteArrayString(byte[] byteArray)
  {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < byteArray.length; i++)
    {
      if (i == 0) {
        stringBuilder.append("{");
      }
      stringBuilder.append(byteToHexString(byteArray[i]));
      if (i == byteArray.length - 1) {
        stringBuilder.append("}");
      } else {
        stringBuilder.append(",");
      }
    }
    return stringBuilder.toString();
  }
  
  public static String byteToHexString(byte b)
  {
    return "0x" + getZeroFilledRightJustifiedString(Integer.toHexString(byteToInteger(b)), 2);
  }
  
  public static int[] appendIntArrays(int[] intArrayA, int[] intArrayB)
  {
    int[] intArrayAB = new int[0];
    if (((intArrayA instanceof int[])) && ((intArrayB instanceof int[])))
    {
      intArrayAB = new int[intArrayA.length + intArrayB.length];
      for (int i = 0; i < intArrayAB.length; i++) {
        intArrayAB[i] = (i < intArrayA.length ? intArrayA[i] : intArrayB[(i - intArrayA.length)]);
      }
    }
    return intArrayAB;
  }
  
  public static byte[] appendByteArrays(byte[] byteArrayA, byte[] byteArrayB)
  {
    byte[] byteArrayAB = new byte[0];
    if (((byteArrayA instanceof byte[])) && ((byteArrayB instanceof byte[])))
    {
      byteArrayAB = new byte[byteArrayA.length + byteArrayB.length];
      for (int i = 0; i < byteArrayAB.length; i++) {
        byteArrayAB[i] = (i < byteArrayA.length ? byteArrayA[i] : byteArrayB[(i - byteArrayA.length)]);
      }
    }
    return byteArrayAB;
  }
  
  public static int[] intForwardMaskShifter(int integer, int bitMask, int shiftBitCount, int count)
  {
    int countIndex = 0;
    int j = 0;
    int k = 0;
    int[] intArray = new int[count];
    while (countIndex < count)
    {
      j = integer >> shiftBitCount * countIndex;
      k = j & bitMask;
      intArray[countIndex] = k;
      countIndex++;
    }
    return intArray;
  }
  
  public static int[] intComplement(int integer)
  {
    int[] intArray = new int[2];
    integer &= 0xFF;
    int c = integer >> 4;
    int nibbleOne = c << 4 | c ^ 0xF;
    c = integer & 0xF;
    int nibbleTwo = c << 4 | c ^ 0xF;
    intArray[0] = nibbleOne;
    intArray[1] = nibbleTwo;
    if (Protocol.logger.isDebugEnabled()) {
      Protocol.logger.debug("intComplement(0x" + getZeroFilledRightJustifiedString(Integer.toHexString(integer), 2) + ") (nibbleOne = " + "0x" + getZeroFilledRightJustifiedString(Integer.toHexString(nibbleOne), 2) + ") (nibbleTwo = " + "0x" + getZeroFilledRightJustifiedString(Integer.toHexString(nibbleTwo), 2) + ")");
    }
    return intArray;
  }
  
  public static int intUncomplement(int[] intArray)
  {
    int integer = 0;
    if (intArray.length == 2)
    {
      int nibbleOne = 0xF0 & intArray[0];
      int nibbleTwo = (0xF0 & intArray[1]) >> 4;
      integer = nibbleOne | nibbleTwo;
      if (Protocol.logger.isDebugEnabled()) {
        Protocol.logger.debug("intUncomplement(" + intArray + ") ((0xf0&(" + "0x" + getZeroFilledRightJustifiedString(Integer.toHexString(intArray[0]), 2) + ")) = nibbleOne = " + "0x" + getZeroFilledRightJustifiedString(Integer.toHexString(nibbleOne), 2) + ") ((0xf0&(" + "0x" + getZeroFilledRightJustifiedString(Integer.toHexString(intArray[1]), 2) + "))>>4) = nibbleTwo = " + "0x" + getZeroFilledRightJustifiedString(Integer.toHexString(nibbleTwo), 2) + ") (integer = " + "0x" + getZeroFilledRightJustifiedString(Integer.toHexString(integer), 2) + ")");
      }
    }
    else
    {
      Protocol.logger.warn("intUncomplement(" + intArray + ") (intArray.length != 2)");
    }
    return integer;
  }
  
  public static int intReverseMaskShifter(int[] intArray, int shift)
  {
    int countIndex = 0;
    int segment = 0;
    int integer = 0;
    int count = intArray.length;
    while (countIndex < count)
    {
      segment = intArray[countIndex] << shift * countIndex;
      integer += segment;
      countIndex++;
    }
    return integer;
  }
  
  public static int byteToInteger(byte b)
  {
    int integer = b & 0xFF;
    return integer;
  }
}
