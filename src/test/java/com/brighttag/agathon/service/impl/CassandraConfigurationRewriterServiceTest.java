package com.brighttag.agathon.service.impl;

import java.io.IOException;
import java.io.OutputStream;

import com.google.inject.util.Providers;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.model.config.CassandraConfiguration;
import com.brighttag.agathon.resources.yaml.config.CassandraConfigurationWriter;

import static org.easymock.EasyMock.expectLastCall;

/**
 * @author codyaray
 * @since 8/2/12
 */
public class CassandraConfigurationRewriterServiceTest extends EasyMockSupport {

  private CassandraConfiguration configuration;
  private CassandraConfigurationWriter writer;
  private OutputStream outputStream;
  private CassandraConfigurationRewriterService service;

  @Before
  public void setUp() {
    configuration = createMock(CassandraConfiguration.class);
    writer = createMock(CassandraConfigurationWriter.class);
    outputStream = createMock(OutputStream.class);
    service = new CassandraConfigurationRewriterService(configuration, writer, Providers.of(outputStream));
  }

  @After
  public void tearDown() {
    verifyAll();
  }

  @Test
  public void startUp() throws Exception {
    writer.writeTo(configuration, outputStream);
    outputStream.close();
    replayAll();
    service.startUp();
  }

  @Test(expected = IOException.class)
  public void startUp_exception() throws Exception {
    writer.writeTo(configuration, outputStream);
    expectLastCall().andThrow(new IOException());
    outputStream.close();
    replayAll();
    service.startUp();
  }

}
