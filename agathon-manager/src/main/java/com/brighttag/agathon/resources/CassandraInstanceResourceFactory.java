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

package com.brighttag.agathon.resources;

import com.brighttag.agathon.model.CassandraRing;

/**
* Creates a {@link CassandraInstanceResource} as a sub-resource of a {@link CassandraRing}.
*
* @author codyaray
* @since 9/17/2013
*/
public interface CassandraInstanceResourceFactory {

  /**
   * Creates an instance resource for a ring.
   */
  CassandraInstanceResource create(CassandraRing ring);

}
