package com.brighttag.agathon.resources.yaml.config;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.net.HostAndPort;
import com.proofpoint.units.DataSize;

import com.brighttag.agathon.model.config.RpcConfiguration;
import com.brighttag.agathon.model.config.RpcConfiguration.ServerType;
import com.brighttag.agathon.resources.MediaTypes;
import com.brighttag.yaml.YamlException;
import com.brighttag.yaml.YamlObject;

/**
 * Reader for converting a {@link YamlObject} into a {@link RpcConfiguration}.
 *
 * @author codyaray
 * @since 7/15/12
 */
@Provider
@Consumes(MediaTypes.APPLICATION_YAML)
public class RpcConfigurationReader
    extends AbstractConfigurationReader<RpcConfiguration> {

  @Override
  public RpcConfiguration fromYaml(YamlObject config) throws YamlException {
    return new RpcConfiguration.Builder()
        .hostAndPort(optHostAndPort(config, "rpc_address", "rpc_port"))
        .keepalive(config.optBoolean("rpc_keepalive", null))
        .serverType(optServerType(config, "rpc_server_type"))
        .minThreads(config.optInt("rpc_min_threads", null))
        .maxThreads(config.optInt("rpc_max_threads", null))
        .sendBuffSize(optDataSize(config, "rpc_send_buff_size_in_bytes", DataSize.Unit.BYTE))
        .recvBuffSize(optDataSize(config, "rpc_recv_buff_size_in_bytes", DataSize.Unit.BYTE))
        .timeout(optDuration(config, "rpc_timeout_in_ms"))
        .build();
  }

  private @Nullable HostAndPort optHostAndPort(YamlObject config, String hostKey, String portKey) {
    String host = config.optString(hostKey);
    Integer port = config.optInt(portKey, null);
    HostAndPort hostAndPort = null;
    if (port != null) {
      hostAndPort = HostAndPort.fromParts(Strings.nullToEmpty(host), port);
    } else if (host != null) {
      hostAndPort = HostAndPort.fromString(host);
    }
    return hostAndPort;
  }

  private @Nullable ServerType optServerType(YamlObject config, String key) throws YamlException {
    return config.has(key) ? ServerType.valueOf(config.getString(key).toUpperCase()) : null;
  }

}
