package com.brighttag.agathon.service.impl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.Service.State;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests the {@link ServiceRegistry} class. Not exhaustive, but gets the job done.
 *
 * @author Eric Lunt
 * @since 10/6/10
 */
public class ServiceRegistryTest {

  @Test
  public void lifecycle() throws Exception {
    final CountDownLatch runningLatch = new CountDownLatch(2);
    final CountDownLatch waitForShutdownLatch = new CountDownLatch(1);
    Service service1 = new FakeService(runningLatch, waitForShutdownLatch);
    Service service2 = new FakeService(runningLatch, waitForShutdownLatch);
    ServiceRegistry registry = new ServiceRegistry(ImmutableSet.of(service1, service2));
    registry.startAndWait();
    runningLatch.await();
    // Now that everyone's running, shut it down
    Future<State> stopFuture = registry.stop();
    waitForShutdownLatch.countDown();
    assertEquals(Service.State.TERMINATED, stopFuture.get());
  }

  @Test
  public void lifecycle_startupException() throws Exception {
    Service service = new FailToStartService();
    ServiceRegistry registry = new ServiceRegistry(ImmutableSet.of(service));
    try {
      registry.startAndWait();
      fail("Should have thrown an exception");
    } catch (Exception e) {
      // Ok to catch Exception here
      assertThat(Throwables.getRootCause(e), is(IllegalStateException.class));
    }
  }

  @Test
  public void lifecycle_runException() throws Exception {
    final CountDownLatch runningLatch = new CountDownLatch(1);
    final CountDownLatch waitForShutdownLatch = new CountDownLatch(1);
    Service service1 = new FakeService(runningLatch, waitForShutdownLatch);
    Service service2 = new FailToRunService();
    ServiceRegistry registry = new ServiceRegistry(ImmutableSet.of(service1, service2));
    registry.startAndWait();
    runningLatch.await();
    // Now that everyone's running, shut it down
    Future<State> stopFuture = registry.stop();
    waitForShutdownLatch.countDown();
    assertEquals(Service.State.TERMINATED, stopFuture.get());
  }

  @Test
  public void lifecycle_shutdownException() throws Exception {
    final CountDownLatch runningLatch = new CountDownLatch(2);
    final CountDownLatch waitForShutdownLatch = new CountDownLatch(1);
    Service service1 = new FakeService(runningLatch, waitForShutdownLatch);
    Service service2 = new FailToStopService(runningLatch, waitForShutdownLatch);
    ServiceRegistry registry = new ServiceRegistry(ImmutableSet.of(service1, service2));
    registry.startAndWait();
    runningLatch.await();
    // Now that everyone's running, shut it down
    Future<State> stopFuture = registry.stop();
    waitForShutdownLatch.countDown();
    assertEquals(Service.State.TERMINATED, stopFuture.get());
  }

  /**
   * Fake service that uses a {@code runningLatch} to notify the test when all
   * {@link FakeService}s are running, and a {@code waitForShutDownLatch} to await
   * for the shutdown command from the test.
   */
  private static class FakeService extends AbstractExecutionThreadService {
    private final CountDownLatch runningLatch;
    private final CountDownLatch waitForShutdownLatch;
    FakeService(CountDownLatch runningLatch, CountDownLatch waitForShutdownLatch) {
      this.runningLatch = runningLatch;
      this.waitForShutdownLatch = waitForShutdownLatch;
    }
    @Override protected void run() throws Exception {
      while (isRunning()) {
        runningLatch.countDown();
        waitForShutdownLatch.await();
      }
    }
  }

  /**
   * Fake service to throw an exception on startUp.
   */
  private static class FailToStartService extends AbstractExecutionThreadService {
    @Override protected void startUp() throws Exception {
      throw new IllegalStateException();
    }
    @Override protected void run() throws Exception { }
  }

  /**
   * Fake service to throw an exception on run.
   */
  private static class FailToRunService extends AbstractExecutionThreadService {
    @Override protected void run() throws Exception {
      throw new IllegalStateException();
    }
  }

  /**
   * Fake service to throw an exception on shutDown.
   */
  private static class FailToStopService extends FakeService {
    FailToStopService(CountDownLatch runningLatch, CountDownLatch waitForShutdownLatch) {
      super(runningLatch, waitForShutdownLatch);
    }
    @Override protected void shutDown() throws Exception {
      throw new IllegalStateException();
    }
  }

}
