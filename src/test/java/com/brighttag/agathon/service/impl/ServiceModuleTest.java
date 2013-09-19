package com.brighttag.agathon.service.impl;

import org.easymock.EasyMockSupport;
import org.junit.Test;

import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.dao.CassandraRingDao;
import com.brighttag.agathon.service.CassandraInstanceService;
import com.brighttag.agathon.service.CassandraRingService;
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
        .dependsOn(CassandraRingDao.class, createMock(CassandraRingDao.class))
        .exposes(CassandraInstanceService.class)
        .exposes(CassandraRingService.class)
        .exposes(SeedService.class)
        .exposesNothingElse()
        .verify();
  }

}
