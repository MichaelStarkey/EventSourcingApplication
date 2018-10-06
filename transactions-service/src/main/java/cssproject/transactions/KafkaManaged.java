package cssproject.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import cssproject.transactions.api.events.AbstractEvent;
import cssproject.transactions.api.events.ConcreteEvent;
import cssproject.transactions.api.events.TransactionEvent;
import cssproject.transactions.core.Transaction;
import cssproject.transactions.db.TransactionDAO;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.lifecycle.Managed;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

public class KafkaManaged implements Managed{

    private final TransactionDAO transactionDAO;

    public KafkaManaged(TransactionDAO transactionDAO){
        this.transactionDAO = transactionDAO;
    }

    /*
    Method will wind up the database using the kafka interface.
     */
    @Override
    @UnitOfWork
    public void start() {
        String url = "http://127.0.0.1:8080/queries/events/transactions";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>("", headers);
        String result = "";
        try {
            System.out.println("Querying for events");
            ResponseEntity<String> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, java.lang.String.class);
            result = response.getBody();
        }catch (Exception e){
            System.out.println("Failed to get events.");
        }
        if(!result.equals("")){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<ConcreteEvent> events = objectMapper.readValue(
                        result, objectMapper.getTypeFactory()
                                .constructCollectionType(List.class, ConcreteEvent.class));
                for(ConcreteEvent e : events){
                    System.out.println("applying event" + e.getType() + ", payload " + e.getPayload());
                    TransactionEvent newEvent = e.getEvent(transactionDAO);
                    System.out.println(newEvent.getType());
                    newEvent.apply();
                }
            }catch (IOException e){
                System.out.println("Error parsing JSON.");
                e.printStackTrace();
            }
        }
    }

    /*
    Shuts down the consumer it made.
     */
    @Override
    public void stop() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.delete(TransactionsApplication.consumerUri);
            System.out.println("Shut consumer down successfully");
        }catch (Exception e){
            System.out.println("Failed to shut down consumer.");
        }
    }
}
