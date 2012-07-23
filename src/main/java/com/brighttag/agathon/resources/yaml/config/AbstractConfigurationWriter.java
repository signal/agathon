package com.brighttag.agathon.resources.yaml.config;

import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.proofpoint.units.DataSize;

import org.joda.time.Duration;
import org.joda.time.Period;

import com.brighttag.agathon.resources.yaml.AbstractYamlWriter;

/**
 * Base class for Yaml configuration writers.
 *
 * @param <T> the type of the domain object
 * @author codyaray
 * @since 7/22/12
 */
public abstract class AbstractConfigurationWriter<T> extends AbstractYamlWriter<T> {

  protected @Nullable Long optPeriod(Optional<Period> period) {
    if (!period.isPresent()) {
      return null;
    }
    return period.get().toStandardDuration().getMillis();
  }

  protected @Nullable Long optDuration(Optional<Duration> duration) {
    if (!duration.isPresent()) {
      return null;
    }
    return duration.get().getMillis();
  }

  protected @Nullable Long optDataSize(Optional<DataSize> dataSize, DataSize.Unit unit) {
    if (!dataSize.isPresent()) {
      return null;
    }
    return dataSize.get().roundTo(unit);
  }

}
