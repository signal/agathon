package com.brighttag.agathon.service;

import java.math.BigInteger;

import javax.annotation.Nullable;

/**
 * Provides the token for the Cassandra coprocess. Alternative implementations
 * may provide support for different network and ring topologies.
 *
 * @author codyaray
 * @since 6/4/12
 */
public interface TokenService {

  /**
   * Returns the token for the coprocess instance, or {@code null} if not available.
   * @return the token for the coprocess instance
   */
  @Nullable BigInteger getToken();

}
