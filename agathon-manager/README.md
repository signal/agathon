# Agathon Management Service

Centralized web service used for managing your Cassandra configuration.

## Install

1. Build the code with Maven: `mvn clean install`
2. Deploy `target/agathon-manager.war` into your favorite J2EE container, such as JBoss or Tomcat.

## Configuration

Configuration is currently done through system properties.

### Basic Configuration

* `com.brighttag.agathon.database`: the database used for storing Cassandra instance records; one of 
   `sdb` (SimpleDB), `memory` (in-memory store), 'zerg' (BrightTag's Ops Center); defaults to `sdb`.

### Seed Provider Configuration

* `com.brighttag.agathon.seeds.per_datacenter`: the number of seeds per data center returned to the  `AgathonSeedProvider`; defaults to `2`.

### Security Group Management Configuration
* `com.brighttag.agathon.security.group_management_enabled`: set to `true` to enable task that updates a security group with current ring members.
* `com.brighttag.agathon.security.group_name_prefix`: prefix for Agathon/Cassandra security group. Required for
   Security Group Management. If using production AWS account, **must use non-production value for testing**.
* `com.brighttag.agathon.security.group_update_frequency_seconds`: frequency at which security group updates are applied.
* `com.brighttag.agathon.cassandra.gossip_port`: Cassandra gossip port, used to create ingress rules for security group updates.

### Provider-Specific Options

#### SimpleDB Backend
* `com.brighttag.agathon.dao.sdb.domain_name`: the name of the SimpleDB domain storing Cassandra instance records.
   Required for SimpleDB. If using production AWS account, **must use non-production value for testing environments**.

#### Zerg Backend
* `com.brighttag.agathon.dao.zerg.region`: the current region in which Agathon is deployed (e.g., "us-east-1"). Required for Zerg support.
* `com.brighttag.agathon.dao.zerg.ring_scope_file`: location of file defining the Cassandra rings and their scope (environment or region).
   Required for Zerg support.
* `com.brighttag.agathon.dao.zerg.manifest_url`: the url to retrieve the Zerg manifest; defaults to `http://localhost:9374/manifest/environment/prod/`.

#### AWS Credentials
* `com.brighttag.agathon.aws.access_key`: your Amazon Web Service Access Key. Required for AWS support (e.g., for SimpleDB or EC2 Security Group Management).
* `com.brighttag.agathon.aws.secret_key`: your Amazon Web Service Secret Key. Required for AWS support (e.g., for SimpleDB or EC2 Security Group Management).

(Zerg is BrightTag's internal ops center. Zerg is not yet open-sourced... but hopefully it will be someday. :)

## Usage

Just startup your J2EE container. To quickly see it in action, run with embedded Jetty from your IDE or run with mvn-tomcat:

    mvn tomcat:run -Dmaven.tomcat.port=8094 -Dmaven.tomcat.path=/ -Dcom.brighttag.agathon.database=memory

Now you need to tell the Agathon app about your ring and Cassandra instances.

    curl -H "Content-Type: application/json" "http://localhost:8094/ring" -d \
      '{"name":"UserStats","instances":[{"id":"1","datacenter":"us-east","rack":"1a","hostname":"cass01ea1","publicIpAddress":"1.1.1.1"}]}'

Instead of specifying all the instances during ring creation, you can add instances individually to the ring.

    curl -H "Content-Type: application/json" "http://localhost:8094/ring/UserStats/instances" -d \
      '{"id":"2","datacenter":"us-west","rack":"1a","hostname":"cass01we1","publicIpAddress":"2.2.2.2"}'

Although you can manually maintain a record of your instances using the SimpleDB backend, if you already have a manifest of all
instances (e.g., using a cloud provider's API), it is recommended that you extend Agathon with a provider for your backend.
This will save you a lot of headache of manually maintaining this SimpleDB database.

## REST API

### Cassandra Configuration

* Get the set of seeds: `GET /rings/{name}/seeds`

#### Get the set of seeds

    GET /rings/UserStats/seeds

The server will reply with a comma-separated set of seed hosts.

    cass02ea1,cass01we1,cass02we1

### Cassandra Manifest Management

All endpoints consume and produce `application/json`. The `Accept` and `Content-Type` headers must
be set appropriately. Agathon exposes two Cassandra object records: rings and instances.

#### Ring Management

* Get the list of all rings: `GET /rings`
* Get a single ring by name: `GET /rings/{name}`
* Create or update a ring: `POST /rings`
* Delete a ring: `DELETE /rings/{name}`

#### Instance Management

* Get the list of all instances in a ring: `GET /rings/{name}/instances`
* Get a single instance by ID: `GET /rings/{name}/instances/{id}`
* Create or update an instance: `POST /rings/{name}/instances`
* Delete an instance: `DELETE /rings/{name}/instances/{id}`

#### Get the list of all rings

    GET /rings

This will return a JSON array of Cassandra ring objects.

    [{
      "name":"UserStats",
      "instances":[
        {"id":"1","datacenter":"us-east","rack":"1a","hostname":"cass01ea1","publicIpAddress":"1.1.1.1"},
        {"id":"2","datacenter":"us-west","rack":"1a","hostname":"cass01we1","publicIpAddress":"2.2.2.2"},
        {"id":"3","datacenter":"us-east","rack":"1b","hostname":"cass02ea1","publicIpAddress":"1.1.1.3"},
        {"id":"4","datacenter":"us-west","rack":"1b","hostname":"cass02we1","publicIpAddress":"2.2.2.4"}
      ]
    }]

#### Get a single ring by name

    GET /rings/UserStats

This will return a single JSON ring object.

    {
      "name":"UserStats",
      "instances":[
        {"id":"1","rack":"1a","hostname":"cass01ea1","datacenter":"us-east","publicIpAddress":"1.1.1.1"},
        {"id":"2","rack":"1a","hostname":"cass01we1","datacenter":"us-west","publicIpAddress":"2.2.2.2"},
        {"id":"3","rack":"1b","hostname":"cass02ea1","datacenter":"us-east","publicIpAddress":"1.1.1.3"},
        {"id":"4","rack":"1b","hostname":"cass02we1","datacenter":"us-west","publicIpAddress":"2.2.2.4"}
      ]
    }

#### Create or update a ring

    POST /rings

    {
      "name":"UserStats",
      "instances":[
        {"id":"1","rack":"1a","hostname":"cass01ea1","datacenter":"us-east","publicIpAddress":"1.1.1.1"},
        {"id":"2","rack":"1a","hostname":"cass01we1","datacenter":"us-west","publicIpAddress":"2.2.2.2"},
        {"id":"3","rack":"1b","hostname":"cass02ea1","datacenter":"us-east","publicIpAddress":"1.1.1.3"},
        {"id":"4","rack":"1b","hostname":"cass02we1","datacenter":"us-west","publicIpAddress":"2.2.2.4"}
      ]
    }

This will respond with `201 Created` upon success, `400 Bad Request` if the JSON could not be parsed, or
`422 Unprocessable Entity` if the Cassandra ring failed validation. A `plain/text` error message accompanies
all errors and indicates the source of the problem.

#### Delete a ring

    DELETE /rings/UserStats

This will respond with `204 No Content` upon success, or `404 Not Found` if no Cassandra ring record exists with
the given name.

#### Get the list of all instances

    GET /rings/UserStats/instances

This will return a JSON array of Cassandra instance objects.

    [{"id":"1","rack":"1a","hostname":"cass01ea1","datacenter":"us-east","publicIpAddress":"1.1.1.1"},
     {"id":"2","rack":"1a","hostname":"cass01we1","datacenter":"us-west","publicIpAddress":"2.2.2.2"},
     {"id":"3","rack":"1b","hostname":"cass02ea1","datacenter":"us-east","publicIpAddress":"1.1.1.3"},
     {"id":"4","rack":"1b","hostname":"cass02we1","datacenter":"us-west","publicIpAddress":"2.2.2.4"}]

#### Get a single instance by ID

    GET /instances/1

This will return a single JSON instance object. 

    {"id":"1","rack":"1a","hostname":"cass01ea1","datacenter":"us-east","publicIpAddress":"1.1.1.1"}

#### Create or update an instance

    POST /instances

    {"id":"1","datacenter":"us-east","rack":"1a","hostname":"cass01ea1","publicIpAddress":"1.1.1.1"}

This will respond with `201 Created` upon success, `400 Bad Request` if the JSON could not be parsed, or
`422 Unprocessable Entity` if the Cassandra instance failed validation. A `plain/text` error message accompanies
all errors and indicates the source of the problem.

#### Delete an instance

    DELETE /instances/2

This will respond with `204 No Content` upon success, or `404 Not Found` if no Cassandra instance record exists with
the given ID.

## Testing

### Unit Tests

Lots of units.

    mvn test

### Integration Tests

Integration tests exist for all endpoints. All tests are implemented in ruby using a DSL described with Riot Gear,
a combination of Riot (a ruby unit-testing framework) and HTTParty (a simple restful webservice framework).

#### Running tests locally

Make sure you have everything:

    > bundle install

If you haven't, tests will complain about not being able to find a gem. The integration tests assume Agathon
is using SimpleDB and seeds data directly to AWS. Be sure to set `com.brighttag.agathon.database.sdb.domain_name`
to "CassandraInstancesIntegration".

**IMPORTANT:** Because SimpleDB is a globally-available store, there are not separate database instances for each environment.
If you don't set the domain to something different in testing, you'll overwrite your production (or other environment) data
with testing data. The integration tests use "CassandraInstancesIntegration" by default.

Then, to run the integration tests:

    > rake test

Just type that. It assumes you are running an instance of Agathon at `http://localhost:8094`

#### Running tests against an ad-hoc environment

If you would like to run integration tests against an `agathon` running on some other hostname or port, go for it.
Simply define the `AGATHON_HOST` environment variable before running `rake test`. For instance, you could point
at a development environment by running the following:

    > AGATHON_HOST="http://cass01.dev.myhost.com" \
      rake test

It's that easy!
