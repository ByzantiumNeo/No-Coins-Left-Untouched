package me.mindlessly.notenoughcoins.utils;

import com.google.common.collect.Sets;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.essential.universal.UChat;
import gg.essential.universal.wrappers.message.UTextComponent;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import me.mindlessly.notenoughcoins.Main;
import me.mindlessly.notenoughcoins.Reference;
import me.mindlessly.notenoughcoins.objects.BestSellingMethod;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class Utils {
  private static final Set<String> SKYBLOCK_IN_ALL_LANGUAGES = Sets.newHashSet((Object[])new String[] { "SKYBLOCK", "空岛生存", "空島生存" });
  
  private static boolean hasSkyblockScoreboard;
  
  private static String formatValue(long amount, long div, char suffix) {
    return (new DecimalFormat(".##")).format(amount / div) + suffix;
  }
  
  public static JsonElement getJson(String jsonUrl) throws IOException {
    URL url = new URL(jsonUrl);
    URLConnection conn = url.openConnection();
    conn.setRequestProperty("Connection", "close");
    conn.setRequestProperty("User-Agent", "NotEnoughCoins/1.0");
    return (new JsonParser()).parse(new InputStreamReader(conn.getInputStream()));
  }
  
  public static String formatValue(long amount) {
    if (amount >= 1000000000000000L)
      return formatValue(amount, 1000000000000000L, 'q'); 
    if (amount >= 1000000000000L)
      return formatValue(amount, 1000000000000L, 't'); 
    if (amount >= 1000000000L)
      return formatValue(amount, 1000000000L, 'b'); 
    if (amount >= 1000000L)
      return formatValue(amount, 1000000L, 'm'); 
    if (amount >= 100000L)
      return formatValue(amount, 1000L, 'k'); 
    return NumberFormat.getInstance().format(amount);
  }
  
  public static void sendMessageWithPrefix(String message) {
    UChat.chat(EnumChatFormatting.GOLD + "[NEC] " + message.replaceAll("&", "§"));
  }
  
  public static void sendMessageWithPrefix(String message, ClickEvent clickEvent) {
    UTextComponent result = new UTextComponent(EnumChatFormatting.GOLD + "[NEC] " + message.replaceAll("&", "§"));
    result.func_150255_a((new ChatStyle()).func_150241_a(clickEvent));
    UChat.chat(result);
  }
  
  public static int getTax(int price) {
    float taxRate = 0.01F;
    if (price >= 1000000)
      taxRate = 0.02F; 
    return (int)Math.floor((price * taxRate));
  }
  
  public static int getTaxedProfit(int price, int profit) {
    return profit - getTax(price);
  }
  
  public static String getProfitText(int profit) {
    EnumChatFormatting color = EnumChatFormatting.GRAY;
    if (profit >= 100000)
      color = EnumChatFormatting.GOLD; 
    if (profit >= 500000)
      color = EnumChatFormatting.GREEN; 
    if (profit >= 1000000)
      color = EnumChatFormatting.DARK_GREEN; 
    if (profit >= 10000000)
      color = EnumChatFormatting.AQUA; 
    return color + "+$" + formatValue(profit);
  }
  
  public static boolean isOnSkyblock() {
    return hasSkyblockScoreboard();
  }
  
  public static boolean hasSkyblockScoreboard() {
    return hasSkyblockScoreboard;
  }
  
  public static void updateSkyblockScoreboard() {
    Minecraft mc = Minecraft.func_71410_x();
    if (mc != null && mc.field_71441_e != null && mc.field_71439_g != null) {
      if (mc.func_71356_B() || mc.field_71439_g.func_142021_k() == null || 
        !mc.field_71439_g.func_142021_k().toLowerCase().contains("hypixel")) {
        hasSkyblockScoreboard = false;
        return;
      } 
      Scoreboard scoreboard = mc.field_71441_e.func_96441_U();
      ScoreObjective sidebarObjective = scoreboard.func_96539_a(1);
      if (sidebarObjective != null) {
        String objectiveName = sidebarObjective.func_96678_d().replaceAll("(?i)\\u00A7.", "");
        for (String skyblock : SKYBLOCK_IN_ALL_LANGUAGES) {
          if (objectiveName.contains(skyblock)) {
            hasSkyblockScoreboard = true;
            return;
          } 
        } 
      } 
      hasSkyblockScoreboard = false;
    } 
  }
  
  public static String getIDFromItemStack(ItemStack stack) {
    if (stack == null)
      return null; 
    NBTTagCompound tag = stack.func_77978_p();
    String id = null;
    if (tag != null && tag.func_150297_b("ExtraAttributes", 10)) {
      NBTTagCompound ea = tag.func_74775_l("ExtraAttributes");
      if (ea.func_150297_b("id", 8)) {
        id = ea.func_74779_i("id");
      } else {
        return null;
      } 
      if ("PET".equals(id)) {
        String petInfo = ea.func_74779_i("petInfo");
        if (petInfo.length() > 0) {
          JsonObject petInfoObject = (JsonObject)(new GsonBuilder()).setPrettyPrinting().create().fromJson(petInfo, JsonObject.class);
          id = petInfoObject.get("type").getAsString() + petInfoObject.get("tier").getAsString();
        } 
      } 
      if ("ENCHANTED_BOOK".equals(id)) {
        NBTTagCompound enchants = ea.func_74775_l("enchantments");
        Iterator<String> iterator = enchants.func_150296_c().iterator();
        if (iterator.hasNext()) {
          String enchname = iterator.next();
          id = enchname.toUpperCase() + ";" + enchants.func_74762_e(enchname);
        } 
      } 
    } 
    return id;
  }
  
  public static void runInAThread(Callable<Void> callable) {
    (new Thread(() -> {
          try {
            callable.call();
          } catch (Exception e) {
            Reference.logger.error(e.getMessage(), e);
          } 
        })).start();
  }
  
  public static String removeColorCodes(String in) {
    return in.replaceAll("(?i)\\u00A7.", "");
  }
  
  public static Map.Entry<BestSellingMethod, Long> getBestSellingMethod(String id) {
    if (id == null)
      return new AbstractMap.SimpleEntry<>(BestSellingMethod.NONE, Long.valueOf(0L)); 
    if (id.equals("POTION"))
      return new AbstractMap.SimpleEntry<>(BestSellingMethod.NONE, Long.valueOf(0L)); 
    BestSellingMethod method = BestSellingMethod.NONE;
    long bestPrice = 0L;
    if (Main.lbinItem.containsKey(id) && (((Integer)Main.lbinItem.get(id)).intValue() - getTax(((Integer)Main.lbinItem.get(id)).intValue())) > bestPrice) {
      bestPrice = (((Integer)Main.lbinItem.get(id)).intValue() - getTax(((Integer)Main.lbinItem.get(id)).intValue()));
      method = BestSellingMethod.LBIN;
    } 
    if (Main.bazaarItem.containsKey(id) && ((Integer)Main.bazaarItem.get(id)).intValue() > bestPrice) {
      bestPrice = ((Integer)Main.bazaarItem.get(id)).intValue();
      method = BestSellingMethod.BAZAAR;
    } 
    if (Main.npcItem.containsKey(id) && ((Integer)Main.npcItem.get(id)).intValue() > bestPrice) {
      bestPrice = ((Integer)Main.npcItem.get(id)).intValue();
      method = BestSellingMethod.NPC;
    } 
    return new AbstractMap.SimpleEntry<>(method, Long.valueOf(bestPrice));
  }
  
  public static EnumChatFormatting getColorCodeFromRarity(String rarity) {
    switch (rarity) {
      case "COMMON":
        return EnumChatFormatting.WHITE;
      case "UNCOMMON":
        return EnumChatFormatting.GREEN;
      case "RARE":
        return EnumChatFormatting.BLUE;
      case "EPIC":
        return EnumChatFormatting.DARK_PURPLE;
      case "LEGENDARY":
        return EnumChatFormatting.GOLD;
      case "MYTHIC":
        return EnumChatFormatting.LIGHT_PURPLE;
      case "DIVINE":
        return EnumChatFormatting.AQUA;
      case "SPECIAL":
      case "VERY_SPECIAL":
        return EnumChatFormatting.RED;
    } 
    return EnumChatFormatting.WHITE;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoin\\utils\Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */