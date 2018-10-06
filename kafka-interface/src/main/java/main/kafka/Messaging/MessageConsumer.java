package main.kafka.Messaging;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

/**
 * A wrapper for KafkaConsumer which configures and launches the consumer on an ExecutorService.
 */
public class MessageConsumer implements Runnable{

    private final KafkaConsumer<String, EventMessage> consumer;

    private UUID id;
    private final String topic;
    private final String endpoint;
    private boolean running = true;

    /**
     *
     * @param topic Kafka topic to subscribe the consumer to.
     * @param endpoint Callback HTTP endpoint to post events to.
     */
    public MessageConsumer(String topic, String endpoint){

        JsonDeserializer<EventMessage> eventMessageJsonDeserializer = new JsonDeserializer<EventMessage>(EventMessage.class);
        eventMessageJsonDeserializer.addTrustedPackages("*");

        Properties props = new Properties();
        props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "json");
        props.put("enable.auto.commit", "true");
        props.put("session.timeout.ms", "30000");

        consumer = new KafkaConsumer<String, EventMessage>(props, new StringDeserializer(), eventMessageJsonDeserializer);
        this.topic = topic;
        this.endpoint = endpoint;
        this.id = UUID.randomUUID();
    }

    /**
     * Multithreaded task to subscribe the KafkaConsumer and open a poll loop.
     */
    public void run(){

        consumer.subscribe(Arrays.asList(topic));
        try {
            while (running) {
                ConsumerRecords<String, EventMessage> records = consumer.poll(100);
                for (ConsumerRecord<String, EventMessage> record : records) {
                    RestTemplate restTemplate = new RestTemplate();
                    HttpEntity<EventMessage> requestEntity = new HttpEntity<EventMessage>(record.value());
                    System.out.println("****************** posting " + requestEntity.getBody().getType());
                    try {
                        ResponseEntity<String> response = restTemplate
                                .exchange(endpoint, HttpMethod.POST, requestEntity, java.lang.String.class);
                    } catch(Exception e){
                        System.out.println("Request failed.");
                        e.printStackTrace();
                        continue;
                    }
                }
            }
        } finally {
            System.out.println("Closing consumer.");
            consumer.close();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
    public String getTopic() {
        return topic;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void shutdown(){
        running = false;
    }
}
