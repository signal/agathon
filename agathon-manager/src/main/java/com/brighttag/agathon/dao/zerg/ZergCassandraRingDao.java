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

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import com.brighttag.agathon.dao.BackingStoreException;
import com.brighttag.agathon.dao.CassandraRingDao;
import com.brighttag.agathon.model.CassandraRing;

/**
 *
 * @author codyaray
 * @since 9/17/2013
 */
public class ZergCassandraRingDao implements CassandraRingDao {

  private final ZergConnector zergConnector;
  private final String currentRegion;
  private final Map<String, String> ringScopes;

  @Inject
  public ZergCassandraRingDao(ZergConnector zergConnector,
      @Named(ZergDaoModule.ZERG_REGION_PROPERTY) String currentRegion,
      @Named(ZergDaoModule.ZERG_RING_SCOPES_PROPERTY) Map<String, String> ringScopes) {
    this.zergConnector = zergConnector;
    this.currentRegion = currentRegion;
    this.ringScopes = ImmutableMap.copyOf(ringScopes);
  }

  @Override
  public ImmutableSet<CassandraRing> findAll() throws BackingStoreException {
    ImmutableSet.Builder<CassandraRing> ringBuilder = ImmutableSet.builder();
    ZergHosts hosts = ZergHosts.from(zergConnector.getHosts());
    for (String ring : hosts.rings()) {
      ringBuilder.add(buildRingFromHosts(ring, hosts));
    }
    return ringBuilder.build();
  }

  @Override
  public @Nullable CassandraRing findByName(String name) throws BackingStoreException {
    ZergHosts hosts = ZergHosts.from(zergConnector.getHosts());
    if (!hosts.rings().contains(name)) {
      return null;
    }
    return buildRingFromHosts(name, hosts);
  }

  @Override
  public void save(CassandraRing ring) {
    throw new UnsupportedOperationException("Save is not supported for " + getClass().getSimpleName());
  }

  @Override
  public void delete(CassandraRing ring) {
    throw new UnsupportedOperationException("Delete is not supported for " + getClass().getSimpleName());
  }

  private CassandraRing buildRingFromHosts(String ring, ZergHosts hosts) {
    return new CassandraRing.Builder()
        .name(ring)
        .instances(hosts.filterScope(ringScopes, currentRegion, ring).toCassandraInstances())
        .build();
  }

}
