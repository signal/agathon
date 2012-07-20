package com.brighttag.agathon.resources.yaml.config;

import com.google.common.net.HostAndPort;
import com.proofpoint.units.DataSize;

import org.joda.time.Duration;
import org.junit.Test;

import com.brighttag.agathon.model.config.RpcConfiguration;
import com.brighttag.agathon.model.config.RpcConfiguration.ServerType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author codyaray
 * @since 7/15/12
 */
public class RpcConfigurationReaderTest extends AbstractConfigurationReaderTest {

  @Test
  public void fromYaml_min() throws Exception {
    RpcConfiguration config = createConfig(MIN_CASSANDRA_YAML, new RpcConfigurationReader());
    /*
     * Just confirm that MIN_CASSANDRA_YAML equals the default configuration.
     * The actual default values are tested in the RpcConfigurationTest.
     */
    assertEquals(RpcConfiguration.DEFAULT, config);
  }

  @Test
  public void fromYaml_max() throws Exception {
    RpcConfiguration config = createConfig(MAX_CASSANDRA_YAML, new RpcConfigurationReader());
    assertEquals(HostAndPort.fromParts("localhost", 9160), config.getHostAndPort().get());
    assertTrue(config.getKeepalive().get());
    assertEquals(ServerType.SYNC, config.getServerType().get());
    assertEquals(16, config.getMinThreads().get().intValue());
    assertEquals(2048, config.getMaxThreads().get().intValue());
    assertEquals(new DataSize(2048, DataSize.Unit.BYTE), config.getSendBuffSize().get());
    assertEquals(new DataSize(2048, DataSize.Unit.BYTE), config.getRecvBuffSize().get());
    assertEquals(Duration.standardSeconds(10), config.getTimeout().get());
  }

}
