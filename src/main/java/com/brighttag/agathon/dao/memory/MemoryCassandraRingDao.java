package com.brighttag.agathon.dao.memory;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brighttag.agathon.dao.CassandraRingDao;
import com.brighttag.agathon.model.CassandraRing;

/**
 * In-memory implementation of {@link CassandraRingDao}.
 *
 * @author codyaray
 * @since 9/16/2013
 */
class MemoryCassandraRingDao implements CassandraRingDao {

  private static final Logger LOG = LoggerFactory.getLogger(MemoryCassandraRingDao.class);

  private final Map<String, CassandraRing> rings;

  public MemoryCassandraRingDao() {
    this(Maps.<String, CassandraRing>newHashMap());
  }

  @VisibleForTesting MemoryCassandraRingDao(Map<String, CassandraRing> rings) {
    this.rings = rings;
  }

  @Override
  public ImmutableSet<CassandraRing> findAll() {
    LOG.info("Returning rings: {}", rings.values());
    return ImmutableSet.copyOf(rings.values());
  }

  @Override
  public @Nullable CassandraRing findByName(String name) {
    LOG.info("Returning ring: {}", rings.get(name));
    return rings.get(name);
  }

  @Override
  public void save(CassandraRing ring) {
    LOG.info("Saving ring: {}", ring);
    rings.put(ring.getName(), ring);
  }

  @Override
  public void delete(CassandraRing ring) {
    LOG.info("Deleting ring: {}", ring);
    rings.remove(ring.getName());
  }

}
