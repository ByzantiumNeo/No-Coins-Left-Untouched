package me.mindlessly.notenoughcoins.tweaker.hooks;

import java.awt.Color;
import java.util.List;
import me.mindlessly.notenoughcoins.Config;
import me.mindlessly.notenoughcoins.objects.BestSellingMethod;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GuiContainerHook {
  public static final int GREEN_OVERLAY = (new Color(0, 255, 0, 100)).getRGB();
  
  public static boolean isSellMerchant(Container inventory) {
    if (inventory.field_75151_b.size() <= 49)
      return false; 
    ItemStack itemStack = ((Slot)inventory.field_75151_b.get(49)).func_75211_c();
    if (itemStack != null) {
      if (itemStack.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150438_bZ) && itemStack.func_82837_s() && 
        Utils.removeColorCodes(itemStack.func_82833_r()).equals("Sell Item"))
        return true; 
      List<String> tooltip = itemStack.func_82840_a((EntityPlayer)(Minecraft.func_71410_x()).field_71439_g, false);
      for (String line : tooltip) {
        if (Utils.removeColorCodes(line).equals("Click to buyback!"))
          return true; 
      } 
    } 
    return false;
  }
  
  public static boolean isBazaar(Container inventory) {
    if (inventory.func_75139_a(0) == null)
      return false; 
    IInventory realInventory = (inventory.func_75139_a(0)).field_75224_c;
    return (realInventory.func_145818_k_() && realInventory.func_145748_c_().func_150260_c().startsWith("Bazaar"));
  }
  
  public static boolean isAuction(Container inventory) {
    if (inventory.func_75139_a(0) == null)
      return false; 
    IInventory realInventory = (inventory.func_75139_a(0)).field_75224_c;
    return (realInventory.func_145818_k_() && realInventory.func_145748_c_().func_150260_c().contains("Auction"));
  }
  
  public static void drawSlot(GuiContainer guiContainer, Slot slot) {
    if (Utils.isOnSkyblock() && 
      Config.bestSellingOverlay && slot.func_75216_d()) {
      BestSellingMethod bestSellingMethod = (BestSellingMethod)Utils.getBestSellingMethod(Utils.getIDFromItemStack(slot.func_75211_c())).getKey();
      boolean drawOverlay = (bestSellingMethod == BestSellingMethod.NPC && isSellMerchant(guiContainer.field_147002_h));
      if (bestSellingMethod == BestSellingMethod.BAZAAR && isBazaar(guiContainer.field_147002_h))
        drawOverlay = true; 
      if (bestSellingMethod == BestSellingMethod.LBIN && isAuction(guiContainer.field_147002_h))
        drawOverlay = true; 
      if (drawOverlay) {
        int slotLeft = slot.field_75223_e;
        int slotTop = slot.field_75221_f;
        int slotRight = slotLeft + 16;
        int slotBottom = slotTop + 16;
        Gui.func_73734_a(slotLeft, slotTop, slotRight, slotBottom, GREEN_OVERLAY);
      } 
    } 
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\tweaker\hooks\GuiContainerHook.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */