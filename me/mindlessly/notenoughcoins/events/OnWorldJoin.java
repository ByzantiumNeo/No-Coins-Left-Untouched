package me.mindlessly.notenoughcoins.events;

import java.util.Timer;
import java.util.TimerTask;
import me.mindlessly.notenoughcoins.Config;
import me.mindlessly.notenoughcoins.commands.subcommands.Toggle;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class OnWorldJoin {
  boolean hasRan = false;
  
  @SubscribeEvent
  public void onEntityJoinWorld(FMLNetworkEvent.ClientConnectedToServerEvent event) {
    Timer timer = new Timer();
    if (Config.enabled && !this.hasRan) {
      this.hasRan = true;
      timer.schedule(new TimerTask() {
            public void run() {
              Toggle.updateConfig();
            }
          },  2000L);
    } 
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\events\OnWorldJoin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */