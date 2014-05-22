/*
 * Copyright 2014 BrightTag, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
