package com.brighttag.agathon.model.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author codyaray
 * @since 7/15/12
 */
public class RequestSchedulerOptionsTest {

  @Test
  public void equals() {
    new EqualsTester()
      .addEqualityGroup(builder().build(), builder().build())
      .addEqualityGroup(builder().throttleLimit(80))
      .addEqualityGroup(builder().defaultWeight(2))
      .addEqualityGroup(builder().weights(ImmutableMap.of("keyspace1", 1, "keyspace2", 5)).build())
      .testEquals();
  }

  @Test(expected = NullPointerException.class)
  public void build_nullThrottleLimit() {
    builder().throttleLimit(null).build();
  }

  @Test(expected = NullPointerException.class)
  public void build_nullWeights() {
    builder().weights(null).build();
  }

  @Test
  public void accessors_requiredAttributes() {
    RequestSchedulerOptions config = builder().build();
    assertEquals(100, config.getThrottleLimit());
  }

  @Test
  public void accessors_optionalAttributes_defaults() {
    RequestSchedulerOptions config = builder().build();
    assertFalse(config.getDefaultWeight().isPresent());
    assertEquals(ImmutableMap.of(), config.getWeights());
  }

  private RequestSchedulerOptions.Builder builder() {
    return new RequestSchedulerOptions.Builder().throttleLimit(100);
  }
}
