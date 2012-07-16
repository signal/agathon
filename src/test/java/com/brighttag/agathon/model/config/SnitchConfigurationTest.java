
package com.brighttag.agathon.model.config;

import com.google.common.testing.EqualsTester;

import org.apache.cassandra.locator.Ec2MultiRegionSnitch;
import org.apache.cassandra.locator.SimpleSnitch;
import org.joda.time.Period;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author codyaray
 * @since 6/27/12
 */
public class SnitchConfigurationTest {

  @Test
  public void equals() {
    new EqualsTester()
      .addEqualityGroup(builder().build(), builder().build())
      .addEqualityGroup(builder().endpointSnitch(Ec2MultiRegionSnitch.class).build())
      .addEqualityGroup(builder().dynamicSnitch(true).build())
      .addEqualityGroup(builder().dynamicSnitchUpdateInterval(Period.millis(100)).build())
      .addEqualityGroup(builder().dynamicSnitchResetInterval(Period.minutes(10)).build())
      .addEqualityGroup(builder().dynamicSnitchBadnessThreshold(0.0).build())
      .testEquals();
  }

  @Test
  public void defaults() {
    SnitchConfiguration config = SnitchConfiguration.DEFAULT;
    assertEquals(SimpleSnitch.class, config.getEndpointSnitch());
    assertFalse(config.getDynamicSnitch().isPresent());
    assertFalse(config.getDynamicSnitchUpdateInterval().isPresent());
    assertFalse(config.getDynamicSnitchResetInterval().isPresent());
    assertFalse(config.getDynamicSnitchBadnessThreshold().isPresent());
  }

  private SnitchConfiguration.Builder builder() {
    return new SnitchConfiguration.Builder();
  }
}
