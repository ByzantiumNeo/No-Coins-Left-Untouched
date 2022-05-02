package org.java_websocket.framing;

import org.java_websocket.enums.Opcode;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.util.Charsetfunctions;

public class TextFrame extends DataFrame {
  public TextFrame() {
    super(Opcode.TEXT);
  }
  
  public void isValid() throws InvalidDataException {
    super.isValid();
    if (!Charsetfunctions.isValidUTF8(getPayloadData()))
      throw new InvalidDataException(1007, "Received text is no valid utf8 string!"); 
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\framing\TextFrame.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */