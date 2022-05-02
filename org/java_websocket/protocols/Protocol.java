package org.java_websocket.protocols;

import java.util.regex.Pattern;

public class Protocol implements IProtocol {
  private static final Pattern patternSpace = Pattern.compile(" ");
  
  private static final Pattern patternComma = Pattern.compile(",");
  
  private final String providedProtocol;
  
  public Protocol(String providedProtocol) {
    if (providedProtocol == null)
      throw new IllegalArgumentException(); 
    this.providedProtocol = providedProtocol;
  }
  
  public boolean acceptProvidedProtocol(String inputProtocolHeader) {
    if ("".equals(this.providedProtocol))
      return true; 
    String protocolHeader = patternSpace.matcher(inputProtocolHeader).replaceAll("");
    String[] headers = patternComma.split(protocolHeader);
    for (String header : headers) {
      if (this.providedProtocol.equals(header))
        return true; 
    } 
    return false;
  }
  
  public String getProvidedProtocol() {
    return this.providedProtocol;
  }
  
  public IProtocol copyInstance() {
    return new Protocol(getProvidedProtocol());
  }
  
  public String toString() {
    return getProvidedProtocol();
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    Protocol protocol = (Protocol)o;
    return this.providedProtocol.equals(protocol.providedProtocol);
  }
  
  public int hashCode() {
    return this.providedProtocol.hashCode();
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\protocols\Protocol.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */