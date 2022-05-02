package me.mindlessly.notenoughcoins;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import javax.net.ssl.HttpsURLConnection;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraftforge.fml.common.ProgressManager;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

public class Authenticator {
  private static final String BASE_URL = "https://nec.robothanzo.dev/auth";
  
  public static String myUUID;
  
  private final ProgressManager.ProgressBar progressBar;
  
  private String token;
  
  public Authenticator(ProgressManager.ProgressBar progressBar) {
    this.progressBar = progressBar;
  }
  
  public static JsonElement getAuthenticatedJson(String jsonUrl) throws IOException {
    HttpsURLConnection connection;
    if (Main.authenticator.getToken() == null) {
      Reference.logger.warn("Authenticator token is null, trying to get one");
      try {
        Main.authenticator.authenticate(false);
      } catch (Exception e) {
        Reference.logger.error(e.getMessage(), e);
        return null;
      } 
      if (Main.authenticator.getToken() == null)
        return null; 
    } 
    try {
      connection = (HttpsURLConnection)(new URL(jsonUrl)).openConnection();
    } catch (IOException e) {
      Reference.logger.error(e.getMessage(), e);
      return null;
    } 
    connection.setRequestProperty("User-Agent", "NotEnoughCoins/1.0");
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setRequestProperty("Authorization", Main.authenticator.getToken());
    connection.setRequestMethod("GET");
    connection.setDoInput(true);
    int code = connection.getResponseCode();
    String payload = String.join("\n", IOUtils.readLines((connection.getErrorStream() == null) ? connection.getInputStream() : connection.getErrorStream()));
    if (code >= 400) {
      Reference.logger.error(jsonUrl + " :: Received " + connection.getResponseCode() + " along with\n" + payload);
      if (code == 401) {
        try {
          Main.authenticator.authenticate(true);
        } catch (AuthenticationException e) {
          Main.authenticator.token = null;
          return null;
        } 
        return getAuthenticatedJson(jsonUrl);
      } 
      return null;
    } 
    return (new JsonParser()).parse(payload);
  }
  
  public String getToken() {
    return this.token;
  }
  
  public void authenticate(boolean withProgress) throws IOException, AuthenticationException, NullPointerException {
    Session session = Minecraft.func_71410_x().func_110432_I();
    String sessionToken = session.func_148254_d();
    if (withProgress)
      this.progressBar.step("Authenticating (1/2)"); 
    String tempToken = Objects.<String>requireNonNull(requestAuth(session.func_148256_e()));
    MinecraftSessionService yggdrasilMinecraftSessionService = Minecraft.func_71410_x().func_152347_ac();
    JsonObject d = getJwtPayload(tempToken);
    yggdrasilMinecraftSessionService.joinServer(session.func_148256_e(), sessionToken, d.get("server_id").getAsString());
    if (withProgress)
      this.progressBar.step("Authenticating (2/2)"); 
    this.token = Objects.<String>requireNonNull(verifyAuth(tempToken));
  }
  
  public JsonObject getJwtPayload(String jwt) {
    String midPart = jwt.split("\\.")[1].replace("+", "-").replace("/", "_");
    String base64Decode = new String(Base64.decodeBase64(midPart));
    return (JsonObject)(new JsonParser()).parse(base64Decode);
  }
  
  private String requestAuth(GameProfile profile) throws IOException {
    myUUID = profile.getId().toString().replaceAll("-", "").toLowerCase(Locale.ROOT);
    HttpsURLConnection connection = (HttpsURLConnection)(new URL("https://nec.robothanzo.dev/auth/request")).openConnection();
    connection.setRequestProperty("User-Agent", "NotEnoughCoins Authentication Service/1.0");
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setRequestMethod("POST");
    connection.setDoInput(true);
    connection.setDoOutput(true);
    connection.getOutputStream().write(("{\"uuid\":\"" + myUUID + "\",\"username\":\"" + profile.getName() + "\"}").getBytes());
    int code = connection.getResponseCode();
    String payload = String.join("\n", IOUtils.readLines((connection.getErrorStream() == null) ? connection.getInputStream() : connection.getErrorStream()));
    if (code >= 400) {
      Reference.logger.error("https://nec.robothanzo.dev/auth/request :: Received " + connection.getResponseCode() + " along with\n" + payload);
      return null;
    } 
    JsonObject json = (JsonObject)(new JsonParser()).parse(payload);
    if (!json.get("success").getAsBoolean())
      return null; 
    return json.get("jwt").getAsString();
  }
  
  private String verifyAuth(String tempToken) throws IOException {
    HttpsURLConnection urlConnection = (HttpsURLConnection)(new URL("https://nec.robothanzo.dev/auth/verify")).openConnection();
    urlConnection.setRequestMethod("POST");
    urlConnection.setRequestProperty("User-Agent", "NotEnoughCoins Authentication Service/1.0");
    urlConnection.setRequestProperty("Content-Type", "application/json");
    urlConnection.setDoInput(true);
    urlConnection.setDoOutput(true);
    urlConnection.getOutputStream().write(("{\"jwt\":\"" + tempToken + "\"}").getBytes());
    String payload = String.join("\n", IOUtils.readLines((urlConnection.getErrorStream() == null) ? urlConnection.getInputStream() : urlConnection.getErrorStream()));
    int code = urlConnection.getResponseCode();
    if (code >= 400) {
      Reference.logger.error("https://nec.robothanzo.dev/auth/verify :: Received " + urlConnection.getResponseCode() + " along with\n" + payload);
      return null;
    } 
    JsonObject json = (JsonObject)(new JsonParser()).parse(payload);
    if (!json.get("success").getAsBoolean())
      return null; 
    return json.get("token").getAsString();
  }
  
  public void logout() throws IOException {
    HttpsURLConnection urlConnection = (HttpsURLConnection)(new URL("https://nec.robothanzo.dev/auth/logout")).openConnection();
    urlConnection.setRequestMethod("GET");
    urlConnection.setRequestProperty("User-Agent", "NotEnoughCoins Authentication Service/1.0");
    urlConnection.setRequestProperty("Content-Type", "application/json");
    urlConnection.setRequestProperty("Authorization", this.token);
    urlConnection.setDoInput(true);
    urlConnection.setDoOutput(true);
    int code = urlConnection.getResponseCode();
    String payload = String.join("\n", IOUtils.readLines((urlConnection.getErrorStream() == null) ? urlConnection.getInputStream() : urlConnection.getErrorStream()));
    if (code >= 400)
      Reference.logger.error("https://nec.robothanzo.dev/auth/logout :: Received " + urlConnection.getResponseCode() + " along with\n" + payload); 
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\me\mindlessly\notenoughcoins\Authenticator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */