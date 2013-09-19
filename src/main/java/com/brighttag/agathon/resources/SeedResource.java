package com.brighttag.agathon.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.common.base.Joiner;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.brighttag.agathon.model.CassandraRing;
import com.brighttag.agathon.service.SeedService;

/**
 * Resource for retrieving seed nodes for Cassandra.
 *
 * @author codyaray
 * @since 5/25/2012
 */
@Produces(MediaType.TEXT_PLAIN)
public class SeedResource {

  private static final Joiner SEED_JOINER = Joiner.on(",").skipNulls();

  private final SeedService service;
  private final CassandraRing ring;

  @Inject
  public SeedResource(SeedService service, @Assisted CassandraRing ring) {
    this.service = service;
    this.ring = ring;
  }

  /**
   * Returns the seeds for the Cassandra ring as a comma-separated string.
   * @return the seeds for the Cassandra ring
   */
  @GET
  public String getSeeds() {
    return SEED_JOINER.join(service.getSeeds(ring));
  }

}
