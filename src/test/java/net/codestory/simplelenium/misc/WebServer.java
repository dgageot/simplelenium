package net.codestory.simplelenium.misc;

import java.io.*;
import java.net.*;
import java.util.*;

import org.simpleframework.http.core.*;
import org.simpleframework.transport.connect.*;

public class WebServer {
  private final Container container;
  private int port;
  private SocketConnection connection;

  public WebServer(Container container) {
    this.container = container;
  }

  private WebServer start(int port) throws IOException {
    this.connection = new SocketConnection(new ContainerServer(container));
    this.connection.connect(new InetSocketAddress(port));
    this.port = port;

    return this;
  }

  public WebServer startOnRandomPort() {
    Random random = new Random();
    for (int i = 0; i < 30; i++) {
      try {
        int port = 8183 + random.nextInt(10000);
        start(port);
        return this;
      } catch (Exception e) {
        System.err.println("Unable to bind server " + e);
      }
    }
    throw new IllegalStateException("Unable to start server");
  }

  public int port() {
    return port;
  }

  public void stop() throws IOException {
    connection.close();
  }
}
