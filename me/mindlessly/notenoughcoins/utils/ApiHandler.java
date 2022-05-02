package me.mindlessly.notenoughcoins.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.mindlessly.notenoughcoins.Authenticator;
import me.mindlessly.notenoughcoins.Config;
import me.mindlessly.notenoughcoins.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;

public class ApiHandler {
  public static void updatePurse() throws IOException {
    if (Utils.isOnSkyblock()) {
      Scoreboard scoreboard = (Minecraft.func_71410_x()).field_71441_e.func_96441_U();
      List<Score> scores = new LinkedList<>(scoreboard.func_96534_i(scoreboard.func_96539_a(1)));
      for (Score score : scores) {
        ScorePlayerTeam scorePlayerTeam = scoreboard.func_96509_i(score.func_96653_e());
        String line = Utils.removeColorCodes(ScorePlayerTeam.func_96667_a((Team)scorePlayerTeam, score.func_96653_e()));
        if (line.contains("Purse: ") || line.contains("Piggy: ")) {
          Main.balance = Double.parseDouble(line.replaceAll("\\(\\+[\\d]+\\)", "").replaceAll("[^\\d.]", ""));
          return;
        } 
      } 
    } 
    JsonArray profilesArray = ((JsonElement)Objects.<JsonElement>requireNonNull(Utils.getJson("https://api.hypixel.net/skyblock/profiles?key=" + Config.apiKey + "&uuid=" + Authenticator.myUUID))).getAsJsonObject().getAsJsonArray("profiles");
    int profileIndex = 0;
    Instant lastProfileSave = Instant.EPOCH;
    for (int i = 0; i < profilesArray.size(); i++) {
      Instant lastSaveLoop;
      try {
        lastSaveLoop = Instant.ofEpochMilli(profilesArray.get(i).getAsJsonObject().get("members")
            .getAsJsonObject().get(Authenticator.myUUID).getAsJsonObject().get("last_save").getAsLong());
      } catch (Exception e) {}
      if (lastSaveLoop.isAfter(lastProfileSave)) {
        profileIndex = i;
        lastProfileSave = lastSaveLoop;
      } 
    } 
    Main.balance = profilesArray.get(profileIndex).getAsJsonObject().get("members").getAsJsonObject().get(Authenticator.myUUID).getAsJsonObject().get("coin_purse").getAsDouble();
  }
  
  public static void updateBazaar() throws IOException {
    JsonObject products = ((JsonElement)Objects.<JsonElement>requireNonNull(Utils.getJson("https://api.hypixel.net/skyblock/bazaar"))).getAsJsonObject().getAsJsonObject("products");
    for (Map.Entry<String, JsonElement> itemEntry : (Iterable<Map.Entry<String, JsonElement>>)products.entrySet()) {
      if (((JsonElement)itemEntry.getValue()).getAsJsonObject().getAsJsonArray("sell_summary").size() > 0)
        Main.bazaarItem.put(itemEntry.getKey(), Integer.valueOf(((JsonElement)itemEntry.getValue()).getAsJsonObject().getAsJsonArray("sell_summary").get(0).getAsJsonObject().get("pricePerUnit").getAsInt())); 
    } 
  }
  
  public static Void updateNPC() throws IOException {
    JsonArray items = ((JsonElement)Objects.<JsonElement>requireNonNull(Utils.getJson("https://api.hypixel.net/resources/skyblock/items"))).getAsJsonObject().getAsJsonArray("items");
    for (JsonElement i : items) {
      JsonObject item = i.getAsJsonObject();
      if (item.has("npc_sell_price"))
        Main.npcItem.put(item.get("id").getAsString(), Integer.valueOf(item.get("npc_sell_price").getAsInt())); 
    } 
    return null;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoin\\utils\ApiHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */