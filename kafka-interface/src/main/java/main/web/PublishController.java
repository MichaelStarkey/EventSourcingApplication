package main.web;

import main.kafka.Messaging.EventMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller which accepts /publish requests to
 * allow services to write events to Kafka.
 */
@RestController
public class PublishController {


    /**
     * Writes the given data as  a message onto the given Kafka topic and onto MongoDB.
     * @param message Request body format which contains a topic and data to write to the topic.
     * @return A ResponseEntity with a status code to indicate success.
     */
    @RequestMapping("/publish")
    public ResponseEntity publish(@RequestBody EventMessage message){
        System.out.println("************** just got " + message.getAggregate() + message.getPayload() + message.getType());
        if(message.getAggregate().equals("none")) return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);

//        Writes to MongoDB.
        Application.database.write(message);
        Application.producer.send(message);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
