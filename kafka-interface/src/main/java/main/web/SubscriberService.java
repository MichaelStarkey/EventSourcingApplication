package main.web;

import main.kafka.Messaging.MessageConsumer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service("subscriberService")
public class SubscriberService {

    private static final ArrayList<MessageConsumer> consumers = new ArrayList<>();

    public ArrayList<MessageConsumer> findAllConsumers(){ return consumers;}

    public void add(MessageConsumer c){
        consumers.add(c);
    }

    public MessageConsumer findById(UUID id){
        for (MessageConsumer c : consumers){
            System.out.println(c + "," + c.getId());
            if (c.getId().equals(id))
                return c;
        }
        return null;
    }

    public void deleteById(UUID id){
        consumers.removeIf(c -> c.getId() == id);
    }

    public boolean isConsumerExist(MessageConsumer consumer) {
        return findById(consumer.getId())!=null;
    }

    public void deleteAllConsumers(){
        consumers.clear();
    }
}
