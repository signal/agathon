package com.brighttag.agathon.service.impl;

import com.google.inject.Key;
import com.google.inject.name.Names;

import org.easymock.EasyMockSupport;
import org.junit.Test;

import com.brighttag.agathon.dao.CassandraInstanceDao;
import com.brighttag.agathon.resources.yaml.config.CassandraConfigurationReader;
import com.brighttag.agathon.resources.yaml.config.CassandraConfigurationWriter;
import com.brighttag.agathon.resources.yaml.config.CommitLogConfigurationReader;
import com.brighttag.agathon.resources.yaml.config.CommitLogConfigurationWriter;
import com.brighttag.agathon.resources.yaml.config.CompactionConfigurationReader;
import com.brighttag.agathon.resources.yaml.config.CompactionConfigurationWriter;
import com.brighttag.agathon.resources.yaml.config.HintedHandoffConfigurationReader;
import com.brighttag.agathon.resources.yaml.config.HintedHandoffConfigurationWriter;
import com.brighttag.agathon.resources.yaml.config.PerformanceConfigurationReader;
import com.brighttag.agathon.resources.yaml.config.PerformanceConfigurationWriter;
import com.brighttag.agathon.resources.yaml.config.RpcConfigurationReader;
import com.brighttag.agathon.resources.yaml.config.RpcConfigurationWriter;
import com.brighttag.agathon.resources.yaml.config.SnitchConfigurationReader;
import com.brighttag.agathon.resources.yaml.config.SnitchConfigurationWriter;
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
        .exposes(CassandraConfigurationReader.class)
        .exposes(CassandraConfigurationWriter.class)
        .exposes(CommitLogConfigurationReader.class)
        .exposes(CommitLogConfigurationWriter.class)
        .exposes(CompactionConfigurationReader.class)
        .exposes(CompactionConfigurationWriter.class)
        .exposes(HintedHandoffConfigurationReader.class)
        .exposes(HintedHandoffConfigurationWriter.class)
        .exposes(PerformanceConfigurationReader.class)
        .exposes(PerformanceConfigurationWriter.class)
        .exposes(RpcConfigurationReader.class)
        .exposes(RpcConfigurationWriter.class)
        .exposes(SnitchConfigurationReader.class)
        .exposes(SnitchConfigurationWriter.class)
        .exposesNothingElse()
        .verify();
  }

}
