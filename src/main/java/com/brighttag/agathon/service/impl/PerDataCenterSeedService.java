package com.brighttag.agathon.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.service.SeedService;

/**
 * A {@link SeedService} that includes a specified number of seeds per data center.
 * The first {@code numSeeds} instances in each data center are returned as seeds.
 *
 * @author codyaray
 * @since 5/25/12
 */
public class PerDataCenterSeedService implements SeedService {

  private static final Logger LOG = LoggerFactory.getLogger(PerDataCenterSeedService.class);

  private static final Function<CassandraInstance, String> INSTANCE_TO_HOSTNAME =
    new Function<CassandraInstance, String>() {
      @Override
      public @Nullable String apply(@Nullable CassandraInstance instance) {
        return (instance != null) ? instance.getHostName() : null;
      }
    };

  private final CassandraInstanceDao dao;
  private final int numSeeds;

  @Inject
  public PerDataCenterSeedService(CassandraInstanceDao dao,
      @Named(ServiceModule.SEEDS_PER_DATACENTER_PROPERTY) int numSeeds) {
    this.dao = dao;
    this.numSeeds = numSeeds;
  }

  @Override
  public Set<String> getSeeds() {
    ImmutableSet.Builder<String> seedBuilder = ImmutableSet.builder();
    SortedSetMultimap<String, CassandraInstance> dataCenterToInstanceMap =
        buildDataCenterToInstanceMap(dao.findAll());

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
   * @return the hostnames of the first {@code numSeeds} hosts
   */
  private Iterable<String> getSeeds(String dataCenter, Collection<CassandraInstance> instancesInDC) {
    int size = instancesInDC.size();
    if (size < numSeeds) {
      LOG.warn("Too few seeds for data center '{}'. Continuing with {} seeds.", dataCenter, size);
    }

    return Iterables.transform(Iterables.limit(instancesInDC, numSeeds), INSTANCE_TO_HOSTNAME);
  }

  private static SortedSetMultimap<String, CassandraInstance> buildDataCenterToInstanceMap(
      List<CassandraInstance> instances) {
    SortedSetMultimap<String, CassandraInstance> dataCenterToInstanceMap = TreeMultimap.create();
    for (CassandraInstance instance : instances) {
      dataCenterToInstanceMap.put(instance.getDataCenter(), instance);
    }
    return dataCenterToInstanceMap;
  }

}
