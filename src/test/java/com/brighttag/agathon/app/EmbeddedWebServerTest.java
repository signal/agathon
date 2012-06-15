package com.brighttag.agathon.app;

import java.io.IOException;

import com.google.inject.servlet.GuiceFilter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

/**
 * Unit test the {@link EmbeddedWebServer}. Relies on PowerMock for mocking
 * final methods, namely {@link Server#start()} and {@link Server#stop()}.
 *
 * @author codyaray
 * @since 6/14/12
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Server.class)
public class EmbeddedWebServerTest  {

  private Server server;
  private Context context;

  @Before
  public void setUp() {
    server = createMock(Server.class);
    context = createMock(Context.class);

    expect(context.addFilter(GuiceFilter.class, "/*", 0)).andReturn(null);
    expect(context.addServlet(DefaultServlet.class, "/")).andReturn(null);
    context.addEventListener(isA(GuiceServletConfig.class));
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void start() throws Exception {
    server.start();
    replayAll();

    webServer().start();
  }

  @Test
  public void start_exception() throws Exception {
    final Exception exception = new Exception();
    server.start();
    expectLastCall().andThrow(exception);
    replayAll();

    try {
      webServer().start();
      fail("Expected IOException");
    } catch (IOException e) {
      assertEquals(exception, e.getCause());
    }
  }

  @Test
  public void isRunning() {
    expect(server.isRunning()).andReturn(false);
    replayAll();

    assertFalse(webServer().isRunning());
  }

  @Test
  public void stop() throws Exception {
    server.stop();
    replayAll();

    webServer().stop();
  }

  @Test
  public void stop_exception() throws Exception {
    final Exception exception = new Exception();
    server.stop();
    expectLastCall().andThrow(exception);
    replayAll();

    try {
      webServer().stop();
      fail("Expected IOException");
    } catch (IOException e) {
      assertEquals(exception, e.getCause());
    }
  }

  @Test
  public void waitForShutdown() throws Exception {
    server.join();
    replayAll();

    webServer().waitForShutdown();
  }

  @Test(expected = InterruptedException.class)
  public void waitForShutdown_interrupted() throws Exception {
    server.join();
    expectLastCall().andThrow(new InterruptedException());
    replayAll();

    webServer().waitForShutdown();
  }

  private EmbeddedWebServer webServer() {
    return new EmbeddedWebServer(server, context);
  }

}
