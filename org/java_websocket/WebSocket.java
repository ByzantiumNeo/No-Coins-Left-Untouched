package org.java_websocket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Collection;
import javax.net.ssl.SSLSession;
import org.java_websocket.drafts.Draft;
import org.java_websocket.enums.Opcode;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.framing.Framedata;
import org.java_websocket.protocols.IProtocol;

public interface WebSocket {
  void close(int paramInt, String paramString);
  
  void close(int paramInt);
  
  void close();
  
  void closeConnection(int paramInt, String paramString);
  
  void send(String paramString);
  
  void send(ByteBuffer paramByteBuffer);
  
  void send(byte[] paramArrayOfbyte);
  
  void sendFrame(Framedata paramFramedata);
  
  void sendFrame(Collection<Framedata> paramCollection);
  
  void sendPing();
  
  void sendFragmentedFrame(Opcode paramOpcode, ByteBuffer paramByteBuffer, boolean paramBoolean);
  
  boolean hasBufferedData();
  
  InetSocketAddress getRemoteSocketAddress();
  
  InetSocketAddress getLocalSocketAddress();
  
  boolean isOpen();
  
  boolean isClosing();
  
  boolean isFlushAndClose();
  
  boolean isClosed();
  
  Draft getDraft();
  
  ReadyState getReadyState();
  
  String getResourceDescriptor();
  
  <T> void setAttachment(T paramT);
  
  <T> T getAttachment();
  
  boolean hasSSLSupport();
  
  SSLSession getSSLSession() throws IllegalArgumentException;
  
  IProtocol getProtocol();
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\WebSocket.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */