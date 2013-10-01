package com.brighttag.agathon.resources;

import com.brighttag.agathon.model.CassandraRing;

/**
* Creates a {@link SeedResource} as a sub-resource of a {@link CassandraRing}.
*
* @author codyaray
* @since 9/18/2013
*/
public interface SeedResourceFactory {

  /**
   * Creates a seed resource for a ring.
   */
  SeedResource create(CassandraRing ring);

}
