package me.mindlessly.notenoughcoins;

import com.google.gson.JsonElement;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import me.mindlessly.notenoughcoins.utils.ApiHandler;
import me.mindlessly.notenoughcoins.utils.Utils;

public class Tasks {
  public static Thread updateBalance;
  
  public static Thread updateBazaarItem;
  
  public static Thread updateFilters;
  
  static {
    updateBalance = new Thread(() -> {
          while (true) {
            while (Config.enabled) {
              try {
                ApiHandler.updatePurse();
                Thread.sleep(15000L);
              } catch (InterruptedException e) {
                e.printStackTrace();
              } catch (Exception e) {
                try {
                  Utils.sendMessageWithPrefix("&cFailed to update balance, please check if you set your API key correctly.");
                  Thread.sleep(60000L);
                } catch (Exception ex) {
                  ex.printStackTrace();
                } 
                e.printStackTrace();
              } 
            } 
            try {
              Thread.sleep(100L);
            } catch (InterruptedException e) {
              e.printStackTrace();
            } 
          } 
        }"Not Enough Coins Balance Updating Task");
    updateBazaarItem = new Thread(() -> {
          while (true) {
            if (Config.enabled || Config.bestSellingMethod) {
              try {
                ApiHandler.updateBazaar();
                Thread.sleep(2500L);
              } catch (Exception e) {
                e.printStackTrace();
                try {
                  Thread.sleep(60000L);
                } catch (InterruptedException ex) {
                  ex.printStackTrace();
                } 
              } 
              continue;
            } 
            try {
              Thread.sleep(100L);
            } catch (InterruptedException e) {
              e.printStackTrace();
            } 
          } 
        }"Not Enough Coins Bazaar Updating Task");
    updateFilters = new Thread(() -> {
          while (true) {
            while (Config.hideSpam) {
              List<String> filters = new LinkedList<>();
              try {
                for (Map.Entry<String, JsonElement> f : (Iterable<Map.Entry<String, JsonElement>>)Utils.getJson("https://nec.robothanzo.dev/filter").getAsJsonObject().getAsJsonObject("result").entrySet()) {
                  for (JsonElement filter : ((JsonElement)f.getValue()).getAsJsonArray())
                    filters.add(filter.getAsString().toLowerCase(Locale.ROOT)); 
                } 
                Main.chatFilters = filters;
                Thread.sleep(60000L);
              } catch (Exception e) {
                e.printStackTrace();
              } 
            } 
            try {
              Thread.sleep(100L);
            } catch (InterruptedException e) {
              e.printStackTrace();
            } 
          } 
        }"Not Enough Coins Filters Updating Task");
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\Tasks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */