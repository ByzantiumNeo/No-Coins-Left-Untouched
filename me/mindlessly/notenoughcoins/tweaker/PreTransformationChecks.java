package me.mindlessly.notenoughcoins.tweaker;

import net.minecraft.launchwrapper.Launch;

public class PreTransformationChecks {
  public static boolean deobfuscated;
  
  public static boolean usingNotchMappings;
  
  static void runChecks() {
    deobfuscated = false;
    deobfuscated = ((Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment")).booleanValue();
    usingNotchMappings = !deobfuscated;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\tweaker\PreTransformationChecks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */