package gg.essential.loader.stage0;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class EssentialLoader {
  static final String STAGE1_RESOURCE = "gg/essential/loader/stage0/stage1.jar";
  
  static final String STAGE1_PKG = "gg.essential.loader.stage1.";
  
  static final String STAGE1_PKG_PATH = "gg.essential.loader.stage1.".replace('.', '/');
  
  static final Logger LOGGER = LogManager.getLogger(EssentialLoader.class);
  
  private final String variant;
  
  public EssentialLoader(String variant) {
    this.variant = variant;
  }
  
  public Path loadStage1File(Path gameDir) throws Exception {
    Path dataDir = gameDir.resolve("essential").resolve("loader").resolve("stage0").resolve(this.variant);
    Path stage1UpdateFile = dataDir.resolve("stage1.update.jar");
    Path stage1File = dataDir.resolve("stage1.jar");
    URL stage1Url = stage1File.toUri().toURL();
    if (!Files.exists(dataDir, new java.nio.file.LinkOption[0]))
      Files.createDirectories(dataDir, (FileAttribute<?>[])new FileAttribute[0]); 
    if (Files.exists(stage1UpdateFile, new java.nio.file.LinkOption[0])) {
      LOGGER.info("Found update for stage1.");
      Files.deleteIfExists(stage1File);
      Files.move(stage1UpdateFile, stage1File, new java.nio.file.CopyOption[0]);
    } 
    URL latestUrl = null;
    int latestVersion = -1;
    if (Files.exists(stage1File, new java.nio.file.LinkOption[0])) {
      latestVersion = getVersion(stage1Url);
      LOGGER.debug("Found stage1 version {}: {}", new Object[] { Integer.valueOf(latestVersion), stage1Url });
    } 
    Enumeration<URL> resources = EssentialLoader.class.getClassLoader().getResources("gg/essential/loader/stage0/stage1.jar");
    if (!resources.hasMoreElements())
      LOGGER.warn("Found no embedded stage1 jar files."); 
    while (resources.hasMoreElements()) {
      URL url = resources.nextElement();
      int version = getVersion(url);
      LOGGER.debug("Found stage1 version {}: {}", new Object[] { Integer.valueOf(version), url });
      if (version > latestVersion) {
        latestVersion = version;
        latestUrl = url;
      } 
    } 
    if (latestUrl != null) {
      LOGGER.info("Updating stage1 to version {} from {}", new Object[] { Integer.valueOf(latestVersion), latestUrl });
      try (InputStream in = latestUrl.openStream()) {
        Files.deleteIfExists(stage1File);
        Files.copy(in, stage1File, new java.nio.file.CopyOption[0]);
      } 
    } 
    return stage1File;
  }
  
  private static int getVersion(URL file) {
    try (JarInputStream in = new JarInputStream(file.openStream(), false)) {
      Manifest manifest = in.getManifest();
      Attributes attributes = manifest.getMainAttributes();
      if (!STAGE1_PKG_PATH.equals(attributes.getValue("Name")))
        return -1; 
      return Integer.parseInt(attributes.getValue("Implementation-Version"));
    } catch (Exception e) {
      LOGGER.warn("Failed to read version from " + file, e);
      return -1;
    } 
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\gg\essential\loader\stage0\EssentialLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */