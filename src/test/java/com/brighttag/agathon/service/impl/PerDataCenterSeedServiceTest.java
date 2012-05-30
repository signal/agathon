package com.brighttag.agathon.service.impl;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.dao.CassandraInstanceDAO;
import com.brighttag.agathon.model.CassandraInstance;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 5/25/12
 */
public class PerDataCenterSeedServiceTest extends EasyMockSupport {

  private static final String COPROCESS_HOSTNAME = "coprocess";

  private static final String DATACENTER1 = "dc1";
  private static final String DATACENTER2 = "dc2";

  private static final String HOSTNAME1 = "host1";
  private static final String HOSTNAME2 = "host2";
  private static final String HOSTNAME3 = "host3";
  private static final String HOSTNAME4 = "host4";
  private static final String HOSTNAME5 = "host5";

  private CassandraInstanceDAO dao;
  private CassandraInstance identity;
  private PerDataCenterSeedService seedProvider;

  @Before
  public void setUp() {
    dao = createMock(CassandraInstanceDAO.class);
    identity = createMock(CassandraInstance.class);
    seedProvider = new PerDataCenterSeedService(dao, identity, 2);

    expect(identity.getHostName()).andStubReturn(COPROCESS_HOSTNAME);
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void getSeeds() {
    CassandraInstance instance1 = buildInstance(DATACENTER1, HOSTNAME1);
    CassandraInstance instance2 = buildInstance(DATACENTER1, HOSTNAME2);
    CassandraInstance instance3 = buildInstance(DATACENTER2, HOSTNAME3);
    CassandraInstance instance4 = buildInstance(DATACENTER2, HOSTNAME4);
    List<CassandraInstance> instances = ImmutableList.of(instance1, instance2, instance3, instance4);
    expect(dao.findAll()).andReturn(instances);
    replayAll();

    assertEquals(ImmutableSet.of(HOSTNAME1, HOSTNAME2, HOSTNAME3, HOSTNAME4), seedProvider.getSeeds());
  }

  @Test
  public void getSeeds_identityFirstInDataCenter() {
    CassandraInstance instance1 = buildInstance(DATACENTER1, COPROCESS_HOSTNAME);
    CassandraInstance instance2 = buildInstance(DATACENTER1, HOSTNAME2);
    CassandraInstance instance3 = buildInstance(DATACENTER1, HOSTNAME3);
    CassandraInstance instance4 = buildInstance(DATACENTER2, HOSTNAME4);
    CassandraInstance instance5 = buildInstance(DATACENTER2, HOSTNAME5);
    List<CassandraInstance> instances = ImmutableList.of(
        instance1, instance2, instance3, instance4, instance5);
    expect(dao.findAll()).andReturn(instances);
    replayAll();

    assertEquals(ImmutableSet.of(HOSTNAME2, HOSTNAME3, HOSTNAME4, HOSTNAME5), seedProvider.getSeeds());
  }

  @Test
  public void getSeeds_identityMiddleInDataCenter() {
    CassandraInstance instance1 = buildInstance(DATACENTER1, HOSTNAME1);
    CassandraInstance instance2 = buildInstance(DATACENTER1, COPROCESS_HOSTNAME);
    CassandraInstance instance3 = buildInstance(DATACENTER1, HOSTNAME3);
    CassandraInstance instance4 = buildInstance(DATACENTER2, HOSTNAME4);
    CassandraInstance instance5 = buildInstance(DATACENTER2, HOSTNAME5);
    List<CassandraInstance> instances = ImmutableList.of(
            instance1, instance2, instance3, instance4, instance5);
    expect(dao.findAll()).andReturn(instances);
    replayAll();

    assertEquals(ImmutableSet.of(HOSTNAME1, HOSTNAME3, HOSTNAME4, HOSTNAME5), seedProvider.getSeeds());
  }

  @Test
  public void getSeeds_identityLastInDataCenter() {
    CassandraInstance instance1 = buildInstance(DATACENTER1, HOSTNAME1);
    CassandraInstance instance2 = buildInstance(DATACENTER1, HOSTNAME2);
    CassandraInstance instance3 = buildInstance(DATACENTER1, COPROCESS_HOSTNAME);
    CassandraInstance instance4 = buildInstance(DATACENTER2, HOSTNAME4);
    CassandraInstance instance5 = buildInstance(DATACENTER2, HOSTNAME5);
    List<CassandraInstance> instances = ImmutableList.of(
            instance1, instance2, instance3, instance4, instance5);
    expect(dao.findAll()).andReturn(instances);
    replayAll();

    assertEquals(ImmutableSet.of(HOSTNAME1, HOSTNAME2, HOSTNAME4, HOSTNAME5), seedProvider.getSeeds());
  }

  @Test
  public void getSeeds_insufficientInstancesInDataCenter() {
    CassandraInstance instance1 = buildInstance(DATACENTER1, HOSTNAME1);
    CassandraInstance instance2 = buildInstance(DATACENTER2, HOSTNAME2);
    CassandraInstance instance3 = buildInstance(DATACENTER2, HOSTNAME3);
    List<CassandraInstance> instances = ImmutableList.of(instance1, instance2, instance3);
    expect(dao.findAll()).andReturn(instances);
    replayAll();

    assertEquals(ImmutableSet.of(HOSTNAME1, HOSTNAME2, HOSTNAME3), seedProvider.getSeeds());
  }

  @Test
  public void getSeeds_identityOnlyInDataCenter() {
    CassandraInstance instance1 = buildInstance(DATACENTER1, COPROCESS_HOSTNAME);
    CassandraInstance instance2 = buildInstance(DATACENTER2, HOSTNAME2);
    CassandraInstance instance3 = buildInstance(DATACENTER2, HOSTNAME3);
    List<CassandraInstance> instances = ImmutableList.of(instance1, instance2, instance3);
    expect(dao.findAll()).andReturn(instances);
    replayAll();

    assertEquals(ImmutableSet.of(HOSTNAME2, HOSTNAME3), seedProvider.getSeeds());
  }

  @Test
  public void getSeeds_identityOnly() {
    CassandraInstance instance1 = buildInstance(DATACENTER1, COPROCESS_HOSTNAME);
    List<CassandraInstance> instances = ImmutableList.of(instance1);
    expect(dao.findAll()).andReturn(instances);
    replayAll();

    assertEquals(ImmutableSet.of(), seedProvider.getSeeds());
  }

  private CassandraInstance buildInstance(String dataCenter, String hostName) {
    return new CassandraInstance.Builder()
        .dataCenter(dataCenter)
        .hostName(hostName)
        .build();
  }

}
