package org.java_websocket;

import java.util.List;
import org.java_websocket.drafts.Draft;

public interface WebSocketFactory {
  WebSocket createWebSocket(WebSocketAdapter paramWebSocketAdapter, Draft paramDraft);
  
  WebSocket createWebSocket(WebSocketAdapter paramWebSocketAdapter, List<Draft> paramList);
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\WebSocketFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */