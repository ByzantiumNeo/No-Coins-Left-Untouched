package org.java_websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import org.java_websocket.enums.Role;

public class SocketChannelIOHelper {
  private SocketChannelIOHelper() {
    throw new IllegalStateException("Utility class");
  }
  
  public static boolean read(ByteBuffer buf, WebSocketImpl ws, ByteChannel channel) throws IOException {
    buf.clear();
    int read = channel.read(buf);
    buf.flip();
    if (read == -1) {
      ws.eot();
      return false;
    } 
    return (read != 0);
  }
  
  public static boolean readMore(ByteBuffer buf, WebSocketImpl ws, WrappedByteChannel channel) throws IOException {
    buf.clear();
    int read = channel.readMore(buf);
    buf.flip();
    if (read == -1) {
      ws.eot();
      return false;
    } 
    return channel.isNeedRead();
  }
  
  public static boolean batch(WebSocketImpl ws, ByteChannel sockchannel) throws IOException {
    if (ws == null)
      return false; 
    ByteBuffer buffer = ws.outQueue.peek();
    WrappedByteChannel c = null;
    if (buffer == null) {
      if (sockchannel instanceof WrappedByteChannel) {
        c = (WrappedByteChannel)sockchannel;
        if (c.isNeedWrite())
          c.writeMore(); 
      } 
    } else {
      do {
        sockchannel.write(buffer);
        if (buffer.remaining() > 0)
          return false; 
        ws.outQueue.poll();
        buffer = ws.outQueue.peek();
      } while (buffer != null);
    } 
    if (ws.outQueue.isEmpty() && ws.isFlushAndClose() && ws.getDraft() != null && ws
      .getDraft().getRole() != null && ws.getDraft().getRole() == Role.SERVER)
      ws.closeConnection(); 
    return (c == null || !((WrappedByteChannel)sockchannel).isNeedWrite());
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\SocketChannelIOHelper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */