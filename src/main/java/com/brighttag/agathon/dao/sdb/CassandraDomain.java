package com.brighttag.agathon.dao.sdb;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents the name of a Cassandra domain in SDB.
 * Simple immutable object does not implement equals or hashCode.
 *
 * @author codyaray
 * @since 9/18/2013
 */
class CassandraDomain {

  private final String domainNamespace;
  private final String ring;

  CassandraDomain(String namespace, String ring) {
    this.domainNamespace = checkNotNull(namespace);
    this.ring = checkNotNull(ring);
  }

  static @Nullable CassandraDomain createFromDomain(String namespace, String domain) {
    String[] parts = domain.split("\\.");
    if (parts.length == 3 && SdbDaoModule.DOMAIN_PREFIX.equals(parts[0]) && namespace.equals(parts[1])) {
      return new CassandraDomain(namespace, parts[2]);
    }
    return null;
  }

  public String getRing() {
    return ring;
  }

  @Override
  public String toString() {
    return String.format("%s.%s.%s", SdbDaoModule.DOMAIN_PREFIX, domainNamespace, ring);
  }

}
