package com.brighttag.agathon.token;

import java.math.BigInteger;

import javax.annotation.Nullable;

import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brighttag.agathon.model.CassandraInstance;

/**
 * A {@link TokenService} that returns the first non-null token produced
 * by the given {@code services}.
 *
 * @author codyaray
 * @since 6/5/12
 */
public class CompositeTokenService implements TokenService {

  private static final Logger LOG = LoggerFactory.getLogger(CompositeTokenService.class);

  private final Iterable<TokenService> services;

  @Inject
  public CompositeTokenService(Iterable<TokenService> services) {
    this.services = services;
  }

  @Override
  public @Nullable BigInteger getToken(CassandraInstance instance) {
    for (TokenService service : services) {
      BigInteger token = service.getToken(instance);
      if (token != null) {
        LOG.debug("Using token {} from {}", token, service.getClass().getSimpleName());
        return token;
      }
    }
    LOG.warn("Failed to return a token");
    return null;
  }

}
