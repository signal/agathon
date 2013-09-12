package com.brighttag.agathon.service.impl;

import com.google.inject.Key;
import com.google.inject.name.Names;

import org.easymock.EasyMockSupport;
import org.junit.Test;

import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.service.CassandraInstanceService;
import com.brighttag.agathon.service.SeedService;
import com.brighttag.testing.ModuleTester;

/**
 * @author codyaray
 * @since 5/12/12
 */
public class ServiceModuleTest extends EasyMockSupport {

  @Test
  public void bind() throws Exception {
    new ModuleTester(new ServiceModule())
        .dependsOn(CassandraInstanceDao.class, createMock(CassandraInstanceDao.class))
        .exposes(CassandraInstanceService.class)
        .exposes(SeedService.class)
        // TODO: This stuff should NOT be exposed
        .exposes(Key.get(Integer.class, Names.named(ServiceModule.SEEDS_PER_DATACENTER_PROPERTY)))
        .exposes(CassandraInstanceServiceImpl.class)
        .exposes(PerDataCenterSeedService.class)
        .exposesNothingElse()
        .verify();
  }

}
