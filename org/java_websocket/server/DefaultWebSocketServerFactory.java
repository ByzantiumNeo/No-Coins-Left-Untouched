package org.java_websocket.server;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketAdapter;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.WebSocketListener;
import org.java_websocket.WebSocketServerFactory;
import org.java_websocket.drafts.Draft;

public class DefaultWebSocketServerFactory implements WebSocketServerFactory {
  public WebSocketImpl createWebSocket(WebSocketAdapter a, Draft d) {
    return new WebSocketImpl((WebSocketListener)a, d);
  }
  
  public WebSocketImpl createWebSocket(WebSocketAdapter a, List<Draft> d) {
    return new WebSocketImpl((WebSocketListener)a, d);
  }
  
  public SocketChannel wrapChannel(SocketChannel channel, SelectionKey key) {
    return channel;
  }
  
  public void close() {}
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\server\DefaultWebSocketServerFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */