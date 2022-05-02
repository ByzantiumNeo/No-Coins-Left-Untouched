package org.java_websocket.exceptions;

public class InvalidHandshakeException extends InvalidDataException {
  private static final long serialVersionUID = -1426533877490484964L;
  
  public InvalidHandshakeException() {
    super(1002);
  }
  
  public InvalidHandshakeException(String s, Throwable t) {
    super(1002, s, t);
  }
  
  public InvalidHandshakeException(String s) {
    super(1002, s);
  }
  
  public InvalidHandshakeException(Throwable t) {
    super(1002, t);
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\exceptions\InvalidHandshakeException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */