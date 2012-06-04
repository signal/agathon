package com.brighttag.agathon.resources;

import java.math.BigInteger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;

import com.brighttag.agathon.service.TokenService;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Resource for retrieving the token for the Cassandra coprocess.
 *
 * @author codyaray
 * @since 6/4/2012
 */
@Path("/token")
@Produces(MediaType.TEXT_PLAIN)
public class TokenResource {

  private final TokenService service;

  @Inject
  public TokenResource(TokenService service) {
    this.service = service;
  }

  /**
   * Returns the token for the coprocess instance.
   * @return the token for the coprocess instance
   */
  @GET
  public String getToken() {
    BigInteger token = service.getToken();
    checkNotNull(token, "Unable to return a token");
    return token.toString();
  }

}
