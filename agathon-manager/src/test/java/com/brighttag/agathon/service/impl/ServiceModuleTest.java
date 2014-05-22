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

import org.easymock.EasyMockSupport;
import org.junit.Test;

import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.dao.CassandraRingDao;
import com.brighttag.agathon.service.CassandraInstanceService;
import com.brighttag.agathon.service.CassandraRingService;
import com.brighttag.agathon.service.SeedService;
import com.brighttag.testing.ModuleTester;

/**
 * @author codyaray
 * @since 5/12/12
 */
public class ServiceModuleTest extends EasyMockSupport {

  @Test
  public void bind() throws Exception {
    new ModuleTester(new ServiceModule())
        .dependsOn(CassandraInstanceDao.class, createMock(CassandraInstanceDao.class))
        .dependsOn(CassandraRingDao.class, createMock(CassandraRingDao.class))
        .exposes(CassandraInstanceService.class)
        .exposes(CassandraRingService.class)
        .exposes(SeedService.class)
        .exposesNothingElse()
        .verify();
  }

}
