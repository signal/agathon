package com.brighttag.agathon.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brighttag.agathon.annotation.Coprocess;
import com.brighttag.agathon.dao.CassandraInstanceDAO;
import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.service.SeedService;

/**
 * A {@link SeedService} that includes a specified number of seeds per data center.
 * The current host will never be included as a seed.
 *
 * @author codyaray
 * @since 5/25/12
 */
public class PerDataCenterSeedService implements SeedService {

  private static final Logger LOG = LoggerFactory.getLogger(PerDataCenterSeedService.class);

  private static final Function<CassandraInstance, String> INSTANCE_TO_HOSTNAME =
    new Function<CassandraInstance, String>() {
      @Override
      public @Nullable String apply(@Nullable CassandraInstance input) {
        return (input != null) ? input.getHostName() : null;
      }
    };

  private final Predicate<CassandraInstance> differentHost = new Predicate<CassandraInstance>() {
    @Override
    public boolean apply(CassandraInstance instance) {
      return !instance.getHostName().equals(coprocess.getHostName());
    }
  };

  private final CassandraInstanceDAO dao;
  private final CassandraInstance coprocess;
  private final int numSeeds;

  @Inject
  public PerDataCenterSeedService(CassandraInstanceDAO dao, @Coprocess CassandraInstance coprocess,
      @Named(ServiceModule.SEEDS_PER_DATACENTER_PROPERTY) int numSeeds) {
    this.dao = dao;
    this.coprocess = coprocess;
    this.numSeeds = numSeeds;
  }

  @Override
  public Set<String> getSeeds() {
    ImmutableSet.Builder<String> seedBuilder = ImmutableSet.builder();
    Multimap<String, CassandraInstance> dataCenterToInstanceMap =
        buildDataCenterToInstanceMap(dao.findAll());

    for (String dc : dataCenterToInstanceMap.keySet()) {
      seedBuilder.addAll(getSeeds(dc, dataCenterToInstanceMap.get(dc)));
    }

    return seedBuilder.build();
  }

  /**
   * Return the first {@code numSeeds} seeds from the {@code instancesInDC} collection
   * for the given {@code dataCenter}. The coprocess instance is first filtered out.
   *
   * @param dataCenter the data center name
   * @param instancesInDC a collection of instances in the data center
   * @return the hostnames of the first {@code numSeeds} hosts
   */
  private Iterable<String> getSeeds(String dataCenter, Collection<CassandraInstance> instancesInDC) {
    Collection<CassandraInstance> instances = Collections2.filter(instancesInDC, differentHost);

    int size = instances.size(); // walk the iterator only once
    if (size < numSeeds) {
      LOG.warn("Too few seeds for data center '{}'. Continuing with {} seeds.", dataCenter, size);
    }

    return Iterables.transform(Iterables.limit(instances, numSeeds), INSTANCE_TO_HOSTNAME);
  }

  private static Multimap<String, CassandraInstance> buildDataCenterToInstanceMap(
      List<CassandraInstance> instances) {
    Multimap<String, CassandraInstance> dataCenterToInstanceMap = ArrayListMultimap.create();
    for (CassandraInstance instance : instances) {
      dataCenterToInstanceMap.put(instance.getDataCenter(), instance);
    }
    return dataCenterToInstanceMap;
  }

}

