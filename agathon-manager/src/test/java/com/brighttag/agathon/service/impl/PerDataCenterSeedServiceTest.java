package com.brighttag.agathon.service.impl;

import com.google.common.collect.ImmutableSet;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.CassandraRing;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 5/25/12
 */
public class PerDataCenterSeedServiceTest extends EasyMockSupport {

  private static final String DATACENTER1 = "dc1";
  private static final String DATACENTER2 = "dc2";

  private static final String HOSTNAME1 = "host1";
  private static final String HOSTNAME2 = "host2";
  private static final String HOSTNAME3 = "host3";
  private static final String HOSTNAME4 = "host4";

  private CassandraRing ring;
  private PerDataCenterSeedService seedProvider;

  @Before
  public void setUp() {
    ring = createMock(CassandraRing.class);
    seedProvider = new PerDataCenterSeedService(2);
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
    ImmutableSet<CassandraInstance> instances = ImmutableSet.of(instance1, instance2, instance3, instance4);
    expect(ring.getInstances()).andReturn(instances);
    replayAll();

    assertEquals(ImmutableSet.of(HOSTNAME1, HOSTNAME2, HOSTNAME3, HOSTNAME4), seedProvider.getSeeds(ring));
  }

  @Test
  public void getSeeds_insufficientInstancesInDataCenter() {
    CassandraInstance instance1 = buildInstance(DATACENTER1, HOSTNAME1);
    CassandraInstance instance2 = buildInstance(DATACENTER2, HOSTNAME2);
    CassandraInstance instance3 = buildInstance(DATACENTER2, HOSTNAME3);
    ImmutableSet<CassandraInstance> instances = ImmutableSet.of(instance1, instance2, instance3);
    expect(ring.getInstances()).andReturn(instances);
    replayAll();

    assertEquals(ImmutableSet.of(HOSTNAME1, HOSTNAME2, HOSTNAME3), seedProvider.getSeeds(ring));
  }

  private CassandraInstance buildInstance(String dataCenter, String hostName) {
    return new CassandraInstance.Builder()
        .id(1)
        .dataCenter(dataCenter)
        .hostName(hostName)
        .build();
  }

}
