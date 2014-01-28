package com.brighttag.agathon.dao.sdb;

import java.util.List;
import java.util.Set;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.ListDomainsRequest;
import com.amazonaws.services.simpledb.model.ListDomainsResult;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import com.brighttag.agathon.aws.AwsModule;
import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.dao.CassandraRingDao;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Guice module to wire up the SimpleDB DAO.
 * <br/>
 * Use of this module requires the {@code ACCESS_KEY_PROPERTY}
 * and {@code SECRET_KEY_PROPERTY} system properties to be set.
 *
 * @author codyaray
 * @since 5/15/12
 */
public class SdbDaoModule extends PrivateModule {

  // Configuration options
  static final String DOMAIN_NAMESPACE_PROPERTY =
      "com.brighttag.agathon.dao.sdb.domain_namespace";

  // Internal bindings and constants
  static final String RINGS_PROPERTY = "com.brighttag.agathon.cassandra.rings";
  static final String DOMAIN_PREFIX = "CassandraInstances";

  @Override
  protected void configure() {
    install(new AwsModule());
    bind(CassandraDomainFactory.class).to(CassandraDomainFactoryImpl.class).in(Singleton.class);
    bindConstant().annotatedWith(Names.named(DOMAIN_NAMESPACE_PROPERTY)).to(
        checkNotNull(System.getProperty(DOMAIN_NAMESPACE_PROPERTY), "SimpleDB domain namespace must be set"));
    bind(CassandraInstanceDao.class).to(SdbCassandraInstanceDao.class);
    bind(CassandraRingDao.class).to(SdbCassandraRingDao.class);
    expose(CassandraInstanceDao.class);
    expose(CassandraRingDao.class);
  }

  @Provides
  AmazonSimpleDBClient provideAmazonSimpleDBClient(AWSCredentials credentials) {
    return new AmazonSimpleDBClient(credentials);
  }

  @Provides @Named(RINGS_PROPERTY)
  Set<String> provideRings(AmazonSimpleDBClient client, CassandraDomainFactory domainFactory) {
    List<String> rings = Lists.newArrayList();
    String nextToken = null;

    do {
      ListDomainsRequest request = new ListDomainsRequest().withNextToken(nextToken);
      ListDomainsResult result = client.listDomains(request);
      for (String domain : result.getDomainNames()) {
        CassandraDomain cassandraDomain = domainFactory.createFromDomain(domain);
        if (cassandraDomain != null) {
          rings.add(cassandraDomain.getRing());
        }
      }
      nextToken = result.getNextToken();
    } while (nextToken != null);
    return ImmutableSet.copyOf(rings);
  }

}
