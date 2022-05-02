package gov.hypixelqol;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class au {
  private static final Object[] w;
  
  private static final Class[] J;
  
  private static final int[] y;
  
  private static final short[] n;
  
  private static final int O = Integer.parseInt(ql.Z("蓴थ?⹍ꃿ䓙릴ꡋ㰟".toCharArray(), (short)21532, (short)1, (short)1));
  
  private static final int d = Integer.parseInt(ql.Z("룊?ᴄ䣜饝뢋౒謸壈넛ҏ".toCharArray(), (short)481, (short)4, (short)1));
  
  static {
    w = new Object[16];
    J = new Class[7];
    y = new int[16];
    "⋗쇈ⴊ줩拋퍍Ҧ♩洿ꐩ꫗置ខ諵̙௙謅㏎棛ꌹ芕冸뭭룷援ᝠ訂훮㸝".toCharArray()[28] = (char)("⋗쇈ⴊ줩拋퍍Ҧ♩洿ꐩ꫗置ខ諵̙௙謅㏎棛ꌹ芕冸뭭룷援ᝠ訂훮㸝".toCharArray()[28] ^ 0x640C);
    char[] arrayOfChar = ql.Z("⋗쇈ⴊ줩拋퍍Ҧ♩洿ꐩ꫗置ខ諵̙௙謅㏎棛ꌹ芕冸뭭룷援ᝠ訂훮㸝".toCharArray(), (short)10163, (short)3, (short)4).toCharArray();
    byte b;
    for (b = 0; b < 16; b++)
      y[b] = arrayOfChar[b * 2] | arrayOfChar[b * 2 + 1] << 16; 
    n = new short[16];
    "??ⱂ‫钉蜺?끯맛诿몍騨ᓂ溡".toCharArray()[8] = (char)("??ⱂ‫钉蜺?끯맛诿몍騨ᓂ溡".toCharArray()[8] ^ 0x5004);
    arrayOfChar = ql.Z("??ⱂ‫钉蜺?끯맛诿몍騨ᓂ溡".toCharArray(), (short)5256, (short)1, (short)4).toCharArray();
    for (b = 0; b < 16; b++)
      n[b] = (short)arrayOfChar[b]; 
  }
  
  private static Class D(int paramInt1, int paramInt2) {
    int i = n[paramInt1] & 0xFFFF;
    i = (i + paramInt2) % 7;
    Class clazz = J[i];
    if (clazz != null)
      return clazz; 
    clazz = Xq(i);
    J[i] = clazz;
    return clazz;
  }
  
  private static Class Xq(int paramInt) {
    String str;
    switch (paramInt) {
      case 0:
        "उⵎᴦ♢椵㐖枉ﯔ홄赴ᅔꔄ泫蕋恌ﱬ腟攅쬁櫓콜㈭凡箳".toCharArray()[2] = (char)("उⵎᴦ♢椵㐖枉ﯔ홄赴ᅔꔄ泫蕋恌ﱬ腟攅쬁櫓콜㈭凡箳".toCharArray()[2] ^ 0x2D64);
        str = s.A("उⵎᴦ♢椵㐖枉ﯔ홄赴ᅔꔄ泫蕋恌ﱬ腟攅쬁櫓콜㈭凡箳".toCharArray(), (short)27817, (short)2, false);
        return Class.forName(str);
      case 1:
        "孟꾭햘禛ꢞ琂㫬쬢c歡ᕖ磬㡻㻉₋큢ﺔ㳃綾劵瀡".toCharArray()[4] = (char)("孟꾭햘禛ꢞ琂㫬쬢c歡ᕖ磬㡻㻉₋큢ﺔ㳃綾劵瀡".toCharArray()[4] ^ 0x7E94);
        str = s.A("孟꾭햘禛ꢞ琂㫬쬢c歡ᕖ磬㡻㻉₋큢ﺔ㳃綾劵瀡".toCharArray(), (short)30241, (short)2, true);
        return Class.forName(str);
      case 2:
        "䣴?瘝ꌊ஽혘ᜰ따볕䞘෩땹嗖".toCharArray()[8] = (char)("䣴?瘝ꌊ஽혘ᜰ따볕䞘෩땹嗖".toCharArray()[8] ^ 0x140F);
        str = s.A("䣴?瘝ꌊ஽혘ᜰ따볕䞘෩땹嗖".toCharArray(), (short)10308, (short)0, true);
        return Class.forName(str);
      case 3:
        "溘八哙盇窏䶒ତᕡ틱叾櫔".toCharArray()[2] = (char)("溘八哙盇窏䶒ତᕡ틱叾櫔".toCharArray()[2] ^ 0x78C7);
        str = s.A("溘八哙盇窏䶒ତᕡ틱叾櫔".toCharArray(), (short)326, (short)0, true);
        return Class.forName(str);
      case 4:
        "秩絻㚴Ҹ汱쨶㐗量嚉溺뵦蛩ᒋ개歉賒訄熙䠫훭伄麃傉굓彥ﮛⱵ넞ẜ掀".toCharArray()[23] = (char)("秩絻㚴Ҹ汱쨶㐗量嚉溺뵦蛩ᒋ개歉賒訄熙䠫훭伄麃傉굓彥ﮛⱵ넞ẜ掀".toCharArray()[23] ^ 0x6155);
        str = s.A("秩絻㚴Ҹ汱쨶㐗量嚉溺뵦蛩ᒋ개歉賒訄熙䠫훭伄麃傉굓彥ﮛⱵ넞ẜ掀".toCharArray(), (short)1836, (short)5, true);
        return Class.forName(str);
      case 5:
        "댌ਅⶆ钞籠⿆䝥슕鴯뙬囌毬⋸ⷩ⎗䣞俟".toCharArray()[13] = (char)("댌ਅⶆ钞籠⿆䝥슕鴯뙬囌毬⋸ⷩ⎗䣞俟".toCharArray()[13] ^ 0x664F);
        str = s.A("댌ਅⶆ钞籠⿆䝥슕鴯뙬囌毬⋸ⷩ⎗䣞俟".toCharArray(), (short)17587, (short)3, false);
        return Class.forName(str);
      case 6:
        "韠䧫쭠㶦歡嵨銝͙懮훑谐⚻囘믵前ꊅ왫᳧桷".toCharArray()[1] = (char)("韠䧫쭠㶦歡嵨銝͙懮훑谐⚻囘믵前ꊅ왫᳧桷".toCharArray()[1] ^ 0x454B);
        str = s.A("韠䧫쭠㶦歡嵨銝͙懮훑谐⚻囘믵前ꊅ왫᳧桷".toCharArray(), (short)11827, (short)2, true);
        return Class.forName(str);
    } 
    throw new NoClassDefFoundError(Integer.toString(paramInt));
  }
  
  static Method e(int paramInt) {
    paramInt = ((paramInt + -1002188260 ^ O) + 1181870271 ^ 0xEC39B044) + d;
    int i = paramInt >>> 16;
    paramInt &= 0xFFFF;
    Method method = (Method)w[paramInt];
    if (method != null)
      return method; 
    Class clazz1 = D(paramInt, i);
    Class clazz2 = clazz1;
    int j = y[paramInt];
    while (clazz1 != null) {
      Method[] arrayOfMethod = clazz1.isInterface() ? clazz1.getMethods() : clazz1.getDeclaredMethods();
      for (Method method1 : arrayOfMethod) {
        int k = i * 31 + method1.getName().hashCode();
        k = 31 * k + 40;
        Class[] arrayOfClass = method1.getParameterTypes();
        for (byte b = 0; b < arrayOfClass.length; b++) {
          Class clazz = arrayOfClass[b];
          if (b != 0)
            k = 31 * k + 44; 
          k = 31 * k + clazz.getName().hashCode();
        } 
        k = 31 * k + 41;
        k = 31 * k + method1.getReturnType().getName().hashCode();
        k = 31 * k + i;
        if (j == k) {
          method1.setAccessible(true);
          w[paramInt] = method1;
          return method1;
        } 
      } 
      clazz1 = clazz1.getSuperclass();
    } 
    for (clazz1 = clazz2; clazz1 != null; clazz1 = clazz1.getSuperclass()) {
      Class[] arrayOfClass = clazz1.getInterfaces();
      for (Class clazz : arrayOfClass) {
        Method[] arrayOfMethod = clazz.getMethods();
        for (Method method1 : arrayOfMethod) {
          int k = i * 31 + method1.getName().hashCode();
          k = 31 * k + 40;
          Class[] arrayOfClass1 = method1.getParameterTypes();
          for (byte b = 0; b < arrayOfClass1.length; b++) {
            Class clazz3 = arrayOfClass1[b];
            if (b != 0)
              k = 31 * k + 44; 
            k = 31 * k + clazz3.getName().hashCode();
          } 
          k = 31 * k + 41;
          k = 31 * k + method1.getReturnType().getName().hashCode();
          k = 31 * k + i;
          if (j == k) {
            method1.setAccessible(true);
            w[paramInt] = method1;
            return method1;
          } 
        } 
      } 
    } 
    return null;
  }
  
  static Object n(int paramInt, Object[] paramArrayOfObject) {
    try {
      Method method = e(paramInt);
      if (method == null)
        throw new NoSuchMethodError(Integer.toString(paramInt)); 
      return method.invoke((Object)null, paramArrayOfObject);
    } catch (InvocationTargetException invocationTargetException) {
      throw invocationTargetException.getTargetException();
    } 
  }
  
  static Object M(Object paramObject, int paramInt, Object[] paramArrayOfObject) {
    try {
      Method method = e(paramInt);
      if (method == null)
        throw new NoSuchMethodError(Integer.toString(paramInt)); 
      return method.invoke(paramObject, paramArrayOfObject);
    } catch (InvocationTargetException invocationTargetException) {
      throw invocationTargetException.getTargetException();
    } 
  }
  
  static Object T(int paramInt, Object[] paramArrayOfObject) {
    try {
      paramInt = ((paramInt + -1002188260 ^ O) + 1181870271 ^ 0xEC39B044) + d;
      int i = paramInt >>> 16;
      paramInt &= 0xFFFF;
      Class clazz = D(paramInt, i);
      Constructor constructor = (Constructor)w[paramInt];
      if (constructor == null) {
        int j = y[paramInt];
        Constructor[] arrayOfConstructor = (Constructor[])clazz.getDeclaredConstructors();
        for (Constructor constructor1 : arrayOfConstructor) {
          int k = i * 31 + 40;
          Class[] arrayOfClass = constructor1.getParameterTypes();
          for (byte b = 0; b < arrayOfClass.length; b++) {
            Class clazz1 = arrayOfClass[b];
            if (b != 0)
              k = 31 * k + 44; 
            k = 31 * k + clazz1.getName().hashCode();
          } 
          k = 31 * k + 41;
          k = 31 * k + i;
          if (j == k) {
            constructor1.setAccessible(true);
            w[paramInt] = constructor1;
            constructor = constructor1;
            break;
          } 
        } 
      } 
      if (constructor == null)
        throw new NoSuchMethodError(Integer.toString(paramInt)); 
      return constructor.newInstance(paramArrayOfObject);
    } catch (InvocationTargetException invocationTargetException) {
      throw invocationTargetException.getTargetException();
    } 
  }
  
  private static Field i(int paramInt) throws Throwable {
    paramInt = ((paramInt + -1002188260 ^ O) + 1181870271 ^ 0xEC39B044) + d;
    int i = paramInt >>> 16;
    paramInt &= 0xFFFF;
    Class clazz1 = D(paramInt, i);
    Class clazz2 = clazz1;
    Field field = (Field)w[paramInt];
    if (field != null)
      return field; 
    int j = y[paramInt];
    while (clazz1 != null) {
      Field[] arrayOfField = clazz1.getDeclaredFields();
      for (Field field1 : arrayOfField) {
        int k = 31 * i + field1.getName().hashCode();
        k = 31 * k + 58;
        k = 31 * k + field1.getType().getName().hashCode();
        k = 31 * k + i;
        if (j == k) {
          field1.setAccessible(true);
          w[paramInt] = field1;
          return field1;
        } 
      } 
      clazz1 = clazz1.getSuperclass();
    } 
    for (clazz1 = clazz2; clazz1 != null; clazz1 = clazz1.getSuperclass()) {
      Class[] arrayOfClass = clazz1.getInterfaces();
      for (Class clazz : arrayOfClass) {
        Field[] arrayOfField = clazz.getFields();
        for (Field field1 : arrayOfField) {
          int k = 31 * i + field1.getName().hashCode();
          k = 31 * k + 58;
          k = 31 * k + field1.getType().getName().hashCode();
          k = 31 * k + i;
          if (j == k) {
            field1.setAccessible(true);
            w[paramInt] = field1;
            return field1;
          } 
        } 
        clazz1 = clazz1.getSuperclass();
      } 
    } 
    return null;
  }
  
  static Object I(Object paramObject, int paramInt) {
    Field field = i(paramInt);
    if (field == null)
      throw new NoSuchFieldError(Integer.toString(paramInt)); 
    return field.get(paramObject);
  }
  
  static void M(Object paramObject1, int paramInt, Object paramObject2) throws Throwable {
    Field field = i(paramInt);
    if (field == null)
      throw new NoSuchFieldError(Integer.toString(paramInt)); 
    field.set(paramObject1, paramObject2);
  }
  
  static Object n(int paramInt) {
    return I(null, paramInt);
  }
  
  static void P(int paramInt, Object paramObject) {
    M((Object)null, paramInt, paramObject);
  }
  
  static {
    "蓴थ?⹍ꃿ䓙릴ꡋ㰟".toCharArray()[6] = (char)("蓴थ?⹍ꃿ䓙릴ꡋ㰟".toCharArray()[6] ^ 0x5627);
  }
  
  static {
    "룊?ᴄ䣜饝뢋౒謸壈넛ҏ".toCharArray()[9] = (char)("룊?ᴄ䣜饝뢋౒謸壈넛ҏ".toCharArray()[9] ^ 0x6C59);
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\gov\hypixelqol\au.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */