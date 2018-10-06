# CSS Team Project

### What is this?
This is the CSS eventsourcing team project. It is a collection of four services (and some utility programs) that demonstrate some of the advantages of event sourcing. You can find out a bit more about each service by reading their respective readmes.

### How to run  
Requirements:
* [Docker](https://www.docker.com/community-edition)
* [Confluent Kafka](https://www.confluent.io/) on port `9092`
  *  with auto topic creation enabled.
* [MongoDB](https://www.mongodb.com/) on port `27017`

If you havnt already, you'll need to build our java services:  
In the kafka-interface directory: `./gradlew build`  
In the transactions-service directory: `mvn package`  

Then its just using docker to build and run:  
```
docker build -t interface-service interface-service/  
docker build -t kafka-interface kafka-interface/  
docker build -t transactions-service transactions-service/  
docker build -t accountService accountService/  

docker run --net=host -d -p 8080:8080 kafka-interface  
docker run --net=host -d -p 3000:3000 interface-service  
docker run --net=host -d -p 3030:3030 transactions-service  
docker run --net=host -d -p 5000:5000 accountService
```

### How it works
Our platform all stems from the showing off the three main advantages of event-sourcing that Martin Fowler originally stated in his essay:
* Provides a single source of truth  
* Allows for state to be rebuilt through replaying
* Allows for state to be rolled back to see what that it looked like previously

#### Single Source of Truth
Our platform stores every event in a mongoDB database eventStore, within the collection called events. These events can be queried for and viewed (more detail in the [kafka-interface directory](/kafka-interface/)).

#### Replayability
Utilising the previous feature, our services will query for all events in the event-store from their out-going topic that are of a version greater than their current, and replay them to recreate their state.  
**Snapshotting**  
The accounts service demonstrates a naive kind of snapshotting, whereby after a certain number of events, the service will create a json structure of its state and post it to the kafka-interface, which will store this in a new collection called snapshots. During replay, the accounts service will look for a recent snapshot before looking for events.

#### Rollbacks
The last feature allows for new branches to be created from a given timestamp. Allowing the services to rebuild off of a new branch will show what the system state was at that time. New events can be built off of that state to see what new paths the system can take. No data is replicated for the branching, as it uses a simplistic datastructure to enhance the select query during replay, only returning events from that branch.
