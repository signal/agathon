package com.brighttag.yaml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;

import org.junit.Test;

/**
 * @author codyaray
 * @since 7/16/12
 */
public class YamlObjectTest {

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
