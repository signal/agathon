package com.brighttag.agathon.security;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Service;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Test;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.service.CassandraRingService;
import com.brighttag.testing.ModuleTester;

/**
 * @author codyaray
 * @since 9/04/2013
 */
public class SecurityGroupModuleTest extends EasyMockSupport {

  @After
  public void tearDown() {
    System.clearProperty(SecurityGroupModule.SECURITY_GROUP_MANAGEMENT_ENABLED_PROPERTY);
  }

  @Test
  public void bindings_enabled() throws Exception {
    System.setProperty(SecurityGroupModule.SECURITY_GROUP_MANAGEMENT_ENABLED_PROPERTY, "true");
    new ModuleTester(new SecurityGroupModule())
        .dependsOn(CassandraRingService.class, createMock(CassandraRingService.class))
        .exposes(Key.get(Integer.class,
            Names.named(SecurityGroupModule.SECURITY_GROUP_UPDATE_PERIOD_PROPERTY)))
        .exposes(Key.get(Integer.class,
            Names.named(SecurityGroupModule.CASSANDRA_GOSSIP_PORT_PROPERTY)))
        .exposes(Key.get(new TypeLiteral<Function<CassandraInstance, String>>() { },
            Names.named(SecurityGroupModule.SECURITY_GROUP_DATACENTERS_PROPERTY)))
        .exposes(SecurityGroupService.class)
        .exposes(SecurityGroupUpdaterService.class)
        .exposesMultibinding(Service.class)
        .exposesNothingElse()
        .verify();
  }

  @Test
  public void bindings_disabled() throws Exception {
    System.setProperty(SecurityGroupModule.SECURITY_GROUP_MANAGEMENT_ENABLED_PROPERTY, "false");
    new ModuleTester(new SecurityGroupModule())
        .exposesNothingElse()
        .verify();
  }

}
