package com.brighttag.agathon.resources;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;

import com.brighttag.agathon.service.ServiceUnavailableException;

/**
 * Maps {@link ServiceUnavailableException}s to HTTP 503 "Service Unavailable" responses.
 * The X-Error header includes a hint to the cause.
 *
 * @author codyaray
 * @since 9/27/2013
 */
@Provider
public class ServiceUnavailableExceptionMapper implements ExceptionMapper<ServiceUnavailableException> {

  @Override
  public Response toResponse(ServiceUnavailableException exception) {
    return Response.status(Response.Status.SERVICE_UNAVAILABLE)
        // TODO Is there a standard name for an error header?
        .header("X-Error", getMessage(exception))
        .build();
  }

  private static final Joiner NAME_JOINER = Joiner.on("|");

  private static final Function<Throwable, String> EXCEPTION_NAME =
      new Function<Throwable, String>() {
        @Override
        public String apply(Throwable t) {
          return t.getClass().getSimpleName();
        }
      };

  private static String getMessage(Throwable exception) {
    String name = NAME_JOINER.join(FluentIterable.from(Throwables.getCausalChain(exception))
        .transform(EXCEPTION_NAME).toList());
    Throwable rootCause = Throwables.getRootCause(exception);
    return String.format("%s: %s", name, rootCause.getMessage());
  }

}
