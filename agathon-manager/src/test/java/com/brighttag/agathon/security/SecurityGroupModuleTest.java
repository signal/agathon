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

import com.google.common.base.Function;
import com.google.common.util.concurrent.Service;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import org.easymock.EasyMockSupport;
import org.joda.time.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.service.CassandraRingService;
import com.brighttag.testing.ModuleTester;

/**
 * @author codyaray
 * @since 9/04/2013
 */
public class SecurityGroupModuleTest extends EasyMockSupport {

  @Before
  public void setUp() {
    System.setProperty(SecurityGroupModule.SECURITY_GROUP_NAME_PREFIX_PROPERTY, "cassandra_");
  }

  @After
  public void tearDown() {
    System.clearProperty(SecurityGroupModule.SECURITY_GROUP_NAME_PREFIX_PROPERTY);
    System.clearProperty(SecurityGroupModule.SECURITY_GROUP_MANAGEMENT_ENABLED_PROPERTY);
  }

  @Test
  public void bindings_enabled() throws Exception {
    System.setProperty(SecurityGroupModule.SECURITY_GROUP_MANAGEMENT_ENABLED_PROPERTY, "true");
    System.setProperty(SecurityGroupModule.CASSANDRA_RING_CONFIG_PROPERTY, "does.not.matter");
    new ModuleTester(new SecurityGroupModule())
        .dependsOn(CassandraRingService.class, createMock(CassandraRingService.class))
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
