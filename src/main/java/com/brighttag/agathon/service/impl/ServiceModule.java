package com.brighttag.agathon.service.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import com.brighttag.agathon.service.CassandraInstanceService;
import com.brighttag.agathon.service.SeedService;

/**
 * Guice module to wire up the services.
 *
 * @author codyaray
 * @since 5/12/12
 */
public class ServiceModule extends AbstractModule {

  public static final String SEEDS_PER_DATACENTER_PROPERTY = "com.brighttag.agathon.seeds.per_datacenter";

  @Override
  protected void configure() {
    bind(CassandraInstanceService.class).to(CassandraInstanceServiceImpl.class).in(Singleton.class);
    bind(SeedService.class).to(PerDataCenterSeedService.class).in(Singleton.class);
  }

  @Provides @Singleton @Named(SEEDS_PER_DATACENTER_PROPERTY)
  int provideSeedsPerDataCenter() {
    return Integer.getInteger(SEEDS_PER_DATACENTER_PROPERTY, 2);
  }

}
