package com.brighttag.agathon.dao.sdb;

import java.util.Set;

import javax.annotation.Nullable;

import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import com.brighttag.agathon.dao.CassandraRingDao;
import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.model.CassandraRing;

/**
 * SimpleDB implementation of {@link CassandraRingDao}.
 * <br/>
 * All methods throw AmazonClientException and AmazonServiceExceptions at runtime.
 * TODO: catch, wrap, and rethrow these as less Amazon-y exceptions.
 *
 * @author codyaray
 * @since 9/16/2013
 */
public class SdbCassandraRingDao implements CassandraRingDao {

  private final AmazonSimpleDBClient client;
  private final SdbCassandraInstanceDao instanceDao;
  private final CassandraDomainFactory domainFactory;
  private final Provider<Set<String>> ringsProvider;

  @Inject
  public SdbCassandraRingDao(AmazonSimpleDBClient client, SdbCassandraInstanceDao instanceDao,
      CassandraDomainFactory domainFactory,
      @Named(SdbDaoModule.RINGS_PROPERTY) Provider<Set<String>> ringsProvider) {
    this.client = client;
    this.instanceDao = instanceDao;
    this.domainFactory = domainFactory;
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
  public @Nullable CassandraRing findByName(String name) {
    if (!ringsProvider.get().contains(name)) {
      return null;
    }
    return getByName(name);
  }

  @Override
  public void save(CassandraRing ring) {
    String domain = domain(ring);
    if (!ringsProvider.get().contains(domain)) {
      client.createDomain(new CreateDomainRequest().withDomainName(domain));
    }
    for (CassandraInstance instance : ring.getInstances()) {
      instanceDao.save(ring.getName(), instance);
    }
  }

  @Override
  public void delete(CassandraRing ring) {
    String domain = domain(ring);
    if (ringsProvider.get().contains(domain)) {
      client.deleteDomain(new DeleteDomainRequest().withDomainName(domain));
    }
  }

  private String domain(CassandraRing ring) {
    return domainFactory.createFromRing(ring.getName()).toString();
  }

  private CassandraRing getByName(String ring) {
    return new CassandraRing.Builder().name(ring).instances(instanceDao.findAll(ring)).build();
  }

}
