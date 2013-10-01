package com.brighttag.agathon.security.ec2;

import com.google.common.base.Function;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import org.easymock.EasyMockSupport;
import org.junit.Test;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.security.SecurityGroupModule;
import com.brighttag.agathon.security.SecurityGroupService;
import com.brighttag.agathon.service.CassandraInstanceService;
import com.brighttag.testing.ModuleTester;

/**
 * @author codyaray
 * @since 9/02/2013
 */
public class Ec2SecurityGroupModuleTest extends EasyMockSupport {

  @Test
  public void bindings() throws Exception {
    new ModuleTester(new Ec2SecurityGroupModule())
        .dependsOn(CassandraInstanceService.class, createMock(CassandraInstanceService.class))
        .exposes(SecurityGroupService.class)
        .exposes(Key.get(new TypeLiteral<Function<CassandraInstance, String>>() { },
            Names.named(SecurityGroupModule.SECURITY_GROUP_DATACENTERS_PROPERTY)))
        .exposesNothingElse()
        .verify();
  }

}
