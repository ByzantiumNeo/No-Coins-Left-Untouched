package org.java_websocket.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.java_websocket.AbstractWebSocket;
import org.java_websocket.SocketChannelIOHelper;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketAdapter;
import org.java_websocket.WebSocketFactory;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.WebSocketServerFactory;
import org.java_websocket.WrappedByteChannel;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.exceptions.WrappedIOException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.Handshakedata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WebSocketServer extends AbstractWebSocket implements Runnable {
  private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
  
  private final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
  
  private final Collection<WebSocket> connections;
  
  private final InetSocketAddress address;
  
  private ServerSocketChannel server;
  
  private Selector selector;
  
  private List<Draft> drafts;
  
  private Thread selectorthread;
  
  private final AtomicBoolean isclosed = new AtomicBoolean(false);
  
  protected List<WebSocketWorker> decoders;
  
  private List<WebSocketImpl> iqueue;
  
  private BlockingQueue<ByteBuffer> buffers;
  
  private int queueinvokes = 0;
  
  private final AtomicInteger queuesize = new AtomicInteger(0);
  
  private WebSocketServerFactory wsf = new DefaultWebSocketServerFactory();
  
  private int maxPendingConnections = -1;
  
  public WebSocketServer() {
    this(new InetSocketAddress(80), AVAILABLE_PROCESSORS, (List<Draft>)null);
  }
  
  public WebSocketServer(InetSocketAddress address) {
    this(address, AVAILABLE_PROCESSORS, (List<Draft>)null);
  }
  
  public WebSocketServer(InetSocketAddress address, int decodercount) {
    this(address, decodercount, (List<Draft>)null);
  }
  
  public WebSocketServer(InetSocketAddress address, List<Draft> drafts) {
    this(address, AVAILABLE_PROCESSORS, drafts);
  }
  
  public WebSocketServer(InetSocketAddress address, int decodercount, List<Draft> drafts) {
    this(address, decodercount, drafts, new HashSet<>());
  }
  
  public WebSocketServer(InetSocketAddress address, int decodercount, List<Draft> drafts, Collection<WebSocket> connectionscontainer) {
    if (address == null || decodercount < 1 || connectionscontainer == null)
      throw new IllegalArgumentException("address and connectionscontainer must not be null and you need at least 1 decoder"); 
    if (drafts == null) {
      this.drafts = Collections.emptyList();
    } else {
      this.drafts = drafts;
    } 
    this.address = address;
    this.connections = connectionscontainer;
    setTcpNoDelay(false);
    setReuseAddr(false);
    this.iqueue = new LinkedList<>();
    this.decoders = new ArrayList<>(decodercount);
    this.buffers = new LinkedBlockingQueue<>();
    for (int i = 0; i < decodercount; i++) {
      WebSocketWorker ex = new WebSocketWorker();
      this.decoders.add(ex);
    } 
  }
  
  public void start() {
    if (this.selectorthread != null)
      throw new IllegalStateException(getClass().getName() + " can only be started once."); 
    (new Thread(this)).start();
  }
  
  public void stop(int timeout) throws InterruptedException {
    List<WebSocket> socketsToClose;
    if (!this.isclosed.compareAndSet(false, true))
      return; 
    synchronized (this.connections) {
      socketsToClose = new ArrayList<>(this.connections);
    } 
    for (WebSocket ws : socketsToClose)
      ws.close(1001); 
    this.wsf.close();
    synchronized (this) {
      if (this.selectorthread != null && this.selector != null) {
        this.selector.wakeup();
        this.selectorthread.join(timeout);
      } 
    } 
  }
  
  public void stop() throws InterruptedException {
    stop(0);
  }
  
  public Collection<WebSocket> getConnections() {
    synchronized (this.connections) {
      return Collections.unmodifiableCollection(new ArrayList<>(this.connections));
    } 
  }
  
  public InetSocketAddress getAddress() {
    return this.address;
  }
  
  public int getPort() {
    int port = getAddress().getPort();
    if (port == 0 && this.server != null)
      port = this.server.socket().getLocalPort(); 
    return port;
  }
  
  public List<Draft> getDraft() {
    return Collections.unmodifiableList(this.drafts);
  }
  
  public void setMaxPendingConnections(int numberOfConnections) {
    this.maxPendingConnections = numberOfConnections;
  }
  
  public int getMaxPendingConnections() {
    return this.maxPendingConnections;
  }
  
  public void run() {
    if (!doEnsureSingleThread())
      return; 
    if (!doSetupSelectorAndServerThread())
      return; 
    try {
      int shutdownCount = 5;
      int selectTimeout = 0;
      while (!this.selectorthread.isInterrupted() && shutdownCount != 0) {
        SelectionKey key = null;
        try {
          if (this.isclosed.get())
            selectTimeout = 5; 
          int keyCount = this.selector.select(selectTimeout);
          if (keyCount == 0 && this.isclosed.get())
            shutdownCount--; 
          Set<SelectionKey> keys = this.selector.selectedKeys();
          Iterator<SelectionKey> i = keys.iterator();
          while (i.hasNext()) {
            key = i.next();
            if (!key.isValid())
              continue; 
            if (key.isAcceptable()) {
              doAccept(key, i);
              continue;
            } 
            if (key.isReadable() && !doRead(key, i))
              continue; 
            if (key.isWritable())
              doWrite(key); 
          } 
          doAdditionalRead();
        } catch (CancelledKeyException cancelledKeyException) {
        
        } catch (ClosedByInterruptException e) {
          return;
        } catch (WrappedIOException ex) {
          handleIOException(key, ex.getConnection(), ex.getIOException());
        } catch (IOException ex) {
          handleIOException(key, (WebSocket)null, ex);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } 
      } 
    } catch (RuntimeException e) {
      handleFatal((WebSocket)null, e);
    } finally {
      doServerShutdown();
    } 
  }
  
  private void doAdditionalRead() throws InterruptedException, IOException {
    while (!this.iqueue.isEmpty()) {
      WebSocketImpl conn = this.iqueue.remove(0);
      WrappedByteChannel c = (WrappedByteChannel)conn.getChannel();
      ByteBuffer buf = takeBuffer();
      try {
        if (SocketChannelIOHelper.readMore(buf, conn, c))
          this.iqueue.add(conn); 
        if (buf.hasRemaining()) {
          conn.inQueue.put(buf);
          queue(conn);
          continue;
        } 
        pushBuffer(buf);
      } catch (IOException e) {
        pushBuffer(buf);
        throw e;
      } 
    } 
  }
  
  private void doAccept(SelectionKey key, Iterator<SelectionKey> i) throws IOException, InterruptedException {
    if (!onConnect(key)) {
      key.cancel();
      return;
    } 
    SocketChannel channel = this.server.accept();
    if (channel == null)
      return; 
    channel.configureBlocking(false);
    Socket socket = channel.socket();
    socket.setTcpNoDelay(isTcpNoDelay());
    socket.setKeepAlive(true);
    WebSocketImpl w = this.wsf.createWebSocket((WebSocketAdapter)this, this.drafts);
    w.setSelectionKey(channel.register(this.selector, 1, w));
    try {
      w.setChannel(this.wsf.wrapChannel(channel, w.getSelectionKey()));
      i.remove();
      allocateBuffers((WebSocket)w);
    } catch (IOException ex) {
      if (w.getSelectionKey() != null)
        w.getSelectionKey().cancel(); 
      handleIOException(w.getSelectionKey(), (WebSocket)null, ex);
    } 
  }
  
  private boolean doRead(SelectionKey key, Iterator<SelectionKey> i) throws InterruptedException, WrappedIOException {
    WebSocketImpl conn = (WebSocketImpl)key.attachment();
    ByteBuffer buf = takeBuffer();
    if (conn.getChannel() == null) {
      key.cancel();
      handleIOException(key, (WebSocket)conn, new IOException());
      return false;
    } 
    try {
      if (SocketChannelIOHelper.read(buf, conn, conn.getChannel())) {
        if (buf.hasRemaining()) {
          conn.inQueue.put(buf);
          queue(conn);
          i.remove();
          if (conn.getChannel() instanceof WrappedByteChannel && ((WrappedByteChannel)conn
            .getChannel()).isNeedRead())
            this.iqueue.add(conn); 
        } else {
          pushBuffer(buf);
        } 
      } else {
        pushBuffer(buf);
      } 
    } catch (IOException e) {
      pushBuffer(buf);
      throw new WrappedIOException(conn, e);
    } 
    return true;
  }
  
  private void doWrite(SelectionKey key) throws WrappedIOException {
    WebSocketImpl conn = (WebSocketImpl)key.attachment();
    try {
      if (SocketChannelIOHelper.batch(conn, conn.getChannel()) && key.isValid())
        key.interestOps(1); 
    } catch (IOException e) {
      throw new WrappedIOException(conn, e);
    } 
  }
  
  private boolean doSetupSelectorAndServerThread() {
    this.selectorthread.setName("WebSocketSelector-" + this.selectorthread.getId());
    try {
      this.server = ServerSocketChannel.open();
      this.server.configureBlocking(false);
      ServerSocket socket = this.server.socket();
      socket.setReceiveBufferSize(16384);
      socket.setReuseAddress(isReuseAddr());
      socket.bind(this.address, getMaxPendingConnections());
      this.selector = Selector.open();
      this.server.register(this.selector, this.server.validOps());
      startConnectionLostTimer();
      for (WebSocketWorker ex : this.decoders)
        ex.start(); 
      onStart();
    } catch (IOException ex) {
      handleFatal((WebSocket)null, ex);
      return false;
    } 
    return true;
  }
  
  private boolean doEnsureSingleThread() {
    synchronized (this) {
      if (this.selectorthread != null)
        throw new IllegalStateException(getClass().getName() + " can only be started once."); 
      this.selectorthread = Thread.currentThread();
      if (this.isclosed.get())
        return false; 
    } 
    return true;
  }
  
  private void doServerShutdown() {
    stopConnectionLostTimer();
    if (this.decoders != null)
      for (WebSocketWorker w : this.decoders)
        w.interrupt();  
    if (this.selector != null)
      try {
        this.selector.close();
      } catch (IOException e) {
        this.log.error("IOException during selector.close", e);
        onError((WebSocket)null, e);
      }  
    if (this.server != null)
      try {
        this.server.close();
      } catch (IOException e) {
        this.log.error("IOException during server.close", e);
        onError((WebSocket)null, e);
      }  
  }
  
  protected void allocateBuffers(WebSocket c) throws InterruptedException {
    if (this.queuesize.get() >= 2 * this.decoders.size() + 1)
      return; 
    this.queuesize.incrementAndGet();
    this.buffers.put(createBuffer());
  }
  
  protected void releaseBuffers(WebSocket c) throws InterruptedException {}
  
  public ByteBuffer createBuffer() {
    return ByteBuffer.allocate(16384);
  }
  
  protected void queue(WebSocketImpl ws) throws InterruptedException {
    if (ws.getWorkerThread() == null) {
      ws.setWorkerThread(this.decoders.get(this.queueinvokes % this.decoders.size()));
      this.queueinvokes++;
    } 
    ws.getWorkerThread().put(ws);
  }
  
  private ByteBuffer takeBuffer() throws InterruptedException {
    return this.buffers.take();
  }
  
  private void pushBuffer(ByteBuffer buf) throws InterruptedException {
    if (this.buffers.size() > this.queuesize.intValue())
      return; 
    this.buffers.put(buf);
  }
  
  private void handleIOException(SelectionKey key, WebSocket conn, IOException ex) {
    if (key != null)
      key.cancel(); 
    if (conn != null) {
      conn.closeConnection(1006, ex.getMessage());
    } else if (key != null) {
      SelectableChannel channel = key.channel();
      if (channel != null && channel
        .isOpen()) {
        try {
          channel.close();
        } catch (IOException iOException) {}
        this.log.trace("Connection closed because of exception", ex);
      } 
    } 
  }
  
  private void handleFatal(WebSocket conn, Exception e) {
    this.log.error("Shutdown due to fatal error", e);
    onError(conn, e);
    if (this.decoders != null)
      for (WebSocketWorker w : this.decoders)
        w.interrupt();  
    if (this.selectorthread != null)
      this.selectorthread.interrupt(); 
    try {
      stop();
    } catch (InterruptedException e1) {
      Thread.currentThread().interrupt();
      this.log.error("Interrupt during stop", e);
      onError((WebSocket)null, e1);
    } 
  }
  
  public final void onWebsocketMessage(WebSocket conn, String message) {
    onMessage(conn, message);
  }
  
  public final void onWebsocketMessage(WebSocket conn, ByteBuffer blob) {
    onMessage(conn, blob);
  }
  
  public final void onWebsocketOpen(WebSocket conn, Handshakedata handshake) {
    if (addConnection(conn))
      onOpen(conn, (ClientHandshake)handshake); 
  }
  
  public final void onWebsocketClose(WebSocket conn, int code, String reason, boolean remote) {
    this.selector.wakeup();
    try {
      if (removeConnection(conn))
        onClose(conn, code, reason, remote); 
    } finally {
      try {
        releaseBuffers(conn);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } 
    } 
  }
  
  protected boolean removeConnection(WebSocket ws) {
    boolean removed = false;
    synchronized (this.connections) {
      if (this.connections.contains(ws)) {
        removed = this.connections.remove(ws);
      } else {
        this.log.trace("Removing connection which is not in the connections collection! Possible no handshake received! {}", ws);
      } 
    } 
    if (this.isclosed.get() && this.connections.isEmpty())
      this.selectorthread.interrupt(); 
    return removed;
  }
  
  protected boolean addConnection(WebSocket ws) {
    if (!this.isclosed.get())
      synchronized (this.connections) {
        return this.connections.add(ws);
      }  
    ws.close(1001);
    return true;
  }
  
  public final void onWebsocketError(WebSocket conn, Exception ex) {
    onError(conn, ex);
  }
  
  public final void onWriteDemand(WebSocket w) {
    WebSocketImpl conn = (WebSocketImpl)w;
    try {
      conn.getSelectionKey().interestOps(5);
    } catch (CancelledKeyException e) {
      conn.outQueue.clear();
    } 
    this.selector.wakeup();
  }
  
  public void onWebsocketCloseInitiated(WebSocket conn, int code, String reason) {
    onCloseInitiated(conn, code, reason);
  }
  
  public void onWebsocketClosing(WebSocket conn, int code, String reason, boolean remote) {
    onClosing(conn, code, reason, remote);
  }
  
  public void onCloseInitiated(WebSocket conn, int code, String reason) {}
  
  public void onClosing(WebSocket conn, int code, String reason, boolean remote) {}
  
  public final void setWebSocketFactory(WebSocketServerFactory wsf) {
    if (this.wsf != null)
      this.wsf.close(); 
    this.wsf = wsf;
  }
  
  public final WebSocketFactory getWebSocketFactory() {
    return (WebSocketFactory)this.wsf;
  }
  
  protected boolean onConnect(SelectionKey key) {
    return true;
  }
  
  private Socket getSocket(WebSocket conn) {
    WebSocketImpl impl = (WebSocketImpl)conn;
    return ((SocketChannel)impl.getSelectionKey().channel()).socket();
  }
  
  public InetSocketAddress getLocalSocketAddress(WebSocket conn) {
    return (InetSocketAddress)getSocket(conn).getLocalSocketAddress();
  }
  
  public InetSocketAddress getRemoteSocketAddress(WebSocket conn) {
    return (InetSocketAddress)getSocket(conn).getRemoteSocketAddress();
  }
  
  public void onMessage(WebSocket conn, ByteBuffer message) {}
  
  public void broadcast(String text) {
    broadcast(text, this.connections);
  }
  
  public void broadcast(byte[] data) {
    broadcast(data, this.connections);
  }
  
  public void broadcast(ByteBuffer data) {
    broadcast(data, this.connections);
  }
  
  public void broadcast(byte[] data, Collection<WebSocket> clients) {
    if (data == null || clients == null)
      throw new IllegalArgumentException(); 
    broadcast(ByteBuffer.wrap(data), clients);
  }
  
  public void broadcast(ByteBuffer data, Collection<WebSocket> clients) {
    if (data == null || clients == null)
      throw new IllegalArgumentException(); 
    doBroadcast(data, clients);
  }
  
  public void broadcast(String text, Collection<WebSocket> clients) {
    if (text == null || clients == null)
      throw new IllegalArgumentException(); 
    doBroadcast(text, clients);
  }
  
  private void doBroadcast(Object data, Collection<WebSocket> clients) {
    List<WebSocket> clientCopy;
    String strData = null;
    if (data instanceof String)
      strData = (String)data; 
    ByteBuffer byteData = null;
    if (data instanceof ByteBuffer)
      byteData = (ByteBuffer)data; 
    if (strData == null && byteData == null)
      return; 
    Map<Draft, List<Framedata>> draftFrames = new HashMap<>();
    synchronized (clients) {
      clientCopy = new ArrayList<>(clients);
    } 
    for (WebSocket client : clientCopy) {
      if (client != null) {
        Draft draft = client.getDraft();
        fillFrames(draft, draftFrames, strData, byteData);
        try {
          client.sendFrame(draftFrames.get(draft));
        } catch (WebsocketNotConnectedException websocketNotConnectedException) {}
      } 
    } 
  }
  
  private void fillFrames(Draft draft, Map<Draft, List<Framedata>> draftFrames, String strData, ByteBuffer byteData) {
    if (!draftFrames.containsKey(draft)) {
      List<Framedata> frames = null;
      if (strData != null)
        frames = draft.createFrames(strData, false); 
      if (byteData != null)
        frames = draft.createFrames(byteData, false); 
      if (frames != null)
        draftFrames.put(draft, frames); 
    } 
  }
  
  public abstract void onOpen(WebSocket paramWebSocket, ClientHandshake paramClientHandshake);
  
  public abstract void onClose(WebSocket paramWebSocket, int paramInt, String paramString, boolean paramBoolean);
  
  public abstract void onMessage(WebSocket paramWebSocket, String paramString);
  
  public abstract void onError(WebSocket paramWebSocket, Exception paramException);
  
  public abstract void onStart();
  
  public class WebSocketWorker extends Thread {
    private BlockingQueue<WebSocketImpl> iqueue;
    
    public WebSocketWorker() {
      this.iqueue = new LinkedBlockingQueue<>();
      setName("WebSocketWorker-" + getId());
      setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
              WebSocketServer.this.log.error("Uncaught exception in thread {}: {}", t.getName(), e);
            }
          });
    }
    
    public void put(WebSocketImpl ws) throws InterruptedException {
      this.iqueue.put(ws);
    }
    
    public void run() {
      WebSocketImpl ws = null;
      try {
        while (true) {
          ws = this.iqueue.take();
          ByteBuffer buf = ws.inQueue.poll();
          assert buf != null;
          doDecode(ws, buf);
          ws = null;
        } 
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (RuntimeException e) {
        WebSocketServer.this.handleFatal((WebSocket)ws, e);
      } 
    }
    
    private void doDecode(WebSocketImpl ws, ByteBuffer buf) throws InterruptedException {
      try {
        ws.decode(buf);
      } catch (Exception e) {
        WebSocketServer.this.log.error("Error while reading from remote connection", e);
      } finally {
        WebSocketServer.this.pushBuffer(buf);
      } 
    }
  }
}


/* Location:              D:\downloads\NotEnoughCoins-0.9.2.1-all (1).jar!\org\java_websocket\server\WebSocketServer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */