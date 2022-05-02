package me.mindlessly.notenoughcoins.utils.updater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import me.mindlessly.notenoughcoins.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class UpdatingScreen extends GuiScreen {
  private static final int DOT_TIME = 200;
  
  private static final String[] DOTS = new String[] { ".", "..", "...", "...", "..." };
  
  private boolean failed = false;
  
  private boolean complete = false;
  
  private GuiButton backButton;
  
  private float progress = 0.0F;
  
  public UpdatingScreen(boolean restartNow) {
    doUpdate(restartNow);
  }
  
  public void func_73866_w_() {
    this.field_146292_n.add(this.backButton = new GuiButton(0, this.field_146294_l / 2 - 100, this.field_146295_m / 4 + 132, 200, 20, ""));
    updateText();
  }
  
  private void updateText() {
    this.backButton.field_146126_j = (this.failed || this.complete) ? "OK" : "Cancel";
  }
  
  private void doUpdate(boolean restartNow) {
    try {
      File directory = new File(new File((Minecraft.func_71410_x()).field_71412_D, "config"), "nec");
      String url = GitHub.getUpdateDownloadUrl();
      String jarName = GitHub.getJarNameFromUrl(url);
      (new Thread(() -> {
            downloadUpdate(url, directory);
            if (!this.failed) {
              GitHub.scheduleCopyUpdateAtShutdown(jarName);
              if (restartNow)
                Minecraft.func_71410_x().func_71400_g(); 
              this.complete = true;
              updateText();
            } 
          }"NEC Update Downloader Thread"))
        
        .start();
    } catch (Exception ex) {
      ex.printStackTrace();
    } 
  }
  
  private void downloadUpdate(String url, File directory) {
    try {
      HttpURLConnection st = (HttpURLConnection)(new URL(url)).openConnection();
      st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
      st.connect();
      if (st.getResponseCode() != 200) {
        this.failed = true;
        updateText();
        Reference.logger.error(url + " returned status code " + st.getResponseCode());
        return;
      } 
      if (!directory.exists() && !directory.mkdirs()) {
        this.failed = true;
        updateText();
        Reference.logger.error("Couldn't create update file directory");
        return;
      } 
      String[] urlParts = url.split("/");
      float fileLength = st.getContentLength();
      File fileSaved = new File(directory, URLDecoder.decode(urlParts[urlParts.length - 1], "UTF-8"));
      InputStream fis = st.getInputStream();
      try (OutputStream fos = new FileOutputStream(fileSaved)) {
        byte[] data = new byte[1024];
        long total = 0L;
        int count;
        while ((count = fis.read(data)) != -1) {
          if ((Minecraft.func_71410_x()).field_71462_r != this) {
            fos.close();
            fis.close();
            this.failed = true;
            return;
          } 
          total += count;
          this.progress = (float)total / fileLength;
          fos.write(data, 0, count);
        } 
      } 
      fis.close();
      if ((Minecraft.func_71410_x()).field_71462_r != this)
        this.failed = true; 
    } catch (Exception ex) {
      ex.printStackTrace();
      this.failed = true;
      updateText();
    } 
  }
  
  public void func_146284_a(GuiButton button) {
    if (button.field_146127_k == 0)
      Minecraft.func_71410_x().func_147108_a(null); 
  }
  
  public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
    func_146276_q_();
    if (this.failed) {
      func_73732_a((Minecraft.func_71410_x()).field_71466_p, EnumChatFormatting.RED + "Update download failed", this.field_146294_l / 2, this.field_146295_m / 2, -1);
    } else if (this.complete) {
      func_73732_a((Minecraft.func_71410_x()).field_71466_p, EnumChatFormatting.GREEN + "Update download complete", this.field_146294_l / 2, this.field_146295_m / 2, 16777215);
    } else {
      int left = Math.max(this.field_146294_l / 2 - 100, 10);
      int right = Math.min(this.field_146294_l / 2 + 100, this.field_146294_l - 10);
      int top = this.field_146295_m / 2 - 2 - MathHelper.func_76123_f((Minecraft.func_71410_x()).field_71466_p.field_78288_b / 2.0F);
      int bottom = this.field_146295_m / 2 + 2 + MathHelper.func_76141_d((Minecraft.func_71410_x()).field_71466_p.field_78288_b / 2.0F);
      func_73734_a(left - 1, top - 1, right + 1, bottom + 1, -4144960);
      int progressPoint = MathHelper.func_76125_a(MathHelper.func_76141_d(this.progress * (right - left) + left), left, right);
      func_73734_a(left, top, progressPoint, bottom, -3457739);
      func_73734_a(progressPoint, top, right, bottom, -1);
      String label = String.format("%d%%", new Object[] { Integer.valueOf(MathHelper.func_76125_a(MathHelper.func_76141_d(this.progress * 100.0F), 0, 100)) });
      (Minecraft.func_71410_x()).field_71466_p.func_78276_b(label, (this.field_146294_l - (Minecraft.func_71410_x()).field_71466_p.func_78256_a(label)) / 2, top + 3, -16777216);
      int x = (this.field_146294_l - (Minecraft.func_71410_x()).field_71466_p.func_78256_a(String.format("Downloading %s", new Object[] { DOTS[DOTS.length - 1] }))) / 2;
      String title = String.format("Downloading %s", new Object[] { DOTS[(int)(System.currentTimeMillis() % (200 * DOTS.length)) / 200] });
      func_73731_b((Minecraft.func_71410_x()).field_71466_p, title, x, top - (Minecraft.func_71410_x()).field_71466_p.field_78288_b - 2, -1);
    } 
    super.func_73863_a(mouseX, mouseY, partialTicks);
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoin\\util\\updater\UpdatingScreen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */