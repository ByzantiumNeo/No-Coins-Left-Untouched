package me.mindlessly.notenoughcoins.events;

import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class OnTick {
  private static int ticks = 0;
  
  @SubscribeEvent
  public void onTick(TickEvent.ClientTickEvent event) {
    if (ticks % 20 == 0)
      Utils.updateSkyblockScoreboard(); 
    ticks++;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\events\OnTick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */