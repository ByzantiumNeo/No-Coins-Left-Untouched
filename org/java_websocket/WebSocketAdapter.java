package org.java_websocket;

import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.PingFrame;
import org.java_websocket.framing.PongFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.HandshakeImpl1Server;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;

public abstract class WebSocketAdapter implements WebSocketListener {
  private PingFrame pingFrame;
  
  public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
    return (ServerHandshakeBuilder)new HandshakeImpl1Server();
  }
  
  public void onWebsocketHandshakeReceivedAsClient(WebSocket conn, ClientHandshake request, ServerHandshake response) throws InvalidDataException {}
  
  public void onWebsocketHandshakeSentAsClient(WebSocket conn, ClientHandshake request) throws InvalidDataException {}
  
  public void onWebsocketPing(WebSocket conn, Framedata f) {
    conn.sendFrame((Framedata)new PongFrame((PingFrame)f));
  }
  
  public void onWebsocketPong(WebSocket conn, Framedata f) {}
  
  public PingFrame onPreparePing(WebSocket conn) {
    if (this.pingFrame == null)
      this.pingFrame = new PingFrame(); 
    return this.pingFrame;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\WebSocketAdapter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */