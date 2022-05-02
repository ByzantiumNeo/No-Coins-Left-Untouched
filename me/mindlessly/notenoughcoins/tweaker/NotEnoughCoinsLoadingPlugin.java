package me.mindlessly.notenoughcoins.tweaker;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;

@MCVersion("1.8.9")
@Name("NotEnoughCoins FTW")
public class NotEnoughCoinsLoadingPlugin implements IFMLLoadingPlugin {
  public String[] getASMTransformerClass() {
    return new String[] { NotEnoughCoinsTransformer.class.getName() };
  }
  
  public String getModContainerClass() {
    return null;
  }
  
  public String getSetupClass() {
    return null;
  }
  
  public void injectData(Map<String, Object> data) {}
  
  public String getAccessTransformerClass() {
    return null;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\tweaker\NotEnoughCoinsLoadingPlugin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */