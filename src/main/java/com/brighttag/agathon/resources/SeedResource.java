package com.brighttag.agathon.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.common.base.Joiner;
import com.google.inject.Inject;

import com.brighttag.agathon.service.SeedService;

/**
 * Resource for retrieving seed nodes for the Cassandra coprocess.
 *
 * @author codyaray
 * @since 5/25/2012
 */
@Path("/seeds")
@Produces(MediaType.TEXT_PLAIN)
public class SeedResource {

  private static final Joiner SEED_JOINER = Joiner.on(",").skipNulls();

  private final SeedService service;

  @Inject
  public SeedResource(SeedService service) {
    this.service = service;
  }

  /**
   * Returns the seeds for the coprocess instance as a comma-separated string.
   * @return the seeds for the coprocess instance
   */
  @GET
  public String getSeeds() {
    return SEED_JOINER.join(service.getSeeds());
  }

}
