package org.java_websocket.exceptions;

public class NotSendableException extends RuntimeException {
  private static final long serialVersionUID = -6468967874576651628L;
  
  public NotSendableException(String s) {
    super(s);
  }
  
  public NotSendableException(Throwable t) {
    super(t);
  }
  
  public NotSendableException(String s, Throwable t) {
    super(s, t);
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\exceptions\NotSendableException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */