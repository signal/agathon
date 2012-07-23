package com.brighttag.agathon.resources.yaml.config;

import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

import com.proofpoint.units.DataSize;

import com.brighttag.agathon.model.config.RpcConfiguration;
import com.brighttag.agathon.resources.MediaTypes;
import com.brighttag.yaml.YamlException;
import com.brighttag.yaml.YamlObject;

/**
 * Writer for converting a {@link RpcConfiguration} into a {@link YamlObject}.
 *
 * @author codyaray
 * @since 7/15/12
 */
@Provider
@Consumes(MediaTypes.APPLICATION_YAML)
public class RpcConfigurationWriter
    extends AbstractConfigurationWriter<RpcConfiguration> {

  @Override
  public YamlObject toYaml(RpcConfiguration config) throws YamlException {
    return new YamlObject.Builder()
        .putIfNotNull("rpc_address", config.getHostAndPort().isPresent() ?
            config.getHostAndPort().get().getHostText() : null)
        .putIfNotNull("rpc_port", config.getHostAndPort().isPresent() ?
            config.getHostAndPort().get().getPort() : null)
        .putIfNotNull("rpc_keepalive", config.getKeepalive())
        .putIfNotNull("rpc_server_type", config.getServerType().isPresent() ?
            config.getServerType().get().toString().toLowerCase() : null)
        .putIfNotNull("rpc_min_threads", config.getMinThreads())
        .putIfNotNull("rpc_max_threads", config.getMaxThreads())
        .putIfNotNull("rpc_send_buff_size_in_bytes",
            optDataSize(config.getSendBuffSize(), DataSize.Unit.BYTE))
        .putIfNotNull("rpc_recv_buff_size_in_bytes",
            optDataSize(config.getRecvBuffSize(), DataSize.Unit.BYTE))
        .putIfNotNull("rpc_timeout_in_ms", optDuration(config.getTimeout()))
        .build();
  }

}
