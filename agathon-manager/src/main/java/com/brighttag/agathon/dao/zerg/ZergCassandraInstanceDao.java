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
import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.model.CassandraInstance;

/**
 * Zerg implementation of {@link CassandraInstanceDAO}.
 *
 * By convention, a Zerg host is considered to be part of a Cassandra ring if it contains
 * a role named "cassandra_<ringname>".
 *
 * Because Zerg uniquely identifies instances by hostname (which remain stable even if rebuilt),
 * this DAO uses the {@link String#hashCode() hashCode} of the hostname as the Cassandra instance ID.
 *
 * Finally, the Zerg DAO assumes you're running Cassandra on EC2 with the {@link Ec2MultiRegionSnitch}.
 * Therefore it translates the EC2 region ("us-east-1") and availability zone ("a") into the Cassandra
 * data center ("us-east") and rack ("1a"), as expected by this particular snitch.
 *
 * @author codyaray
 * @since 5/15/12
 */
public class ZergCassandraInstanceDao implements CassandraInstanceDao {

  private final ZergConnector zergConnector;
  private final String currentRegion;
  private final ImmutableMap<String, String> ringScopes;

  @Inject
  public ZergCassandraInstanceDao(ZergConnector zergConnector,
      @Named(ZergDaoModule.ZERG_REGION_PROPERTY) String currentRegion,
      @Named(ZergDaoModule.ZERG_CASSANDRA_RING_SCOPES) Map<String, String> ringScopes) {
    this.zergConnector = zergConnector;
    this.currentRegion = currentRegion;
    this.ringScopes = ImmutableMap.copyOf(ringScopes);
  }

  @Override
  public ImmutableSet<CassandraInstance> findAll(String ring) throws BackingStoreException {
    return getHosts(ring).toCassandraInstances();
  }

  @Override
  public @Nullable CassandraInstance findById(String ring, int id) throws BackingStoreException {
    for (ZergHost host : getHosts(ring).toSet()) {
      if (id == host.getId()) {
        return ZergHosts.toCassandraInstance(host);
      }
    }
    return null;
  }

  @Override
  public void save(String ring, CassandraInstance instance) {
    throw new UnsupportedOperationException("Save is not supported for " + getClass().getSimpleName());
  }

  @Override
  public void delete(String ring, CassandraInstance instance) {
    throw new UnsupportedOperationException("Delete is not supported for " + getClass().getSimpleName());
  }

  private ZergHosts getHosts(String ring) throws BackingStoreException {
    return ZergHosts.from(zergConnector.getHosts()).filterScope(ringScopes, currentRegion, ring);
  }

}
