package com.brighttag.agathon.coprocess;

import java.io.File;
import java.io.OutputStream;
import java.util.Set;

import com.google.common.util.concurrent.Service;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import org.easymock.EasyMockSupport;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.brighttag.agathon.coprocess.config.CassandraConfigurationService;
import com.brighttag.agathon.coprocess.config.CoprocessConfigurationModule;
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
import com.brighttag.agathon.token.TokenService;
import com.brighttag.testing.ModuleTester;

import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 5/12/12
 */
public class CoprocessModuleTest extends EasyMockSupport {

  private static final int CASSANDRA_ID = 1;

  private static File yamlFile;

  @BeforeClass
  public static void setRequiredSystemProperties() throws Exception {
    yamlFile = File.createTempFile("cassandra", ".yaml"); // TODO: shouldn't be required for testing
    yamlFile.deleteOnExit();
    System.setProperty(CoprocessConfigurationModule.CASSANDRA_YAML_LOCATION, yamlFile.getAbsolutePath());
    System.setProperty(SystemPropertyCoprocessProvider.CASSANDRA_ID_PROPERTY, String.valueOf(CASSANDRA_ID));
  }

  @AfterClass
  public static void clearRequiredSystemProperties() {
    System.clearProperty(SystemPropertyCoprocessProvider.CASSANDRA_ID_PROPERTY);
    System.clearProperty(CoprocessModule.CONFIG_REWRITE_ENABLED_PROPERTY);
  }

  @Test
  public void bindings_enabled() throws Exception {
    System.setProperty(CoprocessModule.CONFIG_REWRITE_ENABLED_PROPERTY, "true");
    Injector injector = new ModuleTester(new CoprocessModule())
        .dependsOn(CassandraInstanceService.class, createMock(CassandraInstanceService.class))
        .dependsOn(TokenService.class, createMock(TokenService.class))
        .exposes(CassandraConfigurationService.class)
        .exposes(CassandraConfigurationRewriterService.class)
        .exposesMultibinding(Service.class)
        // TODO: This stuff should NOT be exposed
        .exposes(Key.get(OutputStream.class,
            Names.named(CoprocessConfigurationModule.CASSANDRA_YAML_LOCATION)))
        .exposes(Key.get(new TypeLiteral<Provider<OutputStream>>() { },
            Names.named(CoprocessConfigurationModule.CASSANDRA_YAML_LOCATION)))
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

    Set<Service> services = injector.getInstance(Key.get(new TypeLiteral<Set<Service>>() { }));
    assertEquals(CassandraConfigurationRewriterService.class, services.iterator().next().getClass());
    assertEquals(1, services.size());
  }

  @Test
  public void bindings_disabled() throws Exception {
    System.setProperty(CoprocessModule.CONFIG_REWRITE_ENABLED_PROPERTY, "false");
    new ModuleTester(new CoprocessModule())
        .exposesNothingElse()
        .verify();
  }

}
