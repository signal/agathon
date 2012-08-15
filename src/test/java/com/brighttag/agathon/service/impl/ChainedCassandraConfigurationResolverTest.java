package com.brighttag.agathon.service.impl;

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.config.CassandraConfiguration;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertSame;

/**
 * @author codyaray
 * @since 8/6/12
 */
public class ChainedCassandraConfigurationResolverTest extends EasyMockSupport {

  private CassandraConfiguration chainedConfiguration;
  private CassandraInstance instance;

  @Before
  public void setUp() {
    chainedConfiguration = createMock(CassandraConfiguration.class);
    instance = createMock(CassandraInstance.class);
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void getConfiguration_empty() {
    CassandraConfigurationResolver resolver = resolver(ImmutableList.<CassandraConfigurationResolver>of());
    replayAll();
    assertSame(chainedConfiguration, resolver.getConfiguration(instance, chainedConfiguration));
  }

  @Test
  public void getConfiguration_multiple() {
    CassandraConfigurationResolver resolver1 = createMock(CassandraConfigurationResolver.class);
    CassandraConfigurationResolver resolver2 = createMock(CassandraConfigurationResolver.class);
    CassandraConfiguration config1 = createMock(CassandraConfiguration.class);
    CassandraConfiguration config2 = createMock(CassandraConfiguration.class);
    expect(resolver1.getConfiguration(instance, chainedConfiguration)).andReturn(config1);
    expect(resolver2.getConfiguration(instance, config1)).andReturn(config2);
    CassandraConfigurationResolver resolver = resolver(ImmutableList.of(resolver1, resolver2));
    replayAll();
    assertSame(config2, resolver.getConfiguration(instance, chainedConfiguration));
    verifyAll();
  }

  private static CassandraConfigurationResolver resolver(List<CassandraConfigurationResolver> resolvers) {
    return new ChainedCassandraConfigurationResolver(resolvers);
  }

}
