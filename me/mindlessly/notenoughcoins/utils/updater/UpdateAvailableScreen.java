package me.mindlessly.notenoughcoins.utils.updater;

import gg.essential.universal.UDesktop;
import java.net.URI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;

public class UpdateAvailableScreen extends GuiScreen {
  private final String text = "A new update is available " + EnumChatFormatting.YELLOW + GitHub.getLatestVersion();
  
  public void func_73866_w_() {
    this.field_146292_n.add(new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 84, 200, 20, "View changelog"));
    this.field_146292_n.add(new GuiButton(1, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 108, 98, 20, "Update now"));
    this.field_146292_n.add(new GuiButton(2, this.field_146294_l / 2 + 2, this.field_146295_m / 4 + 108, 98, 20, "Update at exit"));
    this.field_146292_n.add(new GuiButton(3, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 132, 200, 20, "Cancel"));
  }
  
  public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
    func_146276_q_();
    int yOffset = Math.min(this.field_146295_m / 2, this.field_146295_m / 4 + 80 - (Minecraft.func_71410_x()).field_71466_p.field_78288_b * 2);
    func_73732_a((Minecraft.func_71410_x()).field_71466_p, this.text, this.field_146294_l / 2, yOffset - (Minecraft.func_71410_x()).field_71466_p.field_78288_b - 2, -1);
    func_73732_a((Minecraft.func_71410_x()).field_71466_p, "Update now or when leaving Minecraft?", this.field_146294_l / 2, yOffset, -1);
    func_73732_a((Minecraft.func_71410_x()).field_71466_p, "(Updating now will exit Minecraft after downloading update)", this.field_146294_l / 2, yOffset + (Minecraft.func_71410_x()).field_71466_p.field_78288_b + 2, -1);
    super.func_73863_a(mouseX, mouseY, partialTicks);
  }
  
  public void func_146284_a(GuiButton button) {
    if (button.field_146127_k == 1 || button.field_146127_k == 2) {
      GitHub.showChangelog = true;
      Minecraft.func_71410_x().func_147108_a(new UpdatingScreen((button.field_146127_k == 1)));
    } else if (button.field_146127_k == 3) {
      Minecraft.func_71410_x().func_147108_a(null);
    } else if (button.field_146127_k == 0) {
      UDesktop.browse(URI.create("https://github.com/NotEnoughCoins/NotEnoughCoins/releases"));
    } 
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoin\\util\\updater\UpdateAvailableScreen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */