package com.brighttag.agathon.app;

import java.io.IOException;

/**
 * Runs Agathon on an embedded web server for testing.
 * <br/>
 * Before use, you must set the required system properties in
 * your IDE's Launch Configuration. See the README for details.
 *
 * @author codyaray
 * @since 6/14/12
 */
public class Main {

  private Main() { /* No instances */ }

  /**
   * Runs Agathon on an embedded web server for testing.
   *
   * @param args unused command-line args
   * @throws IOException if a problem occurs starting the server
   * @throws InterruptedException if a problem occurs waiting for the server to shutdown
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    EmbeddedWebServer server = new EmbeddedWebServer(8080);
    server.start();
    server.waitForShutdown();
  }

}
