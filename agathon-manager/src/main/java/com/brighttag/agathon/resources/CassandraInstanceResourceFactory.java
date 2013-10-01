package com.brighttag.agathon.resources;

import com.brighttag.agathon.model.CassandraRing;

/**
* Creates a {@link CassandraInstanceResource} as a sub-resource of a {@link CassandraRing}.
*
* @author codyaray
* @since 9/17/2013
*/
public interface CassandraInstanceResourceFactory {

  /**
   * Creates an instance resource for a ring.
   */
  CassandraInstanceResource create(CassandraRing ring);

}
