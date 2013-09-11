package com.brighttag.agathon.token;

import java.math.BigInteger;

import javax.annotation.Nullable;

import com.brighttag.agathon.model.CassandraInstance;

/**
 * Provides the token for a Cassandra instance. Alternative implementations
 * may provide support for different network and ring topologies.
 *
 * @author codyaray
 * @since 6/4/12
 */
public interface TokenService {

  /**
   * Returns the token for the instance, or {@code null} if not available.
   * @return the token for the instance
   */
  @Nullable BigInteger getToken(CassandraInstance instance);

}
