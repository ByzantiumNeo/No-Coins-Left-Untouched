package org.java_websocket.protocols;

public interface IProtocol {
  boolean acceptProvidedProtocol(String paramString);
  
  String getProvidedProtocol();
  
  IProtocol copyInstance();
  
  String toString();
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\protocols\IProtocol.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */