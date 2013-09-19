package com.brighttag.agathon.dao.sdb;

import javax.annotation.Nullable;

/**
 * Factory for creating {@link CassandraDomain}s.
 *
 * @author codyaray
 * @since 9/23/2013
 */
interface CassandraDomainFactory {

  /**
   * Creates a CassandraDomain from a {@code ring}.
   *
   * @param ring name of the Cassandra ring
   * @return CassandraDomain object
   */
  CassandraDomain createFromRing(String ring);

  /**
   * Creates a CassandraDomain from a SimpleDB {@code domain}.
   * Returns {@code null} if the domain name can't be parsed.
   *
   * @param domain the SimpleDB domain name
   * @return CassandraDomain object or {@code null} if {@code domain} can't be parsed
   */
  @Nullable CassandraDomain createFromDomain(String domain);
}
