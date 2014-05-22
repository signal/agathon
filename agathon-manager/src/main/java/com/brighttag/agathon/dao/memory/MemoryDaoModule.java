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

package com.brighttag.agathon.dao.memory;

import com.google.inject.PrivateModule;
import com.google.inject.Singleton;

import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.dao.CassandraRingDao;

/**
 * Guice module to wire up fake (in-memory) DAOs.
 *
 * @author codyaray
 * @since 5/12/12
 */
public class MemoryDaoModule extends PrivateModule {

  @Override
  protected void configure() {
    bind(MemoryCassandraRingDao.class).in(Singleton.class);
    bind(MemoryCassandraInstanceDao.class).in(Singleton.class);
    bind(CassandraInstanceDao.class).to(MemoryCassandraInstanceDao.class);
    bind(CassandraRingDao.class).to(MemoryCassandraRingDao.class);
    expose(CassandraInstanceDao.class);
    expose(CassandraRingDao.class);
  }

}
