package com.brighttag.agathon.security;

import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

import com.brighttag.agathon.security.ec2.Ec2SecurityGroupModule;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Guice module to wire up security group management functionality.
 *
 * @author codyaray
 * @since 8/30/2013
 */
public class SecurityGroupModule extends AbstractModule {

  // Configuration options
  public static final String SECURITY_GROUP_MANAGEMENT_ENABLED_PROPERTY =
      "com.brighttag.agathon.security.group_management_enabled";
  public static final String SECURITY_GROUP_UPDATE_PERIOD_PROPERTY =
      "com.brighttag.agathon.security.group_update_period_seconds";
  public static final String SECURITY_GROUP_NAME_PREFIX_PROPERTY =
      "com.brighttag.agathon.security.group_name_prefix";
  public static final String CASSANDRA_GOSSIP_PORT_PROPERTY =
      "com.brighttag.agathon.cassandra.gossip_port";

  // Internal bindings
  public static final String SECURITY_GROUP_DATACENTERS_PROPERTY =
      "com.brighttag.agathon.security.group_datacenters";

  @Override
  protected void configure() {
    if (Boolean.getBoolean(SECURITY_GROUP_MANAGEMENT_ENABLED_PROPERTY)) {
      bindConstant().annotatedWith(Names.named(SECURITY_GROUP_UPDATE_PERIOD_PROPERTY)).to(
          Integer.getInteger(SECURITY_GROUP_UPDATE_PERIOD_PROPERTY, 60));
      bindConstant().annotatedWith(Names.named(SECURITY_GROUP_NAME_PREFIX_PROPERTY)).to(checkNotNull(
          System.getProperty(SECURITY_GROUP_NAME_PREFIX_PROPERTY), "Security group name prefix must be set"));
      bindConstant().annotatedWith(Names.named(CASSANDRA_GOSSIP_PORT_PROPERTY)).to(
          Integer.getInteger(CASSANDRA_GOSSIP_PORT_PROPERTY, 7000));
      install(new Ec2SecurityGroupModule());
      bind(SecurityGroupUpdaterService.class).in(Singleton.class);
      Multibinder.newSetBinder(binder(), Service.class)
          .addBinding().to(SecurityGroupUpdaterService.class);
    }
  }

}
