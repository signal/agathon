/*
 * Copyright 2014 BrightTag, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.brighttag.agathon.model;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.google.common.testing.EqualsTester;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 5/12/12
 */
public class CassandraInstanceTest {

  private static final int ID = 10;
  private static final String DATA_CENTER = "dataCenter";
  private static final String RACK = "rack";
  private static final String HOST_NAME = "hostName";
  private static final String PUBLIC_IP_ADDRESS = "publicIpAddress";

  private static Validator validator;

  @BeforeClass
  public static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  public void validate() {
    CassandraInstance instance = builder().build();
    Set<ConstraintViolation<CassandraInstance>> violations = validator.validate(instance);
    assertEquals(0, violations.size());
  }

  @Test
  public void validate_invalidWithNullValues() {
    assertNotEmptyViolation(DATA_CENTER, builder().dataCenter(null).build());
    assertNotEmptyViolation(RACK, builder().rack(null).build());
    assertNotEmptyViolation(HOST_NAME, builder().hostName(null).build());
    assertNotEmptyViolation(PUBLIC_IP_ADDRESS, builder().publicIpAddress(null) .build());
  }

  @Test
  public void validate_invalidWithEmptyValues() {
    assertNotEmptyViolation(DATA_CENTER, builder().dataCenter("").build());
    assertNotEmptyViolation(RACK, builder().rack("").build());
    assertNotEmptyViolation(HOST_NAME, builder().hostName("").build());
    assertNotEmptyViolation(PUBLIC_IP_ADDRESS, builder().publicIpAddress("").build());
  }

  @Test
  public void validate_invalidWithLessThanMinValues() {
    assertNotLessThanViolation("id", 1, builder().id(0).build());
  }

  @Test
  public void equals() {
    new EqualsTester()
        .addEqualityGroup(builder().build(), builder().build())
        .addEqualityGroup(builder().id(ID).build())
        .addEqualityGroup(builder().dataCenter(DATA_CENTER).build())
        .addEqualityGroup(builder().rack(RACK).build())
        .addEqualityGroup(builder().hostName(HOST_NAME).build())
        .addEqualityGroup(builder().publicIpAddress(PUBLIC_IP_ADDRESS).build())
        .testEquals();
  }

  private void assertNotEmptyViolation(String key, CassandraInstance instance) {
    assertViolation(key, "may not be empty", instance);
  }

  private void assertNotLessThanViolation(String key, int value, CassandraInstance instance) {
    assertViolation(key, "must be greater than or equal to " + value, instance);
  }

  private void assertViolation(String key, String message, CassandraInstance instance) {
    Set<ConstraintViolation<CassandraInstance>> violations = validator.validate(instance);
    assertEquals(1, violations.size());
    ConstraintViolation<CassandraInstance> violation = violations.iterator().next();
    assertEquals(key, violation.getPropertyPath().toString());
    assertEquals(message, violation.getMessage());
  }

  private CassandraInstance.Builder builder() {
    return new CassandraInstance.Builder()
        .id(1)
        .dataCenter("dataCenter0")
        .rack("rack0")
        .hostName("hostName0")
        .publicIpAddress("1.1.1.1");
  }

}
