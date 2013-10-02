package com.brighttag.agathon.service.impl;

import java.util.Collection;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.SetMultimap;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.CassandraRing;
import com.brighttag.agathon.service.SeedService;

/**
 * A {@link SeedService} that includes a specified number of seeds per data center.
 *
 * @author codyaray
 * @since 5/25/12
 */
public class PerDataCenterSeedService implements SeedService {

  private static final Logger LOG = LoggerFactory.getLogger(PerDataCenterSeedService.class);

  private static final Function<CassandraInstance, String> INSTANCE_TO_PUBLIC_IP =
    new Function<CassandraInstance, String>() {
      @Override
      public @Nullable String apply(@Nullable CassandraInstance instance) {
        return (instance != null) ? instance.getPublicIpAddress() : null;
      }
    };

  private final int numSeeds;

  @Inject
  public PerDataCenterSeedService(@Named(ServiceModule.SEEDS_PER_DATACENTER_PROPERTY) int numSeeds) {
    this.numSeeds = numSeeds;
  }

  @Override
  public ImmutableSet<String> getSeeds(CassandraRing ring) {
    ImmutableSet.Builder<String> seedBuilder = ImmutableSet.builder();
    SetMultimap<String, CassandraInstance> dataCenterToInstanceMap =
        buildDataCenterToInstanceMap(ring.getInstances());

    for (String dc : dataCenterToInstanceMap.keySet()) {
      seedBuilder.addAll(getSeeds(dc, dataCenterToInstanceMap.get(dc)));
    }

    return seedBuilder.build();
  }

  /**
   * Return the first {@code numSeeds} seeds from the {@code instancesInDC} collection
   * for the given {@code dataCenter}.
   *
   * @param dataCenter the data center name
   * @param instancesInDC a collection of instances in the data center
   * @return the public IP addresses of the first {@code numSeeds} hosts
   */
  private Iterable<String> getSeeds(String dataCenter, Collection<CassandraInstance> instancesInDC) {
    int size = instancesInDC.size();
    if (size < numSeeds) {
      LOG.warn("Too few seeds for data center '{}'. Continuing with {} seeds.", dataCenter, size);
    }

    return Iterables.transform(Iterables.limit(instancesInDC, numSeeds), INSTANCE_TO_PUBLIC_IP);
  }

  private static SetMultimap<String, CassandraInstance> buildDataCenterToInstanceMap(
      ImmutableSet<CassandraInstance> instances) {
    ImmutableSetMultimap.Builder<String, CassandraInstance> dataCenterToInstanceMap =
        ImmutableSetMultimap.builder();
    for (CassandraInstance instance : instances) {
      dataCenterToInstanceMap.put(instance.getDataCenter(), instance);
    }
    return dataCenterToInstanceMap.build();
  }

}
