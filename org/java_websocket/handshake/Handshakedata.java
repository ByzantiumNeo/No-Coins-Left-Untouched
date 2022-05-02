package org.java_websocket.handshake;

import java.util.Iterator;

public interface Handshakedata {
  Iterator<String> iterateHttpFields();
  
  String getFieldValue(String paramString);
  
  boolean hasFieldValue(String paramString);
  
  byte[] getContent();
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\handshake\Handshakedata.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */