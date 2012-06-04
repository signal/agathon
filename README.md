# Agathon - Cassandra Management

Automated Cassandra operations and management. Heavily inspired by Netflix's Priam.

## Install

1. Build the code with Maven: `mvn clean install`
2. Copy `target/agathon.jar` into your `$CASS_HOME/lib` directory.
3. Deploy `target/agathon.war` into your favorite J2EE container, such as JBoss or Tomcat.

## Cassandra Configuration

Agathon runs as a Cassandra coprocess.

Set the `seed_provider` property in `cassandra.yaml` to `com.brighttag.agathon.cassandra.AgathonSeedProvider`. Any parameters are ignored.

### Optional Properties

* `com.brighttag.agathon.seeds_url`: the URL to get Cassandra seeds as a comma-separated list; defaults to `http://127.0.0.1:8080/agathon/seeds`

## Agathon Configuration

Configuration is currently done through system properties.

### Required Properties

* `com.brighttag.agathon.cassandra_id`: the ID of the Cassandra coprocess; Cassandra instance record must exist in database

### Optional Properties

* `com.brighttag.agathon.database`: the database used for storing Cassandra instance records; one of `sdb` (SimpleDB), `fake` (in-memory store); defaults to `sdb`
* `com.brighttag.agathon.nodes.per_datacenter`: the number of seeds per data center returned to the `AgathonSeedProvider`; defaults to `2`
* `com.brighttag.agathon.seeds.per_datacenter`: the number of nodes per data center used for token calculations; defaults to `4`
* `com.brighttag.agathon.aws.access_key`: your Amazon Web Service Access Key. Required for AWS support (e.g., for SimpleDB)
* `com.brighttag.agathon.aws.secret_key`: your Amazon Web Service Secret Key. Required for AWS support (e.g., for SimpleDB)

## Usage

Just startup your J2EE container. To quickly see it in action, run with embedded Tomcat:

    mvn tomcat:run -Dcom.brighttag.agathon.cassandra_id=1 -Dcom.brighttag.agathon.database=fake

Now you need to tell the Agathon app about your Cassandra instances. At minimum, this must include the
Cassandra coprocess instance (i.e., the ID given as a system property). 

    curl -v -HContent-Type:application/json -XPOST "http://localhost:8080/agathon/instances" -d \
      '{"id":"1","datacenter":"us-east","rack":"1a","token":1234,"hostname":"cass01ea1"}'

## REST API

### Cassandra Configuration

* Get the set of seeds for the coprocess: `GET /seeds`
* Get the initial token for the coprocess: `GET /token`

#### Get the set of seeds for the coprocess

    GET /seeds

The server will reply with a comma-separated set of seed hosts.

    cass02ea1,cass01we1,cass02we1

#### Get the initial token for the coprocess

    GET /token

The server will reply with the initial token.

    1808575600

### Cassandra Instance Record Management

All instance management endpoints consume and produce `application/json`. The `Accept` and `Content-Type` headers
must be set appropriately.

* Get the list of all instances: `GET /instances`
* Get a single instance by ID: `GET /instances/{id}`
* Create or update an instance: `POST /instances`
* Delete an instance: `DELETE /instances/{id}`

#### Get the list of all instances

    GET /instances

This will return a JSON array of Cassandra instance objects.

    [{"id":"1","token":"1234","rack":"1a","hostName":"cass01ea1","dataCenter":"us-east"},
     {"id":"2","token":"4567","rack":"1a","hostName":"cass01we1","dataCenter":"us-west"},
     {"id":"3","token":"8910","rack":"1b","hostName":"cass02ea1","dataCenter":"us-east"},
     {"id":"4","token":"9999","rack":"1b","hostName":"cass02we1","dataCenter":"us-west"}]

#### Get a single instance by ID

    GET /instances/1

This will return a single JSON instance object. 

    {"id":"1","token":"1234","rack":"1a","hostName":"cass01ea1","dataCenter":"us-east"}

#### Create or update an instance

    POST /instances

    {"id":"1","datacenter":"us-east","rack":"1a","token":1234,"hostname":"cass01ea1"}

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

If you haven't, tests will complain about not being able to find a gem. Then, to run the integration tests:

    > rake test

Just type that. It assumes you are running

* An instance of `agathon` at `http://localhost:8080/agathon`

#### Running tests against an ad-hoc environment

If you would like to run integration tests against an `agathon` running on some other hostname or port, go for it.
Simply define the `AGATHON_HOST` environment variable before running `rake test`. For instance, you could point
at a development environment by running the following:

    > AGATHON_HOST="http://cass01.dev.thebrighttag.com" \
      rake test

It's that easy!
