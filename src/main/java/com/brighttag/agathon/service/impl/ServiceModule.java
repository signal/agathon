package com.brighttag.agathon.service.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import com.brighttag.agathon.annotation.Coprocess;
import com.brighttag.agathon.dao.CassandraInstanceDAO;
import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.service.CassandraInstanceService;
import com.brighttag.agathon.service.SeedService;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Guice module to wire up the services.
 * <br/>
 * Use of this module requires the {@code CASSANDRA_ID_PROPERTY} system property to be set
 * and the data store behind {@link CassandraInstanceDAO} to contain an instance with this id.
 *
 * @author codyaray
 * @since 5/12/12
 */
public class ServiceModule extends AbstractModule {

  public static final String CASSANDRA_ID_PROPERTY = "com.brighttag.agathon.cassandra_id";
  public static final String SEEDS_PER_DATACENTER_PROPERTY = "com.brighttag.agathon.seeds.per_datacenter";

  @Override
  protected void configure() {
    bind(CassandraInstanceService.class).to(CassandraInstanceServiceImpl.class).in(Singleton.class);
    bind(SeedService.class).to(PerDataCenterSeedService.class).in(Singleton.class);
  }

  @Provides @Singleton @Coprocess
  CassandraInstance provideIdentityCassandraInstance(CassandraInstanceDAO dao,
      @Named(CASSANDRA_ID_PROPERTY) String id) {
    return checkNotNull(dao.findById(id), "Coprocess instance must be in database");
  }

  @Provides @Singleton @Named(CASSANDRA_ID_PROPERTY)
  String provideCassandraId() {
    return checkNotNull(System.getProperty(CASSANDRA_ID_PROPERTY), "Cassandra ID must be set");
  }

  @Provides @Singleton @Named(SEEDS_PER_DATACENTER_PROPERTY)
  int provideSeedsPerDataCenter() {
    return Integer.getInteger(SEEDS_PER_DATACENTER_PROPERTY, 2);
  }

}
