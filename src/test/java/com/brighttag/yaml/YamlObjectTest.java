package com.brighttag.yaml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author codyaray
 * @since 7/16/12
 */
public class YamlObjectTest {

  @Test
  public void constructor_emptyInputStream() {
    assertEquals(new YamlObject(), new YamlObject(baos("")));
  }

  @Test
  public void constructor_nullValueInputStream() {
    assertEquals(new YamlObject(), new YamlObject(baos("key_without_value: ")));
  }

  @Test
  public void equals() {
    new EqualsTester()
        .addEqualityGroup(new YamlObject(), new YamlObject())
        .addEqualityGroup(new YamlObject(ImmutableMap.<String, Object>of("foo", "bar")))
        .addEqualityGroup(new YamlObject(ImmutableMap.<String, Object>of("foo", "baz")))
        .addEqualityGroup(new YamlObject(baos("key: val")))
        .testEquals();
  }

  private InputStream baos(String string) {
    return new ByteArrayInputStream(string.getBytes());
  }

}
