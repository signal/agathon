package com.brighttag.agathon.dao.zerg;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

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

  @Inject
  public ZergCassandraInstanceDao(ZergConnector zergConnector) {
    this.zergConnector = zergConnector;
  }

  @Override
  public ImmutableSet<CassandraInstance> findAll(String ring) {
    return ZergHosts.from(zergConnector.getHosts()).filter(ring).toCassandraInstances();
  }

  @Override
  public @Nullable CassandraInstance findById(String ring, int id) {
    for (ZergHost host : ZergHosts.from(zergConnector.getHosts()).filter(ring).toSet()) {
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

}
