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
* `com.brighttag.agathon.seeds.per_datacenter`: the number of seed nodes per data center returned to the `AgathonSeedProvider`; defaults to `2`
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

### Cassandra Seeds

* Get the list of seeds for the coprocess: `GET /seeds`

#### Get the list of seeds for the coprocess

    GET /seeds

The server will reply with a comma-separated list of seed hosts. The order is arbitrary.

    cass02ea1,cass01we1,cass02we1

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

Lots of units.

    mvn test

Integration tests to come.
