package main.kafka.Messaging;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Properties;

/**
 * Wrapper for Kafka Producer class which sets up configs and overloads send() to accept a Message.
 */
public class MessageProducer {

    final KafkaProducer<String, EventMessage> producer;

    public MessageProducer(){

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", JsonSerializer.class);

        producer = new KafkaProducer<>(props);
    }

    /**
     * Publishes the given message to the topic the Producer is producing to.
     * @param m Message to publish.
     */
    public void send(EventMessage m){
        producer.send(new ProducerRecord<String, EventMessage>(
                m.getAggregate(),
                m.getCommand_id(),
                m
        ));
    }

    public void close(){
        System.out.println("Closing producer.");
        producer.close();
    }
}
