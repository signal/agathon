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

package com.brighttag.agathon.dao.zerg;

import org.junit.Test;

import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.dao.CassandraRingDao;
import com.brighttag.testing.ModuleTester;

/**
 * @author codyaray
 * @since 9/09/2013
 */
public class ZergDaoModuleTest {

  @Test
  public void bindings() throws Exception {
    System.setProperty(ZergDaoModule.ZERG_REGION_PROPERTY, "us-east-1");
    System.setProperty(ZergDaoModule.ZERG_RING_SCOPES_PROPERTY, "somefile.json");
    new ModuleTester(new ZergDaoModule())
        .exposes(CassandraRingDao.class)
        .exposes(CassandraInstanceDao.class)
        .exposesNothingElse()
        .verify();
    System.clearProperty(ZergDaoModule.ZERG_REGION_PROPERTY);
    System.clearProperty(ZergDaoModule.ZERG_RING_SCOPES_PROPERTY);
  }

}
