package com.brighttag.agathon.security.ec2;

import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Exposed;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import com.brighttag.agathon.aws.AwsModule;
import com.brighttag.agathon.model.CassandraInstance;
import com.brighttag.agathon.security.SecurityGroupModule;
import com.brighttag.agathon.security.SecurityGroupService;

/**
 * Guice module to wire up security group management for Amazon EC2.
 *
 * @author Greg Opaczewski
 * @since 1/11/2013
 */
public class Ec2SecurityGroupModule extends PrivateModule {

  @Override
  protected void configure() {
    install(new AwsModule());
    bind(SecurityGroupService.class).to(Ec2SecurityGroupService.class).in(Singleton.class);
    expose(SecurityGroupService.class);
  }

  /**
   * Intentionally not a singleton. Must use a new client for each EC2 region.
   * @see AmazonEC2#setEndpoint(String)
   */
  @Provides
  AmazonEC2 provideAmazonEc2(AWSCredentials credentials) {
    return new AmazonEC2Client(credentials);
  }

  @Provides @Singleton @Named(SecurityGroupModule.SECURITY_GROUP_DATACENTERS_PROPERTY)
  Map<String, Region> provideRegions() {
    ImmutableMap.Builder<String, Region> regionMap = ImmutableMap.builder();
    for (Regions region : Regions.values()) {
      // On first call, this makes a network request to fetch the metadata for all regions
      regionMap.put(region.getName(), Region.getRegion(region));
    }
    return regionMap.build();
  }

  @Provides @Exposed @Singleton @Named(SecurityGroupModule.SECURITY_GROUP_DATACENTERS_PROPERTY)
  Function<CassandraInstance, String> provideDataCenterTransform() {
    return new Function<CassandraInstance, String>() {
      @Override
      public String apply(CassandraInstance instance) {
        // Transform from Cassandra DataCenter/Rack to AWS Region
        // Assumes the {@link Ec2Snitch} or {@link Ec2MultiRegionSnitch}
        // For example, DC: "us-east", Rack: "1a" => Region: "us-east-1"
        return instance.getDataCenter() + "-" + instance.getRack().substring(0, 1);
      }
    };
  }

}
