package com.brighttag.agathon.dao.zerg;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import com.brighttag.agathon.dao.CassandraRingDao;
import com.brighttag.agathon.model.CassandraRing;

/**
 *
 * @author codyaray
 * @since 9/17/2013
 */
public class ZergCassandraRingDao implements CassandraRingDao {

  private final ZergCassandraInstanceDao instanceDao;
  private final Provider<Set<String>> ringsProvider;

  @Inject
  public ZergCassandraRingDao(ZergCassandraInstanceDao instanceDao,
      @Named(ZergDaoModule.RINGS_PROPERTY) Provider<Set<String>> ringsProvider) {
    this.instanceDao = instanceDao;
    this.ringsProvider = ringsProvider;
  }

  @Override
  public ImmutableSet<CassandraRing> findAll() {
    ImmutableSet.Builder<CassandraRing> ringBuilder = ImmutableSet.builder();
    for (String ring : ringsProvider.get()) {
      ringBuilder.add(getByName(ring));
    }
    return ringBuilder.build();
  }

  @Override
  public CassandraRing findByName(String name) {
    if (!ringsProvider.get().contains(name)) {
      return null;
    }
    return getByName(name);
  }

  @Override
  public void save(CassandraRing ring) {
    throw new UnsupportedOperationException("Save is not supported for " + getClass().getSimpleName());
  }

  @Override
  public void delete(CassandraRing ring) {
    throw new UnsupportedOperationException("Delete is not supported for " + getClass().getSimpleName());
  }

  private CassandraRing getByName(String ring) {
    return new CassandraRing.Builder().name(ring).instances(instanceDao.findAll(ring)).build();
  }

}
