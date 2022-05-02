package org.java_websocket.framing;

import org.java_websocket.enums.Opcode;
import org.java_websocket.exceptions.InvalidDataException;

public abstract class DataFrame extends FramedataImpl1 {
  public DataFrame(Opcode opcode) {
    super(opcode);
  }
  
  public void isValid() throws InvalidDataException {}
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\framing\DataFrame.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */