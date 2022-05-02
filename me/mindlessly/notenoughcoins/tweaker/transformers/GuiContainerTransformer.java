package me.mindlessly.notenoughcoins.tweaker.transformers;

import java.util.Iterator;
import me.mindlessly.notenoughcoins.tweaker.utils.TransformerClass;
import me.mindlessly.notenoughcoins.tweaker.utils.TransformerMethod;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class GuiContainerTransformer implements ITransformer {
  public String[] getClassName() {
    return new String[] { TransformerClass.GuiContainer.getTransformerName() };
  }
  
  public void transform(ClassNode classNode, String name) {
    for (MethodNode methodNode : classNode.methods) {
      if (TransformerMethod.drawScreen.matches(methodNode)) {
        Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
        while (iterator.hasNext()) {
          AbstractInsnNode abstractNode = iterator.next();
          if (abstractNode instanceof MethodInsnNode && abstractNode.getOpcode() == 183) {
            MethodInsnNode methodInsnNode = (MethodInsnNode)abstractNode;
            if (methodInsnNode.owner.equals(TransformerClass.GuiContainer.getNameRaw()) && TransformerMethod.drawSlot.matches(methodInsnNode)) {
              methodNode.instructions.insert(abstractNode, (AbstractInsnNode)new MethodInsnNode(184, "me/mindlessly/notenoughcoins/tweaker/hooks/GuiContainerHook", "drawSlot", "(" + TransformerClass.GuiContainer
                    .getName() + TransformerClass.Slot.getName() + ")V", false));
              methodNode.instructions.insert(abstractNode, (AbstractInsnNode)new VarInsnNode(25, 9));
              methodNode.instructions.insert(abstractNode, (AbstractInsnNode)new VarInsnNode(25, 0));
            } 
          } 
        } 
      } 
    } 
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\tweaker\transformers\GuiContainerTransformer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */