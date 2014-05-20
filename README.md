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
