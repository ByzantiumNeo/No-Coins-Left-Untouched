package me.mindlessly.notenoughcoins;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import me.mindlessly.notenoughcoins.commands.NECCommand;
import me.mindlessly.notenoughcoins.commands.subcommands.Help;
import me.mindlessly.notenoughcoins.commands.subcommands.Subcommand;
import me.mindlessly.notenoughcoins.commands.subcommands.Toggle;
import me.mindlessly.notenoughcoins.commands.subcommands.Token;
import me.mindlessly.notenoughcoins.events.OnChatReceived;
import me.mindlessly.notenoughcoins.events.OnGuiOpen;
import me.mindlessly.notenoughcoins.events.OnTick;
import me.mindlessly.notenoughcoins.events.OnTooltip;
import me.mindlessly.notenoughcoins.events.OnWorldJoin;
import me.mindlessly.notenoughcoins.objects.AverageItem;
import me.mindlessly.notenoughcoins.utils.ApiHandler;
import me.mindlessly.notenoughcoins.utils.Utils;
import me.mindlessly.notenoughcoins.utils.updater.GitHub;
import me.mindlessly.notenoughcoins.websocket.Client;
import net.minecraft.command.ICommand;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "nec", name = "NotEnoughCoins", version = "v0.9.2.1")
public class Main {
  public static Config config = new Config();
  
  public static Authenticator authenticator;
  
  public static boolean checkedForUpdate = false;
  
  public static NECCommand commandManager = new NECCommand(new Subcommand[] { (Subcommand)new Toggle(), (Subcommand)new Help(), (Subcommand)new Token() });
  
  public static Map<String, AverageItem> averageItemMap = new HashMap<>();
  
  public static Map<String, Date> processedItem = new HashMap<>();
  
  public static Map<String, Integer> lbinItem = new HashMap<>();
  
  public static Map<String, Integer> bazaarItem = new HashMap<>();
  
  public static Map<String, Integer> npcItem = new HashMap<>();
  
  public static List<String> chatFilters = new LinkedList<>();
  
  public static double balance = 0.0D;
  
  public static boolean justPlayedASound = false;
  
  public static File jarFile;
  
  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    jarFile = event.getSourceFile();
  }
  
  @EventHandler
  public void init(FMLInitializationEvent event) {
    ProgressManager.ProgressBar progressBar = ProgressManager.push("Not Enough Coins", 4);
    authenticator = new Authenticator(progressBar);
    try {
      authenticator.authenticate(true);
    } catch (Exception e) {
      while (progressBar.getStep() < progressBar.getSteps() - 1)
        progressBar.step("loading-failed-" + progressBar.getStep()); 
      e.printStackTrace();
    } 
    progressBar.step("Registering events, commands, hooks & tasks");
    config.preload();
    ClientCommandHandler.instance.func_71560_a((ICommand)commandManager);
    GitHub.downloadDeleteTask();
    MinecraftForge.EVENT_BUS.register(new OnWorldJoin());
    MinecraftForge.EVENT_BUS.register(new OnTick());
    MinecraftForge.EVENT_BUS.register(new OnTooltip());
    MinecraftForge.EVENT_BUS.register(new OnChatReceived());
    MinecraftForge.EVENT_BUS.register(new OnGuiOpen());
    Tasks.updateBalance.start();
    Tasks.updateBazaarItem.start();
    Tasks.updateFilters.start();
    Utils.runInAThread(ApiHandler::updateNPC);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Reference.logger.info("Logging out...");
            try {
              authenticator.logout();
            } catch (IOException e) {
              e.printStackTrace();
            } 
          }));
    progressBar.step("Establishing WebSocket Connection");
    Client.connectWithToken();
    ProgressManager.pop(progressBar);
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\Main.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */