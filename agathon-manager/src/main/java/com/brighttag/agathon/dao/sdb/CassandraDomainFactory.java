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

import javax.annotation.Nullable;

/**
 * Factory for creating {@link CassandraDomain}s.
 *
 * @author codyaray
 * @since 9/23/2013
 */
interface CassandraDomainFactory {

  /**
   * Creates a CassandraDomain from a {@code ring}.
   *
   * @param ring name of the Cassandra ring
   * @return CassandraDomain object
   */
  CassandraDomain createFromRing(String ring);

  /**
   * Creates a CassandraDomain from a SimpleDB {@code domain}.
   * Returns {@code null} if the domain name can't be parsed.
   *
   * @param domain the SimpleDB domain name
   * @return CassandraDomain object or {@code null} if {@code domain} can't be parsed
   */
  @Nullable CassandraDomain createFromDomain(String domain);
}
