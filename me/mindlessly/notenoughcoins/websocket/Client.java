package me.mindlessly.notenoughcoins.websocket;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.essential.universal.USound;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import me.mindlessly.notenoughcoins.Authenticator;
import me.mindlessly.notenoughcoins.Config;
import me.mindlessly.notenoughcoins.Main;
import me.mindlessly.notenoughcoins.Reference;
import me.mindlessly.notenoughcoins.objects.AverageItem;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ResourceLocation;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class Client extends WebSocketClient {
  private Date lastPing;
  
  public long latency = -1L;
  
  public Client(URI serverUri, Map<String, String> httpHeaders) {
    super(serverUri, httpHeaders);
    (new Timer()).schedule(new TimerTask() {
          public void run() {
            Client.this.lastPing = new Date();
            Client.this.send("{\"type\":\"ping\"}");
          }
        },  1000L, 30000L);
  }
  
  public static void connectWithToken() {
    Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", Main.authenticator.getToken());
    try {
      (new Client(new URI("wss://nec.robothanzo.dev/ws"), headers)).connect();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    } 
  }
  
  public void onOpen(ServerHandshake handshakedata) {
    Reference.logger.info("Websocket connection established");
  }
  
  public void onMessage(String message) {
    Date start = new Date();
    JsonObject json = (new JsonParser()).parse(message).getAsJsonObject();
    if (json.has("type"))
      switch (json.get("type").getAsString()) {
        case "profit":
          if (Config.enabled && (!Config.onlySkyblock || Utils.isOnSkyblock()))
            for (JsonElement element : json.getAsJsonArray("result")) {
              JsonObject item = element.getAsJsonObject();
              String auctionID = item.get("auction_id").getAsString();
              String itemID = item.get("id").getAsString();
              if (!Main.processedItem.containsKey(auctionID)) {
                Main.processedItem.put(auctionID, new Date(item.get("end").getAsLong()));
                if (!Config.categoryFilter.contains(item.get("category").getAsString().toUpperCase(Locale.ROOT)) && !Arrays.<String>asList(Config.blacklistedIDs.split("\n")).contains(item.get("id").getAsString())) {
                  int demand, price = item.get("price").getAsInt();
                  int profit = Utils.getTaxedProfit(price, item.get("profit").getAsInt());
                  try {
                    demand = ((AverageItem)Main.averageItemMap.get(itemID)).demand;
                  } catch (NullPointerException e) {
                    Main.processedItem.remove(auctionID);
                    continue;
                  } 
                  double profitPercentage = profit / price;
                  if (price <= Main.balance && profit >= Config.minProfit && profitPercentage >= Config.minProfitPercentage && demand >= Config.minDemand) {
                    if (!Config.manipulationCheck || (price + item.get("profit").getAsInt()) * 0.6D <= ((AverageItem)Main.averageItemMap.get(itemID)).ahAvgPrice) {
                      if (!Authenticator.myUUID.toLowerCase(Locale.ROOT).replaceAll("-", "").equals(item.get("auctioneer").getAsString())) {
                        Utils.sendMessageWithPrefix(Utils.getColorCodeFromRarity(item.get("rarity").getAsString()) + item.get("item_name").getAsString() + "&e " + 
                            Utils.getProfitText(profit) + " &eP: &a" + 
                            Utils.formatValue(price) + " &ePP: &a" + 
                            (int)Math.floor(profitPercentage * 100.0D) + "% &eSPD: &a" + demand + " " + (Config.debug ? ("\n&eCL: &a" + item
                            
                            .get("cache_latency").getAsInt() + "ms") : "") + " " + (Config.debug ? ("&eAL: &a" + item
                            .get("api_latency").getAsInt() + "ms") : "") + " " + (Config.debug ? ("&eWL: &a" + this.latency + "ms") : "") + " " + (Config.debug ? ("&ePL: &a" + ((new Date())
                            
                            .getTime() - start.getTime()) + "ms") : "") + " " + (Config.debug ? ("&eAA: &a" + 
                            Utils.formatValue(((AverageItem)Main.averageItemMap.get(itemID)).ahAvgPrice)) : "") + " " + (Config.debug ? ("&eLBIN: &a" + 
                            Utils.formatValue(((Integer)Main.lbinItem.get(itemID)).intValue())) : "") + " " + ((profit >= 100000) ? "\n" : ""), new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewauction " + auctionID));
                        if (Config.alertSounds && !Main.justPlayedASound) {
                          Main.justPlayedASound = true;
                          USound.INSTANCE.playSoundStatic(new ResourceLocation("note.pling"), 2.0F, 1.0F);
                        } 
                      } 
                      continue;
                    } 
                    Reference.logger.info("Failed manipulation check for " + item.get("item_name").getAsString() + " price " + price + " profit " + profit + " avg " + ((AverageItem)Main.averageItemMap.get(item.get("id").getAsString())).ahAvgPrice);
                  } 
                } 
              } 
            }  
          Main.justPlayedASound = false;
          return;
        case "lowest_bin":
          for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)json.getAsJsonObject("result").entrySet())
            Main.lbinItem.put(entry.getKey(), Integer.valueOf(((JsonElement)entry.getValue()).getAsInt())); 
          return;
        case "average":
          for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)json.getAsJsonObject("result").entrySet()) {
            String item = entry.getKey();
            JsonObject itemDetails = ((JsonElement)entry.getValue()).getAsJsonObject();
            int sampledDays = itemDetails.getAsJsonPrimitive("sampled_days").getAsInt();
            int ahSales = Math.floorDiv(itemDetails.getAsJsonObject("auction").getAsJsonPrimitive("sales").getAsInt(), sampledDays);
            int ahAvgPrice = (int)Math.floor(itemDetails.getAsJsonObject("auction").getAsJsonPrimitive("average_price").getAsDouble());
            int binSales = Math.floorDiv(itemDetails.getAsJsonObject("bin").getAsJsonPrimitive("sales").getAsInt(), sampledDays);
            Main.averageItemMap.put(item, new AverageItem(item, ahSales + binSales, ahAvgPrice));
          } 
          return;
        case "pong":
          if (this.lastPing != null)
            this.latency = ((new Date()).getTime() - this.lastPing.getTime()) / 2L; 
          break;
      }  
  }
  
  public void onClose(int code, String reason, boolean remote) {
    Reference.logger.warn("Websocket connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: " + reason);
    if (reason.contains("418"))
      Utils.sendMessageWithPrefix("&cFailed to fetch from NEC backend, this might be caused by:\n&cYou have been blacklisted from the mod for using macro scripts\n&cPlease join our discord server for more information (in /nec > links)"); 
    if (reason.contains("401")) {
      Reference.logger.warn("Token expired, fetching new token");
      try {
        Main.authenticator.authenticate(false);
      } catch (Exception e) {
        e.printStackTrace();
      } 
    } 
    if (code != 1001) {
      connectWithToken();
      try {
        Thread.sleep(1500L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } 
      Reference.logger.warn("Restarting connection due to abnormal close");
    } 
  }
  
  public void onError(Exception ex) {
    Reference.logger.error("Websocket error", ex);
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\websocket\Client.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */