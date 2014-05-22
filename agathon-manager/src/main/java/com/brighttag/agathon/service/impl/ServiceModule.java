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

package com.brighttag.agathon.service.impl;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import com.brighttag.agathon.service.CassandraInstanceService;
import com.brighttag.agathon.service.CassandraRingService;
import com.brighttag.agathon.service.SeedService;

/**
 * Guice module to wire up the services.
 *
 * @author codyaray
 * @since 5/12/12
 */
public class ServiceModule extends PrivateModule {

  public static final String SEEDS_PER_DATACENTER_PROPERTY = "com.brighttag.agathon.seeds.per_datacenter";

  @Override
  protected void configure() {
    bind(CassandraRingServiceImpl.class).in(Singleton.class);
    bind(CassandraInstanceServiceImpl.class).in(Singleton.class);
    bind(CassandraRingService.class).to(CassandraRingServiceImpl.class);
    bind(CassandraInstanceService.class).to(CassandraInstanceServiceImpl.class);
    bind(SeedService.class).to(PerDataCenterSeedService.class).in(Singleton.class);
    expose(CassandraRingService.class);
    expose(CassandraInstanceService.class);
    expose(SeedService.class);
  }

  @Provides @Singleton @Named(SEEDS_PER_DATACENTER_PROPERTY)
  int provideSeedsPerDataCenter() {
    return Integer.getInteger(SEEDS_PER_DATACENTER_PROPERTY, 2);
  }

}
