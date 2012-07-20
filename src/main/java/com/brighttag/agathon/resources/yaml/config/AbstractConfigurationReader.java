package com.brighttag.agathon.resources.yaml.config;

import java.io.File;

import javax.annotation.Nullable;

import com.google.common.base.Throwables;
import com.proofpoint.units.DataSize;

import org.joda.time.Duration;
import org.joda.time.Period;

import com.brighttag.agathon.resources.yaml.AbstractYamlReader;
import com.brighttag.yaml.YamlException;
import com.brighttag.yaml.YamlObject;

/**
 * Base class for Yaml configuration readers.
 *
 * @param <T> the type of the domain object
 * @author codyaray
 * @since 7/15/12
 */
public abstract class AbstractConfigurationReader<T> extends AbstractYamlReader<T> {

  protected @Nullable Period optPeriod(YamlObject config, String key) {
    try {
      return new Period(config.getLong(key).longValue());
    } catch (YamlException e) {
      return null;
    }
  }

  protected @Nullable Duration optDuration(YamlObject config, String key) {
    try {
      return new Duration(config.getLong(key));
    } catch (YamlException e) {
      return null;
    }
  }

  protected @Nullable DataSize optDataSize(YamlObject config, String key, DataSize.Unit unit) {
    try {
      return new DataSize(config.getLong(key), unit);
    } catch (YamlException e) {
      return null;
    }
  }

  protected @Nullable File optFile(YamlObject config, String key) {
    try {
      return new File(config.getString(key));
    } catch (YamlException e) {
      return null;
    }
  }

  protected @Nullable <V> Class<? extends V> optClass(YamlObject config, String key) {
    try {
      @SuppressWarnings("unchecked")
      Class<? extends V> klass = (Class<? extends V>) Class.forName(config.getString(key));
      return klass;
    } catch (YamlException e) {
      return null;
    } catch (ClassNotFoundException e) {
      throw Throwables.propagate(e);
    }
  }
}
