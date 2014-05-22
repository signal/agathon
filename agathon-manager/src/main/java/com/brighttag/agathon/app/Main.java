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
    EmbeddedWebServer server = new EmbeddedWebServer(8094);
    server.start();
    server.waitForShutdown();
  }

}
