package org.java_websocket.client;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

public interface DnsResolver {
  InetAddress resolve(URI paramURI) throws UnknownHostException;
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\client\DnsResolver.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */