package com.brighttag.agathon.model.config;

import java.util.Arrays;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import com.proofpoint.units.DataSize;

import org.joda.time.Duration;

/**
 * Configuration options related to Cassandra's RPC server.
 *
 * @author codyaray
 * @since 6/27/12
 */
public class RpcConfiguration {

  /**
   * The default configuration if no options are specified.
   */
  public static final RpcConfiguration DEFAULT = new Builder().build();

  /**
   * Cassandra provides you with a variety of options for RPC Server.
   */
  public enum ServerType {
    /**
     * Creates one thread per connection but with a configurable number of threads. This can
     * be expensive in-memory used for thread stack for a large enough number of clients.
     * (Hence, connection pooling is very, very strongly recommended.)
     */
    SYNC,

    /**
     * Nonblocking server implementation with one thread to serve rpc connections.
     * This is not recommended for high throughput use cases.
     */
    ASYNC,

    /**
     * Half sync and half async implementation with configurable number of worker
     * threads (for managing connections). IO Management is done by a set of threads
     * currently equal to the number of processors in the system. The number of threads
     * in the threadpool is configured via {@link RpcConfiguration#minThreads minThreads}
     * and {@link RpcConfiguration#maxThreads maxThreads}. (Connection pooling is strongly
     * recommended in this case too.)
     */
    HSHA
  };

  /**
   * The address to bind the Thrift RPC service to -- clients connect here.
   * Unlike {@link CassandraConfiguration#getListenAddress() listenAddress}, you
   * <b>can</b> specify 0.0.0.0 here if you want Thrift to listen on all interfaces.
   *
   * Leaving it blank leaves it up to InetAddress.getLocalHost(). This
   * will always do the Right Thing <b>if</b> the node is properly configured
   * (hostname, name resolution, etc), and the Right Thing is to use the
   * address associated with the hostname (it might not be).
   */
  private final Optional<HostAndPort> hostAndPort;

  /**
   * Enable or disable keepalive on RPC connections.
   */
  private final Optional<Boolean> keepalive;

  /**
   * Describes the type of RPC server used by Cassandra.
   */
  private final Optional<ServerType> serverType;

  /**
   * Set the request pool size. Default is 16.
   */
  private final Optional<Integer> minThreads;

  /**
   * Set the request pool size. You would primarily set max for the sync server
   * to safeguard against misbehaved clients; if you do hit the max, Cassandra will
   * block until one disconnects before accepting more. Default is unlimited.
   *
   * For the Hsha server, you would set the max so that a fair amount of resources
   * are provided to the other working threads on the server.
   *
   * This configuration is not used for the async server.
   */
  private final Optional<Integer> maxThreads;

  /**
   * Set the sending socket buffer size for RPC connections.
   */
  private final Optional<DataSize> sendBuffSize;

  /**
   * Set the receiving socket buffer size for RPC connections.
   */
  private final Optional<DataSize> recvBuffSize;

  /**
   * Time to wait for a reply from other nodes before failing the command.
   */
  private final Optional<Duration> timeout;

  private RpcConfiguration(Builder builder) {
    this.hostAndPort = builder.hostAndPort;
    this.keepalive = builder.keepalive;
    this.serverType = builder.serverType;
    this.minThreads = builder.minThreads;
    this.maxThreads = builder.maxThreads;
    this.sendBuffSize = builder.sendBuffSize;
    this.recvBuffSize = builder.recvBuffSize;
    this.timeout = builder.timeout;
  }

  public Optional<HostAndPort> getHostAndPort() {
    return hostAndPort;
  }

  public Optional<Boolean> getKeepalive() {
    return keepalive;
  }

  public Optional<ServerType> getServerType() {
    return serverType;
  }

  public Optional<Integer> getMinThreads() {
    return minThreads;
  }

  public Optional<Integer> getMaxThreads() {
    return maxThreads;
  }

  public Optional<DataSize> getSendBuffSize() {
    return sendBuffSize;
  }

  public Optional<DataSize> getRecvBuffSize() {
    return recvBuffSize;
  }

  public Optional<Duration> getTimeout() {
    return timeout;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(significantAttributes());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (obj == this) {
      return true;
    } else if (!getClass().isAssignableFrom(obj.getClass())) {
      return false;
    }
    return Arrays.equals(significantAttributes(), getClass().cast(obj).significantAttributes());
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("hostAndPort", hostAndPort)
        .add("keepalive", keepalive)
        .add("serverType", serverType)
        .add("minThreads", minThreads)
        .add("maxThreads", maxThreads)
        .add("sendBuffSize", sendBuffSize)
        .add("recvBuffSize", recvBuffSize)
        .add("timeout", timeout)
        .toString();
  }

  private Object[] significantAttributes() {
    return new Object[] {
      hostAndPort, keepalive, serverType, minThreads,
      maxThreads, sendBuffSize, recvBuffSize, timeout,
    };
  }

  /**
   * Fluent builder for {@link RpcConfiguration}s.
   *
   * @author codyaray
   * @since 6/27/12
   */
  public static class Builder {

    private Optional<HostAndPort> hostAndPort = Optional.absent();
    private Optional<Boolean> keepalive = Optional.absent();
    private Optional<ServerType> serverType = Optional.absent();
    private Optional<Integer> minThreads = Optional.absent();
    private Optional<Integer> maxThreads = Optional.absent();
    private Optional<DataSize> sendBuffSize = Optional.absent();
    private Optional<DataSize> recvBuffSize = Optional.absent();
    private Optional<Duration> timeout = Optional.absent();

    public Builder hostAndPort(@Nullable HostAndPort hostAndPort) {
      this.hostAndPort = Optional.fromNullable(hostAndPort);
      return this;
    }

    public Builder keepalive(@Nullable Boolean keepalive) {
      this.keepalive = Optional.fromNullable(keepalive);
      return this;
    }

    public Builder serverType(@Nullable ServerType serverType) {
      this.serverType = Optional.fromNullable(serverType);
      return this;
    }

    public Builder minThreads(@Nullable Integer minThreads) {
      this.minThreads = Optional.fromNullable(minThreads);
      return this;
    }

    public Builder maxThreads(@Nullable Integer maxThreads) {
      this.maxThreads = Optional.fromNullable(maxThreads);
      return this;
    }

    public Builder sendBuffSize(@Nullable DataSize sendBuffSize) {
      this.sendBuffSize = Optional.fromNullable(sendBuffSize);
      return this;
    }

    public Builder recvBuffSize(@Nullable DataSize recvBuffSize) {
      this.recvBuffSize = Optional.fromNullable(recvBuffSize);
      return this;
    }

    public Builder timeout(@Nullable Duration timeout) {
      this.timeout = Optional.fromNullable(timeout);
      return this;
    }

    public RpcConfiguration build() {
      return new RpcConfiguration(this);
    }

  }

}
