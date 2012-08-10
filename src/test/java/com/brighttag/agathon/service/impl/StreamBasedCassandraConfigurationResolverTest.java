package com.brighttag.agathon.service.impl;

import java.io.IOException;
import java.io.InputStream;

import com.google.inject.util.Providers;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.model.config.CassandraConfiguration;
import com.brighttag.agathon.resources.yaml.config.CassandraConfigurationReader;
import com.brighttag.agathon.service.CassandraConfigurationResolver;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 8/6/12
 */
public class StreamBasedCassandraConfigurationResolverTest extends EasyMockSupport {

  private CassandraConfigurationResolver resolver;
  private CassandraConfigurationReader reader;
  private InputStream inputStream;

  @Before
  public void setUp() {
    reader = createMock(CassandraConfigurationReader.class);
    inputStream = createMock(InputStream.class);
    resolver = new StreamBasedCassandraConfigurationResolver(reader, Providers.of(inputStream));
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void getConfiguration() throws Exception {
    CassandraConfiguration expected = createMock(CassandraConfiguration.class);
    expect(reader.readFrom(inputStream)).andReturn(expected);
    inputStream.close();
    replayAll();
    assertEquals(expected, resolver.getConfiguration(CassandraConfiguration.DEFAULT));
  }

  @Test
  public void getConfiguration_ioException() throws Exception {
    expect(reader.readFrom(inputStream)).andThrow(new IOException());
    inputStream.close();
    replayAll();
    assertEquals(CassandraConfiguration.DEFAULT,
        resolver.getConfiguration(CassandraConfiguration.DEFAULT));
  }

}
