package org.java_websocket.framing;

import java.nio.ByteBuffer;
import org.java_websocket.enums.Opcode;

public interface Framedata {
  boolean isFin();
  
  boolean isRSV1();
  
  boolean isRSV2();
  
  boolean isRSV3();
  
  boolean getTransfereMasked();
  
  Opcode getOpcode();
  
  ByteBuffer getPayloadData();
  
  void append(Framedata paramFramedata);
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\framing\Framedata.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */