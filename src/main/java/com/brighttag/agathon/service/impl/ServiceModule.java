package com.brighttag.agathon.service.impl;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import com.brighttag.agathon.annotation.Coprocess;
import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.service.CassandraInstanceService;
import com.brighttag.agathon.service.CoprocessProvider;
import com.brighttag.agathon.service.SeedService;
import com.brighttag.agathon.service.TokenService;

/**
 * Guice module to wire up the services.
 *
 * @author codyaray
 * @since 5/12/12
 */
public class ServiceModule extends AbstractModule {

  public static final String NODES_PER_DATACENTER_PROPERTY = "com.brighttag.agathon.nodes.per_datacenter";
  public static final String SEEDS_PER_DATACENTER_PROPERTY = "com.brighttag.agathon.seeds.per_datacenter";

  @Override
  protected void configure() {
    bind(CassandraInstanceService.class).to(CassandraInstanceServiceImpl.class).in(Singleton.class);
    bind(CoprocessProvider.class).to(SystemPropertyCoprocessProvider.class).in(Singleton.class);
    bind(TokenService.class).to(CompositeTokenService.class).in(Singleton.class);
    bind(SeedService.class).to(PerDataCenterSeedService.class).in(Singleton.class);
  }

  @Provides @Singleton
  Iterable<TokenService> provideTokenServices(AssignedTokenService assignedToken,
      AlternatingNetworkTopologyTokenService newToken) {
    return ImmutableList.of(assignedToken, newToken);
  }

  @Provides @Singleton @Coprocess
  CassandraInstance provideCoprocessCassandraInstance(CoprocessProvider coprocessProvider) {
    return coprocessProvider.getCassandraCoprocess();
  }

  @Provides @Singleton @Named(NODES_PER_DATACENTER_PROPERTY)
  int provideNodesPerDataCenter() {
    return Integer.getInteger(NODES_PER_DATACENTER_PROPERTY, 4);
  }

  @Provides @Singleton @Named(SEEDS_PER_DATACENTER_PROPERTY)
  int provideSeedsPerDataCenter() {
    return Integer.getInteger(SEEDS_PER_DATACENTER_PROPERTY, 2);
  }

}
