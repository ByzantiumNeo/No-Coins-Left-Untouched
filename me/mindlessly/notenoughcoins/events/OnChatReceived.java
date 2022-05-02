package me.mindlessly.notenoughcoins.events;

import java.util.Locale;
import me.mindlessly.notenoughcoins.Config;
import me.mindlessly.notenoughcoins.Main;
import me.mindlessly.notenoughcoins.Reference;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OnChatReceived {
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void chat(ClientChatReceivedEvent event) {
    String message = event.message.func_150260_c();
    if (message.startsWith("Your new API key is ")) {
      String key = message.split("key is ")[1];
      Config.apiKey = key;
      Utils.sendMessageWithPrefix("§aAPI Key set to " + key);
    } 
    for (String filter : Main.chatFilters) {
      if (message.toLowerCase(Locale.ROOT).contains(filter) && message.contains(": ")) {
        event.setCanceled(true);
        Reference.logger.info("The following message was ignored due to a chat filter: " + message);
        return;
      } 
    } 
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\events\OnChatReceived.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */