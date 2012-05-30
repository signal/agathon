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
import static org.junit.Assert.assertTrue;

/**
 * @author codyaray
 * @since 5/12/12
 */
public class CassandraInstanceTest {

  private static final String ID = "id";
  private static final String TOKEN = "token";
  private static final String DATA_CENTER = "dataCenter";
  private static final String RACK = "rack";
  private static final String HOST_NAME = "hostName";

  private static final String TOKEN1 = "1";
  private static final String TOKEN2 = "2";
  private static final String TOKEN3 = "3";

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
    assertNotEmptyViolation(TOKEN, builder().token(null).build());
    assertNotEmptyViolation(DATA_CENTER, builder().dataCenter(null).build());
    assertNotEmptyViolation(RACK, builder().rack(null).build());
    assertNotEmptyViolation(HOST_NAME, builder().hostName(null).build());
  }

  @Test
  public void validate_invalidWithEmptyValues() {
    assertNotEmptyViolation(ID, builder().id("").build());
    assertNotEmptyViolation(TOKEN, builder().token("").build());
    assertNotEmptyViolation(DATA_CENTER, builder().dataCenter("").build());
    assertNotEmptyViolation(RACK, builder().rack("").build());
    assertNotEmptyViolation(HOST_NAME, builder().hostName("").build());
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

    // test token ordering trumps other properties
    assertTrue(builder().token(TOKEN1).id(TOKEN2).build()
        .compareTo(builder().token(TOKEN2).id(TOKEN1).build()) < 0);
    assertTrue(builder().token(TOKEN1).dataCenter(TOKEN2).build()
        .compareTo(builder().token(TOKEN2).dataCenter(TOKEN1).build()) < 0);
    assertTrue(builder().token(TOKEN1).rack(TOKEN2).build()
        .compareTo(builder().token(TOKEN2).rack(TOKEN1).build()) < 0);
    assertTrue(builder().token(TOKEN1).hostName(TOKEN2).build()
        .compareTo(builder().token(TOKEN2).hostName(TOKEN1).build()) < 0);
  }

  private void assertNotEmptyViolation(String key, CassandraInstance instance) {
    Set<ConstraintViolation<CassandraInstance>> violations = validator.validate(instance);
    assertEquals(1, violations.size());
    ConstraintViolation<CassandraInstance> violation = violations.iterator().next();
    assertEquals(key, violation.getPropertyPath().toString());
    assertEquals("may not be empty", violation.getMessage());
  }

  private CassandraInstance.Builder builder() {
    return new CassandraInstance.Builder()
        .id("id0")
        .token("token0")
        .dataCenter("dataCenter0")
        .rack("rack0")
        .hostName("hostName0");
  }

}
