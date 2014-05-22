/*
 * Copyright 2014 BrightTag, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.brighttag.agathon.app;

import java.io.IOException;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.servlet.GuiceFilter;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brighttag.agathon.servlet.GuiceServletConfig;
import com.brighttag.agathon.servlet.ServiceRegistryServletContextListener;

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
    context.addEventListener(new ServiceRegistryServletContextListener());
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
