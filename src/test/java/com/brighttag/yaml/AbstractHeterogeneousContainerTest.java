package com.brighttag.yaml;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author codyaray
 * @since 7/16/12
 */
public class AbstractHeterogeneousContainerTest {

  private static final String UNAVAILABLE_KEY = "key10";

  private SimpleHeterogeneousMap obj;

  @Before
  public void setUp() {
    obj = new SimpleHeterogeneousMap(getProperties());
  }

  @Test
  public void get_success() throws Exception {
    assertEquals("string", obj.getString("key1"));
    assertEquals(true, obj.getBoolean("key2"));
    assertEquals(3, obj.getInt("key3").intValue());
    assertEquals(4.0, obj.getDouble("key4").doubleValue(), 0.01);
    assertEquals(5L, obj.getLong("key5").longValue());
    assertEquals(new SimpleHeterogeneousArray(getList()), obj.getArray("key6"));
    assertEquals(new SimpleHeterogeneousMap(getMap()), obj.getMap("key7"));
    assertEquals(new YamlArray(getList()), obj.getArray("key8"));
    assertEquals(new YamlObject(getMap()), obj.getMap("key9"));
  }

  @Test
  public void get_failure() throws Exception {
    assertHeterogeneousException("SimpleHeterogeneousMap[key10] not found", new Callable<String>() {
      @Override
      public String call() throws HeterogeneousException {
        return obj.getString(UNAVAILABLE_KEY);
      }
    });
    assertHeterogeneousException("SimpleHeterogeneousMap[key1] is not a Boolean.", new Callable<Boolean>() {
      @Override
      public Boolean call() throws HeterogeneousException {
        return obj.getBoolean("key1");
      }
    });
    assertHeterogeneousException("SimpleHeterogeneousMap[key2] is not a number.", new Callable<Integer>() {
      @Override
      public Integer call() throws HeterogeneousException {
        return obj.getInt("key2");
      }
    });
    assertHeterogeneousException("SimpleHeterogeneousMap[key2] is not a number.", new Callable<Double>() {
      @Override
      public Double call() throws HeterogeneousException {
        return obj.getDouble("key2");
      }
    });
    assertHeterogeneousException("SimpleHeterogeneousMap[key2] is not a number.", new Callable<Long>() {
      @Override
      public Long call() throws HeterogeneousException {
        return obj.getLong("key2");
      }
    });
    assertHeterogeneousException("SimpleHeterogeneousMap[key5] is not a HeterogeneousArray",
      new Callable<HeterogeneousArray>() {
        @Override
        public HeterogeneousArray call() throws HeterogeneousException {
          return obj.getArray("key5");
        }
      });
    assertHeterogeneousException("SimpleHeterogeneousMap[key6] is not a HeterogeneousMap",
      new Callable<HeterogeneousMap>() {
        @Override
        public HeterogeneousMap call() throws HeterogeneousException {
          return obj.getMap("key6");
        }
      });
  }

  @Test
  public void opt_withGivenDefault() {
    assertEquals("stringy", obj.optString(UNAVAILABLE_KEY, "stringy"));
    assertEquals(true, obj.optBoolean(UNAVAILABLE_KEY, true));
    assertEquals(3, obj.optInt(UNAVAILABLE_KEY, 3).intValue());
    assertEquals(4.0, obj.optDouble(UNAVAILABLE_KEY, 4.0).doubleValue(), 0.01);
    assertEquals(5L, obj.optLong(UNAVAILABLE_KEY, 5L).longValue());
    assertEquals(new YamlArray(getList()), obj.optArray(UNAVAILABLE_KEY, new YamlArray(getList())));
    assertEquals(new YamlObject(getMap()), obj.optMap(UNAVAILABLE_KEY, new YamlObject(getMap())));
  }

  @Test
  public void opt_withBuiltInDefault() {
    assertEquals(null, obj.optString(UNAVAILABLE_KEY));
    assertEquals(false, obj.optBoolean(UNAVAILABLE_KEY));
    assertEquals(0, obj.optInt(UNAVAILABLE_KEY).intValue());
    assertEquals(Double.NaN, obj.optDouble(UNAVAILABLE_KEY).doubleValue(), 0.01);
    assertEquals(0L, obj.optLong(UNAVAILABLE_KEY).longValue());
    assertEquals(null, obj.optArray(UNAVAILABLE_KEY));
    assertEquals(null, obj.optMap(UNAVAILABLE_KEY));
  }

  private static Map<String, Object> getProperties() {
    Map<String, Object> properties = Maps.newLinkedHashMap();
    properties.put("key1", "string");
    properties.put("key2", true);
    properties.put("key3", 3);
    properties.put("key4", 4.0);
    properties.put("key5", 5L);
    properties.put("key6", getList());
    properties.put("key7", getMap());
    properties.put("key8", new YamlArray(getList()));
    properties.put("key9", new YamlObject(getMap()));
    return properties;
  }

  private <T> void assertHeterogeneousException(String message, Callable<T> callable) {
    try {
      callable.call();
      fail("Expected HeterogeneousException");
    } catch (HeterogeneousException e) {
      assertEquals(message, e.getMessage());
    } catch (Exception e) {
      // Ok to catch Exception here
      fail("Unexpected exception: " + e);
    }
  }

  private static Map<String, Object> getMap() {
    return ImmutableMap.<String, Object>of("boo", "bar");
  }

  private static List<Object> getList() {
    return ImmutableList.<Object>of("item1", 2);
  }

}
