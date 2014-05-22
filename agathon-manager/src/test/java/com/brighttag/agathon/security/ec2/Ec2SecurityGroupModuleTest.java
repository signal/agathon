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
