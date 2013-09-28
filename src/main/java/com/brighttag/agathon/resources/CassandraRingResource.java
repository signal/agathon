package com.brighttag.agathon.resources;

import java.net.URI;
import java.util.Set;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.google.inject.Inject;
import com.sun.jersey.api.NotFoundException;

import com.brighttag.agathon.model.CassandraRing;
import com.brighttag.agathon.service.CassandraRingService;

/**
 * Resource for manipulating Cassandra ring records.
 *
 * @author codyaray
 * @since 9/17/2013
 */
@Path("/rings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CassandraRingResource {

  private final CassandraRingService service;
  private final CassandraInstanceResourceFactory instanceResourceFactory;
  private final SeedResourceFactory seedResourceFactory;

  @Inject
  public CassandraRingResource(CassandraRingService service,
      CassandraInstanceResourceFactory instanceResourceFactory,
      SeedResourceFactory seedResourceFactory) {
    this.service = service;
    this.instanceResourceFactory = instanceResourceFactory;
    this.seedResourceFactory = seedResourceFactory;
  }

  /**
   * Return the set of Cassandra rings.
   * @return the set of Cassandra rings
   * @throws ServiceUnavailableException if a required downstream service is unavailable
   */
  @GET
  public Set<CassandraRing> findAll() {
    return service.findAll();
  }

  /**
   * Creates a new Cassandra ring record.
   *
   * @param ring the Cassandra ring
   * @return Response (201) if the Cassandra ring record was created
   */
  @POST
  public Response createRing(@Valid CassandraRing ring) {
    service.save(ring);
    URI location = UriBuilder.fromPath("{name}").build(ring.getName());
    return Response.created(location).build();
  }

  /**
   * Returns a Cassandra ring by {@code name}.
   *
   * @param name the Cassandra ring name
   * @return the Cassandra ring
   * @throws NotFoundException if ring not found with {@code name}
   * @throws ServiceUnavailableException if a required downstream service is unavailable
   */
  @GET
  @Path("{name}")
  public CassandraRing findByName(@PathParam("name") String name) {
    return getByNameIfFound(name);
  }

  /**
   * Delete the Cassandra ring with the given {@code name}.
   *
   * @param ring the Cassandra ring name
   * @return Response (204) if the Cassandra ring was deleted
   * @throws NotFoundException if ring not found with {@code name}
   * @throws ServiceUnavailableException if a required downstream service is unavailable
   */
  @DELETE
  @Path("{name}")
  public Response deleteRing(@PathParam("name") String name) {
    service.delete(getByNameIfFound(name));
    return Response.noContent().build();
  }

  /**
   * Delegate to sub-resource for instance requests.
   *
   * @param name the Cassandra ring name
   * @return the Cassandra instance resource
   * @throws NotFoundException if ring not found with {@code name}
   * @throws ServiceUnavailableException if a required downstream service is unavailable
   */
  @Path("{name}/instances")
  public CassandraInstanceResource getCassandraInstanceResource(@PathParam("name") String name) {
    return instanceResourceFactory.create(getByNameIfFound(name));
  }

  /**
   * Delegate to sub-resource for seed requests.
   *
   * @param name the Cassandra ring name
   * @return the Cassandra seed resource
   * @throws NotFoundException if ring not found with {@code name}
   * @throws ServiceUnavailableException if a required downstream service is unavailable
   */
  @Path("{name}/seeds")
  public SeedResource getSeedResource(@PathParam("name") String name) {
    return seedResourceFactory.create(getByNameIfFound(name));
  }

  private CassandraRing getByNameIfFound(String name) {
    CassandraRing ring = service.findByName(name);
    if (ring == null) {
      throw new NotFoundException(String.format("No ring found with name: %s", name));
    }
    return ring;
  }

}
