package me.mindlessly.notenoughcoins.tweaker.utils;

import me.mindlessly.notenoughcoins.tweaker.PreTransformationChecks;
import org.objectweb.asm.tree.FieldInsnNode;

public enum TransformerField {
  width("width", "field_146294_l", "l", "I"),
  height("height", "field_146295_m", "m", "I"),
  buttonList("buttonList", "field_146292_n", "n", "Ljava/util/List;"),
  id("id", "field_146127_k", "k", "I"),
  lowerChestInventory("lowerChestInventory", "field_147015_w", "w", TransformerClass.IInventory.getName()),
  guiLeft("guiLeft", "field_147003_i", "i", "I"),
  guiTop("guiTop", "field_147009_r", "r", "I"),
  fontRendererObj("fontRendererObj", "field_146289_q", "q", TransformerClass.FontRenderer.getName()),
  inventorySlots("inventorySlots", "field_147002_h", "h", TransformerClass.Container.getName()),
  theSlot("theSlot", "field_147006_u", "u", TransformerClass.Slot.getName()),
  xSize("xSize", "field_146999_f", "f", "I"),
  ySize("ySize", "field_147000_g", "g", "I"),
  mcResourceManager("mcResourceManager", "field_110451_am", "ay", TransformerClass.IReloadableResourceManager.getName()),
  red("red", "field_78291_n", "m", "F"),
  green("green", "field_179186_b", "b", "F"),
  blue("blue", "field_78292_o", "n", "F"),
  alpha("alpha", "field_78305_q", "p", "F"),
  hurtTime("hurtTime", "field_70737_aN ", "au", "I"),
  currentItem("currentItem", "field_70461_c", "c", "I"),
  NULL(null, null, null, null);
  
  private final String name;
  
  private final String type;
  
  TransformerField(String deobfName, String seargeName, String notchName18, String type) {
    this.type = type;
    if (PreTransformationChecks.deobfuscated) {
      this.name = deobfName;
    } else if (PreTransformationChecks.usingNotchMappings) {
      this.name = notchName18;
    } else {
      this.name = seargeName;
    } 
  }
  
  public FieldInsnNode getField(TransformerClass currentClass) {
    return new FieldInsnNode(180, currentClass.getNameRaw(), this.name, this.type);
  }
  
  public FieldInsnNode putField(TransformerClass currentClass) {
    return new FieldInsnNode(181, currentClass.getNameRaw(), this.name, this.type);
  }
  
  public boolean matches(FieldInsnNode fieldInsnNode) {
    return (this.name.equals(fieldInsnNode.name) && this.type.equals(fieldInsnNode.desc));
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\tweake\\utils\TransformerField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */