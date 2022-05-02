package me.mindlessly.notenoughcoins.tweaker.transformers;

import org.objectweb.asm.tree.ClassNode;

public interface ITransformer {
  String[] getClassName();
  
  void transform(ClassNode paramClassNode, String paramString);
  
  boolean nameMatches(String method, String... names) {
    for (String name : names) {
      if (method.equals(name))
        return true; 
    } 
    return false;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\tweaker\transformers\ITransformer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */