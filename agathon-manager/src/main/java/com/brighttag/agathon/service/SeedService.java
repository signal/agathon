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

package com.brighttag.agathon.service;

import com.google.common.collect.ImmutableSet;

import com.brighttag.agathon.model.CassandraRing;

/**
 * Provides the set of public domain names and IP addresses used to seed the Cassandra ring.
 * Alternative implementations may provide support for different network and ring topologies.
 *
 * @author codyaray
 * @since 5/25/12
 */
public interface SeedService {

  /**
   * Returns the seeds for a Cassandra ring.
   *
   * @param ring the Cassandra ring
   * @return the seeds for the Cassandra ring
   */
  ImmutableSet<String> getSeeds(CassandraRing ring);

}
