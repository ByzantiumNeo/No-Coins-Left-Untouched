package me.mindlessly.notenoughcoins.commands;

import gg.essential.api.EssentialAPI;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import me.mindlessly.notenoughcoins.Main;
import me.mindlessly.notenoughcoins.commands.subcommands.Subcommand;
import me.mindlessly.notenoughcoins.utils.Utils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class NECCommand extends CommandBase {
  private final Subcommand[] subcommands;
  
  public NECCommand(Subcommand[] subcommands) {
    this.subcommands = subcommands;
  }
  
  public boolean func_71519_b(ICommandSender sender) {
    return true;
  }
  
  public List<String> func_71514_a() {
    return Arrays.asList(new String[] { "notenoughcoins", "notenoughcoin" });
  }
  
  public String func_71517_b() {
    return "nec";
  }
  
  public String func_71518_a(ICommandSender sender) {
    return "/nec <subcommand> <arguments>";
  }
  
  public void sendHelp(ICommandSender sender) {
    List<String> commandUsages = new LinkedList<>();
    for (Subcommand subcommand : this.subcommands) {
      if (!subcommand.isHidden())
        commandUsages.add(EnumChatFormatting.AQUA + "/nec " + subcommand.getCommandName() + " " + subcommand
            .getCommandUsage() + EnumChatFormatting.DARK_AQUA + " - " + subcommand.getCommandDescription()); 
    } 
    sender.func_145747_a((IChatComponent)new ChatComponentText(EnumChatFormatting.GOLD + "NEC " + EnumChatFormatting.GREEN + "v0.9.2.1" + "\n" + 
          String.join("\n", (Iterable)commandUsages)));
  }
  
  public void func_71515_b(ICommandSender sender, String[] args) {
    if (args.length == 0) {
      EssentialAPI.getGuiUtil().openScreen((GuiScreen)Main.config.gui());
      return;
    } 
    for (Subcommand subcommand : this.subcommands) {
      if (Objects.equals(args[0], subcommand.getCommandName())) {
        if (!subcommand.processCommand(sender, Arrays.<String>copyOfRange(args, 1, args.length)))
          Utils.sendMessageWithPrefix("&cFailed to execute command, command usage: /nec " + subcommand
              .getCommandName() + " " + subcommand.getCommandUsage()); 
        return;
      } 
    } 
    Utils.sendMessageWithPrefix(EnumChatFormatting.RED + "The subcommand wasn't found, please refer to the help message below for the list of subcommands");
    sendHelp(sender);
  }
  
  public List<String> func_180525_a(ICommandSender sender, String[] args, BlockPos pos) {
    List<String> possibilities = new LinkedList<>();
    for (Subcommand subcommand : this.subcommands)
      possibilities.add(subcommand.getCommandName()); 
    if (args.length == 1)
      return func_175762_a(args, possibilities); 
    return null;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\commands\NECCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */