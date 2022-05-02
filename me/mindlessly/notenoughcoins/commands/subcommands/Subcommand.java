package me.mindlessly.notenoughcoins.commands.subcommands;

import net.minecraft.command.ICommandSender;

public interface Subcommand {
  String getCommandName();
  
  boolean isHidden();
  
  String getCommandUsage();
  
  String getCommandDescription();
  
  boolean processCommand(ICommandSender paramICommandSender, String[] paramArrayOfString);
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\commands\subcommands\Subcommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */