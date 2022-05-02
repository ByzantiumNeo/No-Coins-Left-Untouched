package org.java_websocket.server;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import org.java_websocket.SSLSocketChannel2;

public class SSLParametersWebSocketServerFactory extends DefaultSSLWebSocketServerFactory {
  private final SSLParameters sslParameters;
  
  public SSLParametersWebSocketServerFactory(SSLContext sslContext, SSLParameters sslParameters) {
    this(sslContext, Executors.newSingleThreadScheduledExecutor(), sslParameters);
  }
  
  public SSLParametersWebSocketServerFactory(SSLContext sslContext, ExecutorService executerService, SSLParameters sslParameters) {
    super(sslContext, executerService);
    if (sslParameters == null)
      throw new IllegalArgumentException(); 
    this.sslParameters = sslParameters;
  }
  
  public ByteChannel wrapChannel(SocketChannel channel, SelectionKey key) throws IOException {
    SSLEngine e = this.sslcontext.createSSLEngine();
    e.setUseClientMode(false);
    e.setSSLParameters(this.sslParameters);
    return (ByteChannel)new SSLSocketChannel2(channel, e, this.exec, key);
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\server\SSLParametersWebSocketServerFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */