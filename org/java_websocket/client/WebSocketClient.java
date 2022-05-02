package org.java_websocket.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.java_websocket.AbstractWebSocket;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.WebSocketListener;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.enums.Opcode;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.exceptions.InvalidHandshakeException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshakeBuilder;
import org.java_websocket.handshake.HandshakeImpl1Client;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.protocols.IProtocol;

public abstract class WebSocketClient extends AbstractWebSocket implements Runnable, WebSocket {
  protected URI uri = null;
  
  private WebSocketImpl engine = null;
  
  private Socket socket = null;
  
  private SocketFactory socketFactory = null;
  
  private OutputStream ostream;
  
  private Proxy proxy = Proxy.NO_PROXY;
  
  private Thread writeThread;
  
  private Thread connectReadThread;
  
  private Draft draft;
  
  private Map<String, String> headers;
  
  private CountDownLatch connectLatch = new CountDownLatch(1);
  
  private CountDownLatch closeLatch = new CountDownLatch(1);
  
  private int connectTimeout = 0;
  
  private DnsResolver dnsResolver = null;
  
  public WebSocketClient(URI serverUri) {
    this(serverUri, (Draft)new Draft_6455());
  }
  
  public WebSocketClient(URI serverUri, Draft protocolDraft) {
    this(serverUri, protocolDraft, (Map<String, String>)null, 0);
  }
  
  public WebSocketClient(URI serverUri, Map<String, String> httpHeaders) {
    this(serverUri, (Draft)new Draft_6455(), httpHeaders);
  }
  
  public WebSocketClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders) {
    this(serverUri, protocolDraft, httpHeaders, 0);
  }
  
  public WebSocketClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout) {
    if (serverUri == null)
      throw new IllegalArgumentException(); 
    if (protocolDraft == null)
      throw new IllegalArgumentException("null as draft is permitted for `WebSocketServer` only!"); 
    this.uri = serverUri;
    this.draft = protocolDraft;
    this.dnsResolver = new DnsResolver() {
        public InetAddress resolve(URI uri) throws UnknownHostException {
          return InetAddress.getByName(uri.getHost());
        }
      };
    if (httpHeaders != null) {
      this.headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
      this.headers.putAll(httpHeaders);
    } 
    this.connectTimeout = connectTimeout;
    setTcpNoDelay(false);
    setReuseAddr(false);
    this.engine = new WebSocketImpl((WebSocketListener)this, protocolDraft);
  }
  
  public URI getURI() {
    return this.uri;
  }
  
  public Draft getDraft() {
    return this.draft;
  }
  
  public Socket getSocket() {
    return this.socket;
  }
  
  public void addHeader(String key, String value) {
    if (this.headers == null)
      this.headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER); 
    this.headers.put(key, value);
  }
  
  public String removeHeader(String key) {
    if (this.headers == null)
      return null; 
    return this.headers.remove(key);
  }
  
  public void clearHeaders() {
    this.headers = null;
  }
  
  public void setDnsResolver(DnsResolver dnsResolver) {
    this.dnsResolver = dnsResolver;
  }
  
  public void reconnect() {
    reset();
    connect();
  }
  
  public boolean reconnectBlocking() throws InterruptedException {
    reset();
    return connectBlocking();
  }
  
  private void reset() {
    Thread current = Thread.currentThread();
    if (current == this.writeThread || current == this.connectReadThread)
      throw new IllegalStateException("You cannot initialize a reconnect out of the websocket thread. Use reconnect in another thread to ensure a successful cleanup."); 
    try {
      closeBlocking();
      if (this.writeThread != null) {
        this.writeThread.interrupt();
        this.writeThread = null;
      } 
      if (this.connectReadThread != null) {
        this.connectReadThread.interrupt();
        this.connectReadThread = null;
      } 
      this.draft.reset();
      if (this.socket != null) {
        this.socket.close();
        this.socket = null;
      } 
    } catch (Exception e) {
      onError(e);
      this.engine.closeConnection(1006, e.getMessage());
      return;
    } 
    this.connectLatch = new CountDownLatch(1);
    this.closeLatch = new CountDownLatch(1);
    this.engine = new WebSocketImpl((WebSocketListener)this, this.draft);
  }
  
  public void connect() {
    if (this.connectReadThread != null)
      throw new IllegalStateException("WebSocketClient objects are not reuseable"); 
    this.connectReadThread = new Thread(this);
    this.connectReadThread.setName("WebSocketConnectReadThread-" + this.connectReadThread.getId());
    this.connectReadThread.start();
  }
  
  public boolean connectBlocking() throws InterruptedException {
    connect();
    this.connectLatch.await();
    return this.engine.isOpen();
  }
  
  public boolean connectBlocking(long timeout, TimeUnit timeUnit) throws InterruptedException {
    connect();
    return (this.connectLatch.await(timeout, timeUnit) && this.engine.isOpen());
  }
  
  public void close() {
    if (this.writeThread != null)
      this.engine.close(1000); 
  }
  
  public void closeBlocking() throws InterruptedException {
    close();
    this.closeLatch.await();
  }
  
  public void send(String text) {
    this.engine.send(text);
  }
  
  public void send(byte[] data) {
    this.engine.send(data);
  }
  
  public <T> T getAttachment() {
    return (T)this.engine.getAttachment();
  }
  
  public <T> void setAttachment(T attachment) {
    this.engine.setAttachment(attachment);
  }
  
  protected Collection<WebSocket> getConnections() {
    return (Collection)Collections.singletonList(this.engine);
  }
  
  public void sendPing() {
    this.engine.sendPing();
  }
  
  public void run() {
    InputStream istream;
    try {
      boolean upgradeSocketToSSLSocket = prepareSocket();
      this.socket.setTcpNoDelay(isTcpNoDelay());
      this.socket.setReuseAddress(isReuseAddr());
      if (!this.socket.isConnected()) {
        InetSocketAddress addr = new InetSocketAddress(this.dnsResolver.resolve(this.uri), getPort());
        this.socket.connect(addr, this.connectTimeout);
      } 
      if (upgradeSocketToSSLSocket && "wss".equals(this.uri.getScheme()))
        upgradeSocketToSSL(); 
      if (this.socket instanceof SSLSocket) {
        SSLSocket sslSocket = (SSLSocket)this.socket;
        SSLParameters sslParameters = sslSocket.getSSLParameters();
        onSetSSLParameters(sslParameters);
        sslSocket.setSSLParameters(sslParameters);
      } 
      istream = this.socket.getInputStream();
      this.ostream = this.socket.getOutputStream();
      sendHandshake();
    } catch (Exception e) {
      onWebsocketError((WebSocket)this.engine, e);
      this.engine.closeConnection(-1, e.getMessage());
      return;
    } catch (InternalError e) {
      if (e.getCause() instanceof java.lang.reflect.InvocationTargetException && e.getCause()
        .getCause() instanceof IOException) {
        IOException cause = (IOException)e.getCause().getCause();
        onWebsocketError((WebSocket)this.engine, cause);
        this.engine.closeConnection(-1, cause.getMessage());
        return;
      } 
      throw e;
    } 
    this.writeThread = new Thread(new WebsocketWriteThread(this));
    this.writeThread.start();
    byte[] rawbuffer = new byte[16384];
    try {
      int readBytes;
      while (!isClosing() && !isClosed() && (readBytes = istream.read(rawbuffer)) != -1)
        this.engine.decode(ByteBuffer.wrap(rawbuffer, 0, readBytes)); 
      this.engine.eot();
    } catch (IOException e) {
      handleIOException(e);
    } catch (RuntimeException e) {
      onError(e);
      this.engine.closeConnection(1006, e.getMessage());
    } 
    this.connectReadThread = null;
  }
  
  private void upgradeSocketToSSL() throws NoSuchAlgorithmException, KeyManagementException, IOException {
    SSLSocketFactory factory;
    if (this.socketFactory instanceof SSLSocketFactory) {
      factory = (SSLSocketFactory)this.socketFactory;
    } else {
      SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
      sslContext.init(null, null, null);
      factory = sslContext.getSocketFactory();
    } 
    this.socket = factory.createSocket(this.socket, this.uri.getHost(), getPort(), true);
  }
  
  private boolean prepareSocket() throws IOException {
    boolean upgradeSocketToSSLSocket = false;
    if (this.proxy != Proxy.NO_PROXY) {
      this.socket = new Socket(this.proxy);
      upgradeSocketToSSLSocket = true;
    } else if (this.socketFactory != null) {
      this.socket = this.socketFactory.createSocket();
    } else if (this.socket == null) {
      this.socket = new Socket(this.proxy);
      upgradeSocketToSSLSocket = true;
    } else if (this.socket.isClosed()) {
      throw new IOException();
    } 
    return upgradeSocketToSSLSocket;
  }
  
  protected void onSetSSLParameters(SSLParameters sslParameters) {
    sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
  }
  
  private int getPort() {
    int port = this.uri.getPort();
    String scheme = this.uri.getScheme();
    if ("wss".equals(scheme))
      return (port == -1) ? 443 : port; 
    if ("ws".equals(scheme))
      return (port == -1) ? 80 : port; 
    throw new IllegalArgumentException("unknown scheme: " + scheme);
  }
  
  private void sendHandshake() throws InvalidHandshakeException {
    String path, part1 = this.uri.getRawPath();
    String part2 = this.uri.getRawQuery();
    if (part1 == null || part1.length() == 0) {
      path = "/";
    } else {
      path = part1;
    } 
    if (part2 != null)
      path = path + '?' + part2; 
    int port = getPort();
    String host = this.uri.getHost() + ((port != 80 && port != 443) ? (":" + port) : "");
    HandshakeImpl1Client handshake = new HandshakeImpl1Client();
    handshake.setResourceDescriptor(path);
    handshake.put("Host", host);
    if (this.headers != null)
      for (Map.Entry<String, String> kv : this.headers.entrySet())
        handshake.put(kv.getKey(), kv.getValue());  
    this.engine.startHandshake((ClientHandshakeBuilder)handshake);
  }
  
  public ReadyState getReadyState() {
    return this.engine.getReadyState();
  }
  
  public final void onWebsocketMessage(WebSocket conn, String message) {
    onMessage(message);
  }
  
  public final void onWebsocketMessage(WebSocket conn, ByteBuffer blob) {
    onMessage(blob);
  }
  
  public final void onWebsocketOpen(WebSocket conn, Handshakedata handshake) {
    startConnectionLostTimer();
    onOpen((ServerHandshake)handshake);
    this.connectLatch.countDown();
  }
  
  public final void onWebsocketClose(WebSocket conn, int code, String reason, boolean remote) {
    stopConnectionLostTimer();
    if (this.writeThread != null)
      this.writeThread.interrupt(); 
    onClose(code, reason, remote);
    this.connectLatch.countDown();
    this.closeLatch.countDown();
  }
  
  public final void onWebsocketError(WebSocket conn, Exception ex) {
    onError(ex);
  }
  
  public final void onWriteDemand(WebSocket conn) {}
  
  public void onWebsocketCloseInitiated(WebSocket conn, int code, String reason) {
    onCloseInitiated(code, reason);
  }
  
  public void onWebsocketClosing(WebSocket conn, int code, String reason, boolean remote) {
    onClosing(code, reason, remote);
  }
  
  public void onCloseInitiated(int code, String reason) {}
  
  public void onClosing(int code, String reason, boolean remote) {}
  
  public WebSocket getConnection() {
    return (WebSocket)this.engine;
  }
  
  public InetSocketAddress getLocalSocketAddress(WebSocket conn) {
    if (this.socket != null)
      return (InetSocketAddress)this.socket.getLocalSocketAddress(); 
    return null;
  }
  
  public InetSocketAddress getRemoteSocketAddress(WebSocket conn) {
    if (this.socket != null)
      return (InetSocketAddress)this.socket.getRemoteSocketAddress(); 
    return null;
  }
  
  public void onMessage(ByteBuffer bytes) {}
  
  private class WebsocketWriteThread implements Runnable {
    private final WebSocketClient webSocketClient;
    
    WebsocketWriteThread(WebSocketClient webSocketClient) {
      this.webSocketClient = webSocketClient;
    }
    
    public void run() {
      Thread.currentThread().setName("WebSocketWriteThread-" + Thread.currentThread().getId());
      try {
        runWriteData();
      } catch (IOException e) {
        WebSocketClient.this.handleIOException(e);
      } finally {
        closeSocket();
        WebSocketClient.this.writeThread = null;
      } 
    }
    
    private void runWriteData() throws IOException {
      try {
        while (!Thread.interrupted()) {
          ByteBuffer buffer = WebSocketClient.this.engine.outQueue.take();
          WebSocketClient.this.ostream.write(buffer.array(), 0, buffer.limit());
          WebSocketClient.this.ostream.flush();
        } 
      } catch (InterruptedException e) {
        for (ByteBuffer buffer : WebSocketClient.this.engine.outQueue) {
          WebSocketClient.this.ostream.write(buffer.array(), 0, buffer.limit());
          WebSocketClient.this.ostream.flush();
        } 
        Thread.currentThread().interrupt();
      } 
    }
    
    private void closeSocket() {
      try {
        if (WebSocketClient.this.socket != null)
          WebSocketClient.this.socket.close(); 
      } catch (IOException ex) {
        WebSocketClient.this.onWebsocketError(this.webSocketClient, ex);
      } 
    }
  }
  
  public void setProxy(Proxy proxy) {
    if (proxy == null)
      throw new IllegalArgumentException(); 
    this.proxy = proxy;
  }
  
  @Deprecated
  public void setSocket(Socket socket) {
    if (this.socket != null)
      throw new IllegalStateException("socket has already been set"); 
    this.socket = socket;
  }
  
  public void setSocketFactory(SocketFactory socketFactory) {
    this.socketFactory = socketFactory;
  }
  
  public void sendFragmentedFrame(Opcode op, ByteBuffer buffer, boolean fin) {
    this.engine.sendFragmentedFrame(op, buffer, fin);
  }
  
  public boolean isOpen() {
    return this.engine.isOpen();
  }
  
  public boolean isFlushAndClose() {
    return this.engine.isFlushAndClose();
  }
  
  public boolean isClosed() {
    return this.engine.isClosed();
  }
  
  public boolean isClosing() {
    return this.engine.isClosing();
  }
  
  public boolean hasBufferedData() {
    return this.engine.hasBufferedData();
  }
  
  public void close(int code) {
    this.engine.close(code);
  }
  
  public void close(int code, String message) {
    this.engine.close(code, message);
  }
  
  public void closeConnection(int code, String message) {
    this.engine.closeConnection(code, message);
  }
  
  public void send(ByteBuffer bytes) {
    this.engine.send(bytes);
  }
  
  public void sendFrame(Framedata framedata) {
    this.engine.sendFrame(framedata);
  }
  
  public void sendFrame(Collection<Framedata> frames) {
    this.engine.sendFrame(frames);
  }
  
  public InetSocketAddress getLocalSocketAddress() {
    return this.engine.getLocalSocketAddress();
  }
  
  public InetSocketAddress getRemoteSocketAddress() {
    return this.engine.getRemoteSocketAddress();
  }
  
  public String getResourceDescriptor() {
    return this.uri.getPath();
  }
  
  public boolean hasSSLSupport() {
    return this.engine.hasSSLSupport();
  }
  
  public SSLSession getSSLSession() {
    return this.engine.getSSLSession();
  }
  
  public IProtocol getProtocol() {
    return this.engine.getProtocol();
  }
  
  private void handleIOException(IOException e) {
    if (e instanceof javax.net.ssl.SSLException)
      onError(e); 
    this.engine.eot();
  }
  
  public abstract void onOpen(ServerHandshake paramServerHandshake);
  
  public abstract void onMessage(String paramString);
  
  public abstract void onClose(int paramInt, String paramString, boolean paramBoolean);
  
  public abstract void onError(Exception paramException);
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\client\WebSocketClient.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */