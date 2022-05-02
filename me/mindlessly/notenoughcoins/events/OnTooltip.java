package me.mindlessly.notenoughcoins.events;

import gg.essential.universal.UKeyboard;
import java.util.Map;
import me.mindlessly.notenoughcoins.Config;
import me.mindlessly.notenoughcoins.objects.BestSellingMethod;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OnTooltip {
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onTooltip(ItemTooltipEvent event) {
    if (!Utils.isOnSkyblock())
      return; 
    String id = Utils.getIDFromItemStack(event.itemStack);
    if (Config.debug && id != null)
      event.toolTip.add(EnumChatFormatting.YELLOW + EnumChatFormatting.BOLD.toString() + "Item ID: " + EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + id); 
    if (Config.bestSellingMethod) {
      Map.Entry<BestSellingMethod, Long> result = Utils.getBestSellingMethod(id);
      if (result.getKey() == BestSellingMethod.NONE)
        return; 
      boolean shifted = UKeyboard.isShiftKeyDown();
      if (event.itemStack.field_77994_a > 1 && !shifted && 
        !event.toolTip.contains(EnumChatFormatting.DARK_GRAY + "[SHIFT show x" + event.itemStack.field_77994_a + "]"))
        event.toolTip.add(EnumChatFormatting.DARK_GRAY + "[SHIFT show x" + event.itemStack.field_77994_a + "]"); 
      event.toolTip.add(EnumChatFormatting.YELLOW + EnumChatFormatting.BOLD.toString() + "Best Selling Method: " + EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + ((BestSellingMethod)result
          .getKey()).toString() + " ($" + Utils.formatValue(shifted ? (((Long)result
            .getValue()).longValue() * event.itemStack.field_77994_a) : ((Long)result.getValue()).longValue()) + ")");
    } 
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\events\OnTooltip.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */