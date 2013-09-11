package com.brighttag.agathon.token;

import java.math.BigInteger;

import javax.annotation.Nullable;

import com.brighttag.agathon.model.CassandraInstance;

/**
 * A {@link TokenService} that returns the token attached to the
 * {@literal @}{@link Coprocess} {@link CassandraInstance}.
 *
 * @author codyaray
 * @since 6/4/12
 */
public class AssignedTokenService implements TokenService {

  @Override
  public @Nullable BigInteger getToken(CassandraInstance instance) {
    return instance.getToken();
  }

}
