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

package com.brighttag.agathon.security;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.google.common.util.concurrent.Service;
import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import com.brighttag.agathon.security.ec2.Ec2SecurityGroupModule;

import org.joda.time.Duration;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Guice module to wire up security group management functionality.
 *
 * @author codyaray
 * @since 8/30/2013
 */
public class SecurityGroupModule extends AbstractModule {

  private static final String SECURITY_GROUP_PREFIX = "com.brighttag.agathon.security.";

  // Configuration options
  public static final String SECURITY_GROUP_MANAGEMENT_ENABLED_PROPERTY =
      SECURITY_GROUP_PREFIX + "group_management_enabled";
  public static final String SECURITY_GROUP_UPDATE_PERIOD_PROPERTY =
      SECURITY_GROUP_PREFIX + "group_update_period_seconds";
  public static final String SECURITY_GROUP_NAME_PREFIX_PROPERTY =
      SECURITY_GROUP_PREFIX + "group_name_prefix";

  public static final String CASSANDRA_GOSSIP_PORT_PROPERTY =
      "com.brighttag.agathon.cassandra.gossip_port";
  public static final String CASSANDRA_SSL_GOSSIP_PORT_PROPERTY =
      "com.brighttag.agathon.cassandra.ssl_gossip_port";
  public static final String CASSANDRA_RING_CONFIG_PROPERTY =
      "com.brighttag.agathon.cassandra.ring_config_file";

  // Internal bindings
  public static final String SECURITY_GROUP_DATACENTERS_PROPERTY =
      SECURITY_GROUP_PREFIX + "group_datacenters";

  public static final String CASSANDRA_RING_SSL_ENABLED =
      "com.brighttag.agathon.cassandra.ssl_enabled";

  private static final Type RING_CONFIG_FILE_TYPE =
      new TypeLiteral<Map<String, Map<String, String>>>() {}.getType();

  @Override
  protected void configure() {
    if (Boolean.getBoolean(SECURITY_GROUP_MANAGEMENT_ENABLED_PROPERTY)) {
      install(new PrivateSecurityGroupModule());
      Multibinder.newSetBinder(binder(), Service.class)
          .addBinding().to(SecurityGroupUpdaterService.class);
    }
  }

  private static class PrivateSecurityGroupModule extends PrivateModule {

    @Override
    protected void configure() {
      bind(Gson.class).in(Singleton.class);
      bind(Duration.class).annotatedWith(Names.named(SECURITY_GROUP_UPDATE_PERIOD_PROPERTY)).toInstance(
          Duration.standardSeconds(Integer.getInteger(SECURITY_GROUP_UPDATE_PERIOD_PROPERTY, 10)));
      bindConstant().annotatedWith(Names.named(SECURITY_GROUP_NAME_PREFIX_PROPERTY)).to(checkNotNull(
          System.getProperty(SECURITY_GROUP_NAME_PREFIX_PROPERTY), "Security group name prefix must be set"));
      bindConstant().annotatedWith(Names.named(CASSANDRA_RING_CONFIG_PROPERTY))
          .to(checkNotNull(System.getProperty(CASSANDRA_RING_CONFIG_PROPERTY), "Cassandra ring config file not specified"));
      bindConstant().annotatedWith(Names.named(CASSANDRA_GOSSIP_PORT_PROPERTY)).to(
          Integer.getInteger(CASSANDRA_GOSSIP_PORT_PROPERTY, 7000));
      bindConstant().annotatedWith(Names.named(CASSANDRA_SSL_GOSSIP_PORT_PROPERTY)).to(
          Integer.getInteger(CASSANDRA_GOSSIP_PORT_PROPERTY, 7001));
      install(new Ec2SecurityGroupModule());
      bind(SecurityGroupUpdaterService.class).in(Singleton.class);
      expose(SecurityGroupUpdaterService.class);
    }

    @Provides @Singleton @Named(CASSANDRA_RING_CONFIG_PROPERTY)
    Map<String, Map<String, String>> provideCassandraRingConfigMap(
        @Named(CASSANDRA_RING_CONFIG_PROPERTY) String filename, Gson gson) throws IOException {
      String json = Files.toString(new File(filename), Charsets.UTF_8);
      return gson.fromJson(json, RING_CONFIG_FILE_TYPE);
    }

    @Provides @Singleton @Named(CASSANDRA_RING_SSL_ENABLED)
    Set<String> provideCassandraRingSslEnabled(
        @Named(CASSANDRA_RING_CONFIG_PROPERTY) final Map<String, Map<String, String>> ringConfig) {
      return FluentIterable.from(ringConfig.keySet())
          .filter(new Predicate<String>() {
            @Override
            public boolean apply(String ring) {
              return Boolean.parseBoolean(ringConfig.get(ring).get("ssl_enabled"));
            }
          })
          .toSet();
    }

  }

}
