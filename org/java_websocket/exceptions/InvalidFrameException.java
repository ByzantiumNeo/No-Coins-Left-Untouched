package org.java_websocket.exceptions;

public class InvalidFrameException extends InvalidDataException {
  private static final long serialVersionUID = -9016496369828887591L;
  
  public InvalidFrameException() {
    super(1002);
  }
  
  public InvalidFrameException(String s) {
    super(1002, s);
  }
  
  public InvalidFrameException(Throwable t) {
    super(1002, t);
  }
  
  public InvalidFrameException(String s, Throwable t) {
    super(1002, s, t);
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\exceptions\InvalidFrameException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */