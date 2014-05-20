# Agathon Cassandra Plugin

Cassandra plugin providing a `SeedProvider` backed by Agathon-Manager.

## Install

1. Build the code with Maven: `mvn clean install`
2. Copy `target/agathon-cassandra.jar` into your `$CASS_HOME/lib` directory.

## Configuration

Set the `seed_provider` property in `cassandra.yaml` to `com.brighttag.agathon.cassandra.AgathonSeedProvider`.

### Parameters

* `ring_name`: the name of the ring which this instance is part. (required)
* `agathon_host`: the hostname of the agathon-manager. (optional; defaults to `localhost`)

### Example

    seed_provider:
        - class_name: com.brighttag.agathon.cassandra.AgathonSeedProvider
          parameters:
              - agathon_host: "agathon01ea1"
                ring_name: "userstats"
