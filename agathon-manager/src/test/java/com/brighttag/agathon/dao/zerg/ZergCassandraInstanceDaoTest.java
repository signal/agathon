package com.brighttag.agathon.dao.zerg;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

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

  private static final String RING_NAME = "myring";

  private ZergConnector zergConnector;
  private ZergCassandraInstanceDao dao;

  @Before
  public void setupMocks() {
    zergConnector = createMock(ZergConnector.class);
    dao = new ZergCassandraInstanceDao(zergConnector);
  }

  @Test
  public void findAll() throws Exception {
    expect(zergConnector.getHosts()).andReturn(HOSTS);
    replayAll();

    assertEquals(INSTANCES, dao.findAll(RING_NAME));
  }

  @Test
  public void findAll_emptyManifest() throws Exception {
    expect(zergConnector.getHosts()).andReturn(ImmutableSet.<ZergHost>of());
    replayAll();

    assertEquals(ImmutableSet.of(), dao.findAll(RING_NAME));
  }

  @Test(expected = BackingStoreException.class)
  public void findAll_backingStoreException() throws Exception {
    expect(zergConnector.getHosts()).andThrow(new BackingStoreException());
    replayAll();

    dao.findAll(RING_NAME);
  }

  @Test
  public void findById() throws Exception {
    expect(zergConnector.getHosts()).andReturn(HOSTS);
    replayAll();

    Iterator<CassandraInstance> iterator = INSTANCES.iterator();
    iterator.next();
    assertEquals(iterator.next(), dao.findById(RING_NAME, 1026494710));
  }

  @Test
  public void findById_notFound() throws Exception {
    expect(zergConnector.getHosts()).andReturn(HOSTS);
    replayAll();

    assertNull(dao.findById(RING_NAME, 99));
  }

  @Test
  public void findById_emptyManifest() throws Exception {
    expect(zergConnector.getHosts()).andReturn(ImmutableSet.<ZergHost>of());
    replayAll();

    assertNull(dao.findById(RING_NAME, 99));
  }

  @Test(expected = BackingStoreException.class)
  public void findById_backingStoreException() throws Exception {
    expect(zergConnector.getHosts()).andThrow(new BackingStoreException());
    replayAll();

    dao.findById(RING_NAME, 99);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void save() {
    dao.save(RING_NAME, INSTANCES.iterator().next());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void delete() {
    dao.delete(RING_NAME, INSTANCES.iterator().next());
  }

  private static final ImmutableSet<ZergHost> HOSTS = ImmutableSet.of(
      new ZergHost("tagserve01ap1", ImmutableList.of("tagserve"), "us-northeast-1a", "54.0.1.1"),
      new ZergHost("cass01we2", ImmutableList.of("cassandra", "cassandra_myring"), "us-west-2a", "54.1.1.1"),
      new ZergHost("stats01ea1", ImmutableList.of("cassandra", "cassandra_stats"), "us-east-1c", "54.2.1.1"),
      new ZergHost("cass01ea1", ImmutableList.of("cassandra", "cassandra_myring"), "us-east-1a", "54.2.1.2"),
      new ZergHost("cass02ea1", ImmutableList.of("cassandra", "cassandra_myring"), "us-east-1b", "54.2.1.3"));

  private static final Set<CassandraInstance> INSTANCES = ImmutableSet.of(
      new CassandraInstance.Builder().id(1026512133).hostName("cass01we2").publicIpAddress("54.1.1.1")
          .dataCenter("us-west").rack("2a").build(),
      new CassandraInstance.Builder().id(1026494710).hostName("cass01ea1").publicIpAddress("54.2.1.2")
          .dataCenter("us-east").rack("1a").build(),
      new CassandraInstance.Builder().id(1026524501).hostName("cass02ea1").publicIpAddress("54.2.1.3")
          .dataCenter("us-east").rack("1b").build());

}
