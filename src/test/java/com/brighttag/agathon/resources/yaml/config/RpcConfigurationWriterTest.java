package com.brighttag.agathon.resources.yaml.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 7/15/12
 */
public class RpcConfigurationWriterTest extends AbstractConfigurationWriterTest {

  @Test
  public void toYaml_min() throws Exception {
    String config = createConfig(MIN_CONFIG.getRpcConfiguration(), new RpcConfigurationWriter());
    assertEquals("{}\n", config);
  }

  @Test
  public void toYaml_max() throws Exception {
    String config = createConfig(MAX_CONFIG.getRpcConfiguration(), new RpcConfigurationWriter());
    assertEquals(LINE_JOINER.join(
        "rpc_address: localhost",
        "rpc_port: 9160",
        "rpc_keepalive: true",
        "rpc_server_type: sync",
        "rpc_min_threads: 16",
        "rpc_max_threads: 2048",
        "rpc_send_buff_size_in_bytes: 2048",
        "rpc_recv_buff_size_in_bytes: 2048",
        "rpc_timeout_in_ms: 10000",
        ""
    ), config);
  }

}
