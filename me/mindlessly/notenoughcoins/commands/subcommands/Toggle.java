package me.mindlessly.notenoughcoins.commands.subcommands;

import me.mindlessly.notenoughcoins.Config;
import me.mindlessly.notenoughcoins.Main;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.command.ICommandSender;

public class Toggle implements Subcommand {
  public static void updateConfig() {
    if (Config.enabled) {
      Utils.sendMessageWithPrefix("&aFlipper enabled.");
    } else {
      Utils.sendMessageWithPrefix("&cFlipper disabled.");
    } 
  }
  
  public String getCommandName() {
    return "toggle";
  }
  
  public boolean isHidden() {
    return false;
  }
  
  public String getCommandUsage() {
    return "";
  }
  
  public String getCommandDescription() {
    return "Toggles the flipper on or off";
  }
  
  public boolean processCommand(ICommandSender sender, String[] args) {
    Config.enabled = !Config.enabled;
    Main.config.writeData();
    updateConfig();
    return true;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\commands\subcommands\Toggle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */