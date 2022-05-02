package me.mindlessly.notenoughcoins.tweaker;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import me.mindlessly.notenoughcoins.Reference;
import me.mindlessly.notenoughcoins.tweaker.transformers.GuiContainerTransformer;
import me.mindlessly.notenoughcoins.tweaker.transformers.ITransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.commons.lang3.mutable.MutableInt;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class NotEnoughCoinsTransformer implements IClassTransformer {
  static {
    PreTransformationChecks.runChecks();
  }
  
  private final Multimap<String, ITransformer> transformerMap = (Multimap<String, ITransformer>)ArrayListMultimap.create();
  
  public NotEnoughCoinsTransformer() {
    registerTransformer((ITransformer)new GuiContainerTransformer());
  }
  
  private void registerTransformer(ITransformer transformer) {
    for (String cls : transformer.getClassName())
      this.transformerMap.put(cls, transformer); 
  }
  
  public byte[] transform(String name, String transformedName, byte[] bytes) {
    if (bytes == null)
      return null; 
    Collection<ITransformer> transformers = this.transformerMap.get(transformedName);
    if (transformers.isEmpty())
      return bytes; 
    Reference.logger.info("Found {} transformers for {}", new Object[] { Integer.valueOf(transformers.size()), transformedName });
    ClassReader reader = new ClassReader(bytes);
    ClassNode node = new ClassNode();
    reader.accept((ClassVisitor)node, 8);
    MutableInt classWriterFlags = new MutableInt(3);
    transformers.forEach(transformer -> {
          Reference.logger.info("Applying transformer {} on {}...", new Object[] { transformer.getClass().getName(), transformedName });
          transformer.transform(node, transformedName);
        });
    ClassWriter writer = new ClassWriter(classWriterFlags.getValue().intValue());
    try {
      node.accept(writer);
    } catch (Throwable t) {
      Reference.logger.error("Exception when transforming " + transformedName + " : " + t.getClass().getSimpleName());
      t.printStackTrace();
      outputBytecode(transformedName, writer);
      return bytes;
    } 
    outputBytecode(transformedName, writer);
    return writer.toByteArray();
  }
  
  private void outputBytecode(String transformedName, ClassWriter writer) {
    try {
      File bytecodeDirectory = new File("bytecode");
      File bytecodeOutput = new File(bytecodeDirectory, transformedName + ".class");
      if (!bytecodeDirectory.exists())
        return; 
      if (!bytecodeOutput.exists())
        bytecodeOutput.createNewFile(); 
      FileOutputStream os = new FileOutputStream(bytecodeOutput);
      os.write(writer.toByteArray());
      os.close();
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\tweaker\NotEnoughCoinsTransformer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */