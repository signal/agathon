package com.brighttag.agathon.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.google.inject.Exposed;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Guice module to wire up common AWS dependencies.
 * <br/>
 * Use of all AWS modules requires the {@code ACCESS_KEY_PROPERTY}
 * and {@code SECRET_KEY_PROPERTY} system properties to be set.
 *
 * @author codyaray
 * @since 6/4/12
 */
public class AwsModule extends PrivateModule {

  public static final String ACCESS_KEY_PROPERTY = "com.brighttag.agathon.aws.access_key";
  public static final String SECRET_KEY_PROPERTY = "com.brighttag.agathon.aws.secret_key";

  @Override
  protected void configure() {
    // Nothing to do
  }

  @Provides @Exposed @Singleton
  AWSCredentials provideAWSCredentials(
      @Named(ACCESS_KEY_PROPERTY) String accessKey,
      @Named(SECRET_KEY_PROPERTY) String secretKey) {
    return new BasicAWSCredentials(accessKey, secretKey);
  }

  @Provides @Singleton @Named(ACCESS_KEY_PROPERTY)
  String provideAccessKey() {
    return checkNotNull(System.getProperty(ACCESS_KEY_PROPERTY), "Access Key must be set");
  }

  @Provides @Singleton @Named(SECRET_KEY_PROPERTY)
  String provideSecretKey() {
    return checkNotNull(System.getProperty(SECRET_KEY_PROPERTY), "Secret Key must be set");
  }

}
