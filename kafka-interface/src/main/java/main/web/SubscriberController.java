package main.web;

import main.kafka.Messaging.MessageConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;


/**
 * Controller which accepts /subscribe and /unsubscribe requests
 * to subscribe/unsubscribe services to a given topic.
 */
@RestController
public class SubscriberController {

    @Autowired
    SubscriberService subscriberService;

    /**
     * Subscribes a single KafkaConsumer to the given topic on on an ExecutorService.
     * @param topic Kakfa topic to subscribe to.
     * @param endpoint HTTP callback endpoint to post consumed messages to.
     * @return A ResponseEntity with a status code to indicate success, URI for the created resource.
     */
    @RequestMapping(value = "/subscribers", method = RequestMethod.PUT)
    public ResponseEntity createSubscriber(@RequestParam(value="topic", defaultValue="none") String topic,
                                           @RequestParam(value="endpoint", defaultValue="none") String endpoint,
                                           UriComponentsBuilder ucBuilder){

        if(topic.equals("none") || (!endpoint.startsWith("http://")))
            return new ResponseEntity<>("Bad request: " + topic + ", " + endpoint, HttpStatus.BAD_REQUEST);

        MessageConsumer consumer = new MessageConsumer(topic, endpoint);
        if(subscriberService.isConsumerExist(consumer)) {
            consumer.shutdown();
            return new ResponseEntity<>("Already exists: " + topic + ", " + endpoint, HttpStatus.CONFLICT);
        }
        System.out.println(consumer);
        subscriberService.add(consumer);
        Application.executorService.execute(consumer);
        System.out.println("after executing");
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/subscribers/{id}").buildAndExpand(consumer.getId()).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    /**
     * Closes the specified consumer.
     * @param id unique id for the consumer
     * @return A ResponseEntity with a status code to indicate success.
     */
    @RequestMapping(value = "/subscribers/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<MessageConsumer> deleteSubscribers(@PathVariable("id") UUID id){

        MessageConsumer consumer = subscriberService.findById(id);
        if(consumer == null) {
            System.out.println("Unable to delete. Subscriber with id " + id + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        consumer.shutdown();
        subscriberService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
