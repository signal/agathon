package com.brighttag.agathon.dao.zerg;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.CassandraRing;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author codyaray
 * @since 9/27/2013
 */
public class ZergCassandraRingDaoTest extends EasyMockSupport {

  private static final String RING_NAME = "myring";

  private ZergConnector zergConnector;
  private ZergCassandraRingDao dao;

  @Before
  public void setupMocks() {
    zergConnector = createMock(ZergConnector.class);
    dao = new ZergCassandraRingDao(zergConnector);
  }

  @Test
  public void findAll() throws Exception {
    expect(zergConnector.getHosts()).andReturn(HOSTS);
    replayAll();

    assertEquals(RINGS, dao.findAll());
  }

  @Test
  public void findAll_badManifest() throws Exception {
    expect(zergConnector.getHosts()).andReturn(ImmutableSet.<ZergHost>of());
    replayAll();

    assertEquals(ImmutableSet.of(), dao.findAll());
  }

  @Test
  public void findById() throws Exception {
    expect(zergConnector.getHosts()).andReturn(HOSTS);
    replayAll();

    Iterator<CassandraRing> iterator = RINGS.iterator();
    assertEquals(iterator.next(), dao.findByName(RING_NAME));
  }

  @Test
  public void findById_notFound() throws Exception {
    expect(zergConnector.getHosts()).andReturn(HOSTS);
    replayAll();

    assertNull(dao.findByName("nothere"));
  }

  @Test
  public void findById_badManifest() throws Exception {
    expect(zergConnector.getHosts()).andReturn(ImmutableSet.<ZergHost>of());
    replayAll();

    assertNull(dao.findByName(RING_NAME));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void save() {
    dao.save(RINGS.iterator().next());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void delete() {
    dao.delete(RINGS.iterator().next());
  }

  private static final ImmutableSet<ZergHost> HOSTS = ImmutableSet.of(
      host("tagserve01ap1", "us-northeast-1a", "54.0.1.1", "tagserve"),
      host("cass01we2", "us-west-2a", "54.1.1.1", "cassandra", "cassandra_myring"),
      host("cass02we2", "us-west-2b", "54.1.1.2", "cassandra", "cassandra_myring"),
      host("cass01ea1", "us-east-1a", "54.2.1.3", "cassandra", "cassandra_myring"),
      host("cass02ea1", "us-east-1b", "54.2.1.4", "cassandra", "cassandra_myring"),
      host("stats01we2", "us-west-2a", "108.2.1.1", "cassandra", "cassandra_stats"),
      host("stats01ea1", "us-east-1a", "108.2.1.2", "cassandra", "cassandra_stats"),
      host("stats02ea1", "us-east-1b", "108.2.1.3", "cassandra", "cassandra_stats"));

  private static ZergHost host(String hostName, String zone, String publicIp, String... roles) {
    return new ZergHost(hostName, ImmutableList.copyOf(roles), zone, publicIp);
  }

  private static CassandraInstance instance(String name, String dataCenter, String rack, String publicIp) {
    return new CassandraInstance.Builder()
        .id(name.hashCode())
        .hostName(name)
        .dataCenter(dataCenter)
        .rack(rack)
        .publicIpAddress(publicIp)
        .build();
  }

  private static final Set<CassandraRing> RINGS = ImmutableSet.of(
      new CassandraRing.Builder()
          .name(RING_NAME)
          .instances(ImmutableSet.of(
              instance("cass01we2", "us-west", "2a", "54.1.1.1"),
              instance("cass02we2", "us-west", "2b", "54.1.1.2"),
              instance("cass01ea1", "us-east", "1a", "54.2.1.3"),
              instance("cass02ea1", "us-east", "1b", "54.2.1.4")))
          .build(),
      new CassandraRing.Builder()
          .name("stats")
          .instances(ImmutableSet.of(
              instance("stats01we2", "us-west", "2a", "108.2.1.1"),
              instance("stats01ea1", "us-east", "1a", "108.2.1.2"),
              instance("stats02ea1", "us-east", "1b", "108.2.1.3")))
          .build());

}
