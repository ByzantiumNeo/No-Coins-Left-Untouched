package org.java_websocket.exceptions;

public class IncompleteException extends Exception {
  private static final long serialVersionUID = 7330519489840500997L;
  
  private final int preferredSize;
  
  public IncompleteException(int preferredSize) {
    this.preferredSize = preferredSize;
  }
  
  public int getPreferredSize() {
    return this.preferredSize;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\exceptions\IncompleteException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */