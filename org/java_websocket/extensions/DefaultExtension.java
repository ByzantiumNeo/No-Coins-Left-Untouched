package org.java_websocket.extensions;

import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.exceptions.InvalidFrameException;
import org.java_websocket.framing.Framedata;

public class DefaultExtension implements IExtension {
  public void decodeFrame(Framedata inputFrame) throws InvalidDataException {}
  
  public void encodeFrame(Framedata inputFrame) {}
  
  public boolean acceptProvidedExtensionAsServer(String inputExtension) {
    return true;
  }
  
  public boolean acceptProvidedExtensionAsClient(String inputExtension) {
    return true;
  }
  
  public void isFrameValid(Framedata inputFrame) throws InvalidDataException {
    if (inputFrame.isRSV1() || inputFrame.isRSV2() || inputFrame.isRSV3())
      throw new InvalidFrameException("bad rsv RSV1: " + inputFrame
          .isRSV1() + " RSV2: " + inputFrame.isRSV2() + " RSV3: " + inputFrame
          .isRSV3()); 
  }
  
  public String getProvidedExtensionAsClient() {
    return "";
  }
  
  public String getProvidedExtensionAsServer() {
    return "";
  }
  
  public IExtension copyInstance() {
    return new DefaultExtension();
  }
  
  public void reset() {}
  
  public String toString() {
    return getClass().getSimpleName();
  }
  
  public int hashCode() {
    return getClass().hashCode();
  }
  
  public boolean equals(Object o) {
    return (this == o || (o != null && getClass() == o.getClass()));
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\extensions\DefaultExtension.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */