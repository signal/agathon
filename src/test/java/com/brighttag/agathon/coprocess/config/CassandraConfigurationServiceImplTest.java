package com.brighttag.agathon.coprocess.config;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.config.CassandraConfiguration;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertSame;

/**
 * @author codyaray
 * @since 9/09/2013
 */
public class CassandraConfigurationServiceImplTest extends EasyMockSupport {

  private CassandraConfigurationResolver resolver;
  private CassandraInstance instance;
  private CassandraConfiguration configuration;

  @Before
  public void setupMocks() {
    resolver = createMock(CassandraConfigurationResolver.class);
    instance = createMock(CassandraInstance.class);
    configuration = createMock(CassandraConfiguration.class);
  }

  @Test
  public void equals() {
    expect(resolver.getConfiguration(instance, CassandraConfiguration.DEFAULT))
        .andReturn(configuration);
    replayAll();
    CassandraConfigurationService service = new CassandraConfigurationServiceImpl(resolver);
    assertSame(configuration, service.getConfiguration(instance));
  }

}
