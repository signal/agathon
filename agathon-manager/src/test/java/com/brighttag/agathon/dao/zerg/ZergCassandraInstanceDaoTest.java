package com.brighttag.agathon.dao.zerg;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.dao.BackingStoreException;
import com.brighttag.agathon.model.CassandraInstance;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author codyaray
 * @since 9/09/2013
 */
public class ZergCassandraInstanceDaoTest extends EasyMockSupport {

  private static final String BIG_RING = "bigring";
  private static final String SMALL_RING = "smallring";
  private static final String MY_REGION = "us-west-2";
  private static final Map<String, String> SCOPE_MAP =
      ImmutableMap.of(BIG_RING, "environment", SMALL_RING, "region");

  private ZergConnector zergConnector;
  private ZergCassandraInstanceDao dao;

  @Before
  public void setupMocks() {
    zergConnector = createMock(ZergConnector.class);
    dao = new ZergCassandraInstanceDao(zergConnector, MY_REGION, SCOPE_MAP);
  }

  @Test
  public void findAll() throws Exception {
    expect(zergConnector.getHosts()).andReturn(HOSTS);
    replayAll();

    assertEquals(BIG_RING_INSTANCES, dao.findAll(BIG_RING));
  }

  @Test
  public void findAll_emptyManifest() throws Exception {
    expect(zergConnector.getHosts()).andReturn(ImmutableSet.<ZergHost>of());
    replayAll();

    assertEquals(ImmutableSet.of(), dao.findAll(BIG_RING));
  }

  @Test(expected = BackingStoreException.class)
  public void findAll_backingStoreException() throws Exception {
    expect(zergConnector.getHosts()).andThrow(new BackingStoreException());
    replayAll();

    dao.findAll(BIG_RING);
  }

  @Test
  public void findAll_regionScopedRing() throws Exception {
    expect(zergConnector.getHosts()).andReturn(HOSTS);
    replayAll();

    assertEquals(SMALL_RING_INSTANCES_IN_WEST, dao.findAll(SMALL_RING));
  }

  @Test
  public void findById() throws Exception {
    expect(zergConnector.getHosts()).andReturn(HOSTS);
    replayAll();

    Iterator<CassandraInstance> iterator = BIG_RING_INSTANCES.iterator();
    iterator.next();
    assertEquals(iterator.next(), dao.findById(BIG_RING, 1026494710));
  }

  @Test
  public void findById_notFound() throws Exception {
    expect(zergConnector.getHosts()).andReturn(HOSTS);
    replayAll();

    assertNull(dao.findById(BIG_RING, 99));
  }

  @Test
  public void findById_emptyManifest() throws Exception {
    expect(zergConnector.getHosts()).andReturn(ImmutableSet.<ZergHost>of());
    replayAll();

    assertNull(dao.findById(BIG_RING, 99));
  }

  @Test(expected = BackingStoreException.class)
  public void findById_backingStoreException() throws Exception {
    expect(zergConnector.getHosts()).andThrow(new BackingStoreException());
    replayAll();

    dao.findById(BIG_RING, 99);
  }

  @Test
  public void findById_regionScopedRing() throws Exception {
    expect(zergConnector.getHosts()).andReturn(HOSTS);
    replayAll();

    CassandraInstance instance = Iterables.getOnlyElement(SMALL_RING_INSTANCES_IN_WEST);
    assertEquals(instance, dao.findById(SMALL_RING, 127844420));
  }

  @Test
  public void findById_regionScopedRing_ringNotInCurrentRegion() throws Exception {
    expect(zergConnector.getHosts()).andReturn(HOSTS);
    replayAll();

    assertNull(dao.findById(SMALL_RING, 127826997));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void save() {
    dao.save(BIG_RING, BIG_RING_INSTANCES.iterator().next());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void delete() {
    dao.delete(BIG_RING, BIG_RING_INSTANCES.iterator().next());
  }

  private static final ImmutableSet<ZergHost> HOSTS = ImmutableSet.of(
      host("tagserve01ap1", "us-northeast-1a", "54.0.1.1", "tagserve"),
      host("cass01we2",  "us-west-2a", "54.1.1.1", "cassandra", "cassandra_bigring"),
      host("stats01we2", "us-west-2a", "54.1.1.2", "cassandra", "cassandra_smallring"),
      host("stats01ea1", "us-east-1c", "54.2.1.1", "cassandra", "cassandra_smallring"),
      host("cass01ea1",  "us-east-1a", "54.2.1.2", "cassandra", "cassandra_bigring"),
      host("cass02ea1",  "us-east-1b", "54.2.1.3", "cassandra", "cassandra_bigring"));

  private static final Set<CassandraInstance> BIG_RING_INSTANCES = ImmutableSet.of(
      instance(1026512133, "cass01we2", "54.1.1.1", "us-west", "2a", "54.1.1.1-bt.com"),
      instance(1026494710, "cass01ea1", "54.2.1.2", "us-east", "1a", "54.2.1.2-bt.com"),
      instance(1026524501, "cass02ea1", "54.2.1.3", "us-east", "1b", "54.2.1.3-bt.com"));

  private static final Set<CassandraInstance> SMALL_RING_INSTANCES_IN_WEST = ImmutableSet.of(
      instance(127844420, "stats01we2", "54.1.1.2", "us-west", "2a", "54.1.1.2-bt.com"));

  private static CassandraInstance instance(int id, String hostName, String ipAddress,
      String dataCenter, String rack, String fqdn) {
    return new CassandraInstance.Builder().id(id).hostName(hostName).publicIpAddress(ipAddress)
            .dataCenter(dataCenter).rack(rack).fullyQualifiedDomainName(fqdn).build();
  }

  private static ZergHost host(String hostName, String zone, String publicIp, String... roles) {
    return new ZergHost(hostName, ImmutableList.copyOf(roles), zone, publicIp, publicIp + "-bt.com");
  }

}
