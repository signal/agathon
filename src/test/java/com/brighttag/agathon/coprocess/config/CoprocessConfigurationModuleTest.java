package com.brighttag.agathon.coprocess.config;

import java.io.OutputStream;

import com.google.inject.Key;
import com.google.inject.name.Names;

import org.easymock.EasyMockSupport;
import org.junit.Test;

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
import com.brighttag.agathon.token.TokenService;
import com.brighttag.testing.ModuleTester;

/**
 * @author codyaray
 * @since 9/06/2013
 */
public class CoprocessConfigurationModuleTest extends EasyMockSupport {

  @Test
  public void bindings() throws Exception {
    new ModuleTester(new CoprocessConfigurationModule())
        .dependsOn(TokenService.class, createMock(TokenService.class))
        .exposes(CassandraConfigurationService.class)
        .exposes(Key.get(OutputStream.class,
            Names.named(CoprocessConfigurationModule.CASSANDRA_YAML_LOCATION)))
        // TODO: This stuff should NOT be exposed
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
