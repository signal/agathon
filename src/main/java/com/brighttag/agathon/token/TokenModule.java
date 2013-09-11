package com.brighttag.agathon.token;

import com.google.common.collect.ImmutableList;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Guice module to wire up token management functionality.
 *
 * @author codyaray
 * @since 9/06/2013
 */
public class TokenModule extends PrivateModule {

  public static final String NODES_PER_DATACENTER_PROPERTY = "com.brighttag.agathon.nodes.per_datacenter";

  @Override
  protected void configure() {
    // Explicitly binding impls to avoid exposing them automatically
    // See {@link Injector.html#createChildInjector(Module...)}
    bind(AssignedTokenService.class).in(Singleton.class);
    bind(AlternatingNetworkTopologyTokenService.class).in(Singleton.class);
    bind(TokenService.class).to(CompositeTokenService.class).in(Singleton.class);
    expose(TokenService.class);
  }

  @Provides @Singleton
  Iterable<TokenService> provideTokenServices(AssignedTokenService assignedToken,
      AlternatingNetworkTopologyTokenService newToken) {
    return ImmutableList.of(assignedToken, newToken);
  }

  @Provides @Singleton @Named(NODES_PER_DATACENTER_PROPERTY)
  int provideNodesPerDataCenter() {
    return Integer.getInteger(NODES_PER_DATACENTER_PROPERTY, 4);
  }

}
