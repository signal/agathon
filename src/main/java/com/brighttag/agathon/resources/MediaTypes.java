package com.brighttag.agathon.resources;

import javax.ws.rs.core.MediaType;

/**
 * Common {@link MediaType}s.
 *
 * @author codyaray
 * @since 7/16/12
 */
public final class MediaTypes {

  private MediaTypes() { /* No instances */ }

  public static final String APPLICATION_YAML = "application/yaml";
  public static final MediaType APPLICATION_YAML_TYPE = MediaType.valueOf(APPLICATION_YAML);

}
