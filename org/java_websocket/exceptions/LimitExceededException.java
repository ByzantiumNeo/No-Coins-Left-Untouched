package org.java_websocket.exceptions;

public class LimitExceededException extends InvalidDataException {
  private static final long serialVersionUID = 6908339749836826785L;
  
  private final int limit;
  
  public LimitExceededException() {
    this(2147483647);
  }
  
  public LimitExceededException(int limit) {
    super(1009);
    this.limit = limit;
  }
  
  public LimitExceededException(String s, int limit) {
    super(1009, s);
    this.limit = limit;
  }
  
  public LimitExceededException(String s) {
    this(s, 2147483647);
  }
  
  public int getLimit() {
    return this.limit;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\exceptions\LimitExceededException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */