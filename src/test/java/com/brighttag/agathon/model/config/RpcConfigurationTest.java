package com.brighttag.agathon.model.config;

import com.google.common.net.HostAndPort;
import com.google.common.testing.EqualsTester;
import com.proofpoint.units.DataSize;

import org.joda.time.Duration;
import org.junit.Test;

import com.brighttag.agathon.model.config.RpcConfiguration.ServerType;

import static org.junit.Assert.assertFalse;

/**
 * @author codyaray
 * @since 6/27/12
 */
public class RpcConfigurationTest {

  @Test
  public void equals() {
    new EqualsTester()
      .addEqualityGroup(builder().build(), builder().build())
      .addEqualityGroup(builder().hostAndPort(HostAndPort.fromString("localhost:9160")).build())
      .addEqualityGroup(builder().keepalive(true).build())
      .addEqualityGroup(builder().serverType(ServerType.SYNC).build())
      .addEqualityGroup(builder().minThreads(16).build())
      .addEqualityGroup(builder().maxThreads(2048).build())
      .addEqualityGroup(builder().sendBuffSize(DataSize.valueOf("2MB")).build())
      .addEqualityGroup(builder().recvBuffSize(DataSize.valueOf("2MB")).build())
      .addEqualityGroup(builder().timeout(Duration.standardSeconds(5)).build())
      .testEquals();
  }

  @Test
  public void defaults() {
    RpcConfiguration config = RpcConfiguration.DEFAULT;
    assertFalse(config.getHostAndPort().isPresent());
    assertFalse(config.getKeepalive().isPresent());
    assertFalse(config.getServerType().isPresent());
    assertFalse(config.getMinThreads().isPresent());
    assertFalse(config.getMaxThreads().isPresent());
    assertFalse(config.getSendBuffSize().isPresent());
    assertFalse(config.getRecvBuffSize().isPresent());
    assertFalse(config.getTimeout().isPresent());
  }

  private RpcConfiguration.Builder builder() {
    return new RpcConfiguration.Builder();
  }
}
