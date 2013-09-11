package com.brighttag.agathon.service.impl;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Service;
import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A registry for managing {@link Service}s. It itself implements the {@link Service} interface.
 * This implementation will fail and throw an exception if any of the services fail to startup.
 *
 * @author Eric Lunt
 * @since 10/6/10
 */
public class ServiceRegistry extends AbstractIdleService {

  private static final Logger LOG = LoggerFactory.getLogger(ServiceRegistry.class);

  private final Set<Service> services;

  @Inject
  public ServiceRegistry(Set<Service> services) {
    this.services = ImmutableSet.copyOf(services);
  }

  @Override
  protected void startUp() throws Exception {
    List<Future<State>> startFutures = Lists.newArrayList();
    LOG.info("Starting service registry");

    // Start the services in parallel
    for (Service service : services) {
      Future<State> startFuture = service.start();
      startFutures.add(startFuture);
      LOG.info("Start service {}, future={}", serviceName(service), startFuture);
    }

    // Wait for the services ... or let the get() throw an exception
    for (Future<State> stateFuture : startFutures) {
      State state = stateFuture.get();
      LOG.info("Service future {} result={}", stateFuture, state);
    }

    LOG.info("Finished starting service registry");
  }

  @Override
  protected void shutDown() {
    List<Future<State>> stopFutures = Lists.newArrayList();
    LOG.info("Stopping service registry");

    // Stop the services in parallel
    for (Service service : services) {
      if (service.isRunning()) {
        Future<State> stopFuture = service.stop();
        stopFutures.add(stopFuture);
        LOG.info("Stop service {}, future={}", serviceName(service), stopFuture);
      }
    }

    // Wait for the services ... and swallow exceptions
    for (Future<State> stateFuture : stopFutures) {
      try {
        State state = stateFuture.get();
        LOG.info("Service future {} result={}", stateFuture, state);
      } catch (Exception e) {
        // Ok to catch Exception here
        LOG.warn("Problem stopping service, but pushing on", e);
      }
    }

    LOG.info("Finished stopping service registry");
  }

  private String serviceName(Service service) {
    return service.getClass().getSimpleName();
  }

}
