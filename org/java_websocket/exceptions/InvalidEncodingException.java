package org.java_websocket.exceptions;

import java.io.UnsupportedEncodingException;

public class InvalidEncodingException extends RuntimeException {
  private final UnsupportedEncodingException encodingException;
  
  public InvalidEncodingException(UnsupportedEncodingException encodingException) {
    if (encodingException == null)
      throw new IllegalArgumentException(); 
    this.encodingException = encodingException;
  }
  
  public UnsupportedEncodingException getEncodingException() {
    return this.encodingException;
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\exceptions\InvalidEncodingException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */