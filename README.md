# Agathon - Cassandra Management

Automated Cassandra operations and management. Heavily inspired by Netflix's Priam.

## Features

* Manifest of Cassandra Rings and Instances. Currently supported backends: SimpleDB, Zerg, or create your own adapter.
* Security Group Management. Currently supported backends: EC2.
* Cassandra Seed Provider. Backed by Agathon Manager's manifest.
* Deploy to your own hardware or any cloud provider.
* Designed to be super easy to extend.

(Zerg is BrightTag's internal ops center. Zerg is not yet open-sourced... but hopefully it will be someday. :)

## Components

This application consists of two components:

* agathon-manager: RESTful web service
* agathon-cassandra: plugin for Cassandra

In the future, a UI may be provided for interacting with the backing web service.
But for now, `curl` will have to do. :)

## Install

1. Build the code with Maven: `mvn clean install`
2. Copy `agathon-cassandra/target/agathon-cassandra.jar` into your `$CASS_HOME/lib` directory.
3. Deploy `agathon-manager/target/agathon-manager.war` into your favorite J2EE container, such as JBoss or Tomcat.

## Configuration, Usage, Testing

See `READMEs` in each sub-project directory.

## Copyright and License

Copyright 2014 BrightTag, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.