package me.mindlessly.notenoughcoins.events;

import gg.essential.api.EssentialAPI;
import me.mindlessly.notenoughcoins.utils.updater.GitHub;
import me.mindlessly.notenoughcoins.utils.updater.UpdateAvailableScreen;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OnGuiOpen {
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onGuiOpen(GuiOpenEvent event) {
    if (event.gui == null || event.gui.getClass() != GuiMainMenu.class)
      return; 
    if (!GitHub.shownGUI) {
      GitHub.fetchLatestRelease();
      if (!GitHub.isLatest())
        EssentialAPI.getGuiUtil().openScreen((GuiScreen)new UpdateAvailableScreen()); 
      GitHub.shownGUI = true;
    } 
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\events\OnGuiOpen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */