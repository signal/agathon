package com.brighttag.agathon.coprocess.config;

import com.google.inject.Inject;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.config.CassandraConfiguration;
import com.brighttag.agathon.token.TokenService;

/**
 * Appends a {@link CassandraConfiguration#initialToken token} to the chained configuration.
 *
 * @author codyaray
 * @since 8/2/12
 */
public class TokenAppendingCassandraConfigurationResolver implements CassandraConfigurationResolver {

  private final TokenService tokenService;

  @Inject
  public TokenAppendingCassandraConfigurationResolver(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  @Override
  public CassandraConfiguration getConfiguration(CassandraInstance instance,
      CassandraConfiguration chainedConfiguration) {
    return new CassandraConfiguration.Builder(chainedConfiguration)
        .initialToken(tokenService.getToken(instance))
        .build();
  }

}
