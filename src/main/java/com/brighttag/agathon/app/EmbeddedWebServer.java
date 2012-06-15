package com.brighttag.agathon.app;

import java.io.IOException;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.servlet.GuiceFilter;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An embedded web server for running Agathon.
 *
 * @author codyaray
 * @since 6/14/12
 */
public class EmbeddedWebServer {

  private static final Logger LOG = LoggerFactory.getLogger(EmbeddedWebServer.class);

  private final Server server;

  public EmbeddedWebServer(int port) {
    this(new Server(port));
  }

  private EmbeddedWebServer(Server server) {
    this(server, new Context(server, "/"));
  }

  @VisibleForTesting EmbeddedWebServer(Server server, Context context) {
    this.server = server;

    context.addFilter(GuiceFilter.class, "/*", 0);
    context.addServlet(DefaultServlet.class, "/");
    context.addEventListener(new GuiceServletConfig());
  }

  /**
   * Start the embedded web server.
   * @throws IOException if a problem occurs starting the server.
   */
  public void start() throws IOException {
    try {
      server.start();
    } catch (Exception e) {
      // Ok to catch Exception here
      LOG.error("Error starting HTTP Server", e);
      throw new IOException("Unable to start HTTP Server", e);
    }
  }

  /**
   * Returns {@code true} if the server is running.
   * @return {@code true} if the server is running.
   */
  public boolean isRunning() {
    return server.isRunning();
  }

  /**
   * Stop the embedded web server.
   * @throws IOException if a problem occurs stopping the server.
   */
  public void stop() throws IOException {
    try {
      server.stop();
    } catch (Exception e) {
      // Ok to catch Exception here
      LOG.error("Error stopping HTTP Server", e);
      throw new IOException("Unable to stop HTTP Server", e);
    }
  }

  /**
   * Blocks until the server is stopped.
   * @throws InterruptedException if a problem occurs waiting for the server to shutdown.
   */
  public void waitForShutdown() throws InterruptedException {
    server.join();
  }

}
