## kafka-interface

### What is kafka-interface?
A gradle based java interface for the kafka messaging platform design to transfer events between eventSourced services.

### How to use kafka-interface
Currently, running `./gradlew bootRun` in the root of the project will build and run the service.

Using docker: `./gradlew build docker` will build, test and compile a docker container.

## Endpoints
### `/subscribers?topic=topic&endpoint=endpoint` method: PUT
Adds a new subscriber to a a given topic, posting events to a given endpoint.
Returns a URI for the created subscriber.
`"topic": <STRING:kafka topic>`,`"endpoint": <STRING:callback endpoint. http://<url>`

### `/subscribers/{id}` method: DELETE
Closes a given subscriber.  
`"id": <STRING:id_of_subscriber_to_unsub>`

### `/branch/<STRING:branch_name>/<LONG:timestamp>` method: PUT  
Creates a new branch off of the current branch from a given timestamp.

### `/checkout/<STRING:branch_name>` method: PUT  
Switches the kafka-interface platform to a certain branch, all further event queries will be from the selected branch.

### `/queries/events/<STRING:aggregate>` method: GET
Returns a list of all events from a selected aggregate from timestamp 0.

### `/queries/events/<STRING:aggregate>?from=<INT:from>` method: GET
Returns a list of all events from a selected aggregate from timestamp `from`.

### `/queries/snapshots/<STRING:aggregate>` method: GET
Returns the most recent snapshot. Snapshots are in the form:  
The snapshot is a list of typeless maps, in an attempt to make it as versatile as possible.
```
{
    "version": <INT:version>,
    "aggregate": <STRING:aggregate>,
    "snapshot": [
        <MAP<>:data_map>
    ]
}
```

### `/queries/branches/` method: GET
Returns a list of all branches.

### `/publish/` method: POST
Post an event to a specific topic.  
```
{
    "aggregate": <STRING:name_of_aggregate>,
    "type": <STRING:type_of_event>,
    "command_id": <STRING:id_of_initial_command>,
    "version": <INT:aggregate_version>,
    "payload": <K,V:event_specific_key, event_specific_value>
}
```
