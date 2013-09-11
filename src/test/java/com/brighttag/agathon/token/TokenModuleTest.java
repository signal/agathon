package com.brighttag.agathon.token;

import org.easymock.EasyMockSupport;
import org.junit.Test;

import com.brighttag.testing.ModuleTester;

/**
 * @author codyaray
 * @since 9/06/2013
 */
public class TokenModuleTest extends EasyMockSupport {

  @Test
  public void bind() throws Exception {
    new ModuleTester(new TokenModule())
        .exposes(TokenService.class)
        .exposesNothingElse()
        .verify();
  }

}
