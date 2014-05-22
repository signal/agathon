/*
 * Copyright 2014 BrightTag, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.brighttag.agathon.dao.sdb;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Simple implementation of {@link CassandraDomainFactory}.
 *
 * Unfortunately, we can't use Guice factories here because both variations
 * ({@link #createFromDomain(String)} and {@link #createFromRing(String)} require constructors
 * with the same signatures. We had to use a factory method here instead for one of the cases.
 *
 * @author codyaray
 * @since 9/23/2013
 */
class CassandraDomainFactoryImpl implements CassandraDomainFactory {

  private final String namespace;

  @Inject
  public CassandraDomainFactoryImpl(@Named(SdbDaoModule.DOMAIN_NAMESPACE_PROPERTY) String namespace) {
    this.namespace = namespace;
  }

  @Override
  public CassandraDomain createFromRing(String ring) {
    return new CassandraDomain(namespace, ring);
  }

  @Override
  public CassandraDomain createFromDomain(String domain) {
    return CassandraDomain.createFromDomain(namespace, domain);
  }

}
