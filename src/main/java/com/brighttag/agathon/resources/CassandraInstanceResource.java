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
import com.google.inject.assistedinject.Assisted;
import com.sun.jersey.api.NotFoundException;

import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.CassandraRing;
import com.brighttag.agathon.service.CassandraInstanceService;

/**
 * Resource for manipulating Cassandra instance records.
 *
 * @author codyaray
 * @since 5/12/2012
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CassandraInstanceResource {

  private final CassandraInstanceService service;
  private final CassandraRing ring;

  @Inject
  public CassandraInstanceResource(CassandraInstanceService service, @Assisted CassandraRing ring) {
    this.service = service;
    this.ring = ring;
  }

  /**
   * Return the set of Cassandra instances.
   * @return the set of Cassandra instances
   */
  @GET
  public Set<CassandraInstance> findAll() {
    return service.findAll(ring.getName());
  }

  /**
   * Creates a new Cassandra instance record.
   *
   * @param instance the Cassandra instance
   * @return Response (201) if the Cassandra instance record was created
   */
  @POST
  public Response createInstance(@Valid CassandraInstance instance) {
    service.save(ring.getName(), instance);
    URI location = UriBuilder.fromPath("{id}").build(instance.getId());
    return Response.created(location).build();
  }

  /**
   * Returns a Cassandra instance by {@code id}.
   *
   * @param id the Cassandra instance ID
   * @return the Cassandra instance
   * @throws NotFoundException if instance not found with {@code id}
   */
  @GET
  @Path("{id}")
  public CassandraInstance findById(@PathParam("id") int id) {
    return getByIdIfFound(id);
  }

  /**
   * Delete the Cassandra instance with the given {@code id}.
   *
   * @param id the Cassandra instance ID
   * @return Response (204) if the Cassandra instance was deleted
   * @throws NotFoundException if instance not found with {@code id}
   */
  @DELETE
  @Path("{id}")
  public Response deleteInstance(@PathParam("id") int id) {
    service.delete(ring.getName(), getByIdIfFound(id));
    return Response.noContent().build();
  }

  private CassandraInstance getByIdIfFound(int id) {
    CassandraInstance instance = service.findById(ring.getName(), id);
    if (instance == null) {
      throw new NotFoundException(String.format("No instance found with id: %s", id));
    }
    return instance;
  }

}
