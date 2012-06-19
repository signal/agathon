package com.brighttag.agathon.model;

import java.math.BigInteger;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.google.common.testing.EqualsTester;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author codyaray
 * @since 5/12/12
 */
public class CassandraInstanceTest {

  private static final String ID = "id";
  private static final BigInteger TOKEN = BigInteger.ONE;
  private static final String DATA_CENTER = "dataCenter";
  private static final String RACK = "rack";
  private static final String HOST_NAME = "hostName";

  private static final BigInteger TOKEN1 = BigInteger.valueOf(1);
  private static final BigInteger TOKEN2 = BigInteger.valueOf(2);
  private static final BigInteger TOKEN3 = BigInteger.valueOf(3);

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
    assertNotEmptyViolation(ID, builder().id(null).build());
    assertNotNullViolation("token", builder().token(null).build());
    assertNotEmptyViolation(DATA_CENTER, builder().dataCenter(null).build());
    assertNotEmptyViolation(RACK, builder().rack(null).build());
    assertNotEmptyViolation(HOST_NAME, builder().hostName(null).build());
  }

  @Test
  public void validate_invalidWithEmptyValues() {
    assertNotEmptyViolation(ID, builder().id("").build());
    assertNotEmptyViolation(DATA_CENTER, builder().dataCenter("").build());
    assertNotEmptyViolation(RACK, builder().rack("").build());
    assertNotEmptyViolation(HOST_NAME, builder().hostName("").build());
  }

  @Test
  public void validate_invalidWithLessThanMinValues() {
    assertNotLessThanViolation("token", 0, builder().token(BigInteger.valueOf(-1)).build());
  }

  @Test
  public void equals() {
    new EqualsTester()
        .addEqualityGroup(builder().build(), builder().build())
        .addEqualityGroup(builder().id(ID).build())
        .addEqualityGroup(builder().token(TOKEN).build())
        .addEqualityGroup(builder().dataCenter(DATA_CENTER).build())
        .addEqualityGroup(builder().rack(RACK).build())
        .addEqualityGroup(builder().hostName(HOST_NAME).build())
        .testEquals();
  }

  @Test
  public void compareTo_orderedByToken() {
    assertTrue(builder().token(TOKEN1).build().compareTo(builder().token(TOKEN2).build()) < 0);
    assertTrue(builder().token(TOKEN2).build().compareTo(builder().token(TOKEN2).build()) == 0);
    assertTrue(builder().token(TOKEN3).build().compareTo(builder().token(TOKEN2).build()) > 0);
  }

  private void assertNotNullViolation(String key, CassandraInstance instance) {
    assertViolation(key, "may not be null", instance);
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
        .id("0")
        .token(BigInteger.valueOf(0))
        .dataCenter("dataCenter0")
        .rack("rack0")
        .hostName("hostName0");
  }

}
