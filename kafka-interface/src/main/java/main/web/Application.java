package main.web;

import main.kafka.Messaging.MessageConsumer;
import main.kafka.Messaging.MessageProducer;
import main.mongo.MongoDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class Application {

    static MessageProducer producer;
    public static boolean running = true;
    static ExecutorService executorService;
    static MongoDB database;

    public static void main(String[] args){
        SpringApplication.run(Application.class, args);

        executorService = Executors.newFixedThreadPool(4);
        producer = new MessageProducer();
        database = new MongoDB();
        Runtime r = Runtime.getRuntime();
        r.addShutdownHook(new MyThread());
        try{
            Thread.sleep(3000);
        } catch(Exception e){e.printStackTrace();}
    }
}

class MyThread extends Thread{

    @Autowired
    SubscriberService subscriberService;

    public void run(){
        Application.database.close();
        Application.producer.close();
        for (MessageConsumer consumer : subscriberService.findAllConsumers()){
            consumer.shutdown();
        }
        subscriberService.deleteAllConsumers();
    }
}
