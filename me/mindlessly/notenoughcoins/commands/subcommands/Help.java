package me.mindlessly.notenoughcoins.commands.subcommands;

import me.mindlessly.notenoughcoins.Main;
import net.minecraft.command.ICommandSender;

public class Help implements Subcommand {
  public String getCommandName() {
    return "help";
  }
  
  public boolean isHidden() {
    return false;
  }
  
  public String getCommandUsage() {
    return "";
  }
  
  public String getCommandDescription() {
    return "Sends the help message";
  }
  
  public boolean processCommand(ICommandSender sender, String[] args) {
    Main.commandManager.sendHelp(sender);
    return true;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\commands\subcommands\Help.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */