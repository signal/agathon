package com.brighttag.agathon.model.config;

import com.google.common.testing.EqualsTester;

import org.joda.time.Duration;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * @author codyaray
 * @since 6/27/12
 */
public class HintedHandoffConfigurationTest {

  @Test
  public void equals() {
    new EqualsTester()
      .addEqualityGroup(builder().build(), builder().build())
      .addEqualityGroup(builder().enabled(true).build())
      .addEqualityGroup(builder().maxHintWindow(Duration.standardMinutes(5)).build())
      .addEqualityGroup(builder().throttleDelay(Duration.standardSeconds(30)).build())
      .testEquals();
  }

  @Test
  public void defaults() {
    HintedHandoffConfiguration config = HintedHandoffConfiguration.DEFAULT;
    assertFalse(config.getEnabled().isPresent());
    assertFalse(config.getMaxHintWindow().isPresent());
    assertFalse(config.getThrottleDelay().isPresent());
  }

  private HintedHandoffConfiguration.Builder builder() {
    return new HintedHandoffConfiguration.Builder();
  }
}
