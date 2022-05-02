package org.java_websocket.exceptions;

public class IncompleteHandshakeException extends RuntimeException {
  private static final long serialVersionUID = 7906596804233893092L;
  
  private final int preferredSize;
  
  public IncompleteHandshakeException(int preferredSize) {
    this.preferredSize = preferredSize;
  }
  
  public IncompleteHandshakeException() {
    this.preferredSize = 0;
  }
  
  public int getPreferredSize() {
    return this.preferredSize;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\exceptions\IncompleteHandshakeException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */