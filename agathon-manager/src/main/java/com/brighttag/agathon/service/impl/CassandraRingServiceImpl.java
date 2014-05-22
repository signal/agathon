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

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

import com.brighttag.agathon.dao.BackingStoreException;
import com.brighttag.agathon.dao.CassandraRingDao;
import com.brighttag.agathon.model.CassandraRing;
import com.brighttag.agathon.service.CassandraRingService;
import com.brighttag.agathon.service.ServiceUnavailableException;

/**
 * DAO-based proxy implementation of {@link CassandraRingService}.
 *
 * @author codyaray
 * @since 9/17/2013
 */
public class CassandraRingServiceImpl implements CassandraRingService {

  private final CassandraRingDao dao;

  @Inject
  public CassandraRingServiceImpl(CassandraRingDao dao) {
    this.dao = dao;
  }

  @Override
  public ImmutableSet<CassandraRing> findAll() {
    try {
      return dao.findAll();
    } catch (BackingStoreException e) {
      throw new ServiceUnavailableException(e);
    }
  }

  @Override
  public CassandraRing findByName(String name) {
    try {
      return dao.findByName(name);
    } catch (BackingStoreException e) {
      throw new ServiceUnavailableException(e);
    }
  }

  @Override
  public void save(CassandraRing ring) {
    dao.save(ring);
  }

  @Override
  public void delete(CassandraRing ring) {
    dao.delete(ring);
  }

}
