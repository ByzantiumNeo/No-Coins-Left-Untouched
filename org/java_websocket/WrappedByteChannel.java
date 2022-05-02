package org.java_websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public interface WrappedByteChannel extends ByteChannel {
  boolean isNeedWrite();
  
  void writeMore() throws IOException;
  
  boolean isNeedRead();
  
  int readMore(ByteBuffer paramByteBuffer) throws IOException;
  
  boolean isBlocking();
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\WrappedByteChannel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */