package cssproject.transactions;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

import cssproject.transactions.core.Transaction;
import cssproject.transactions.db.TransactionDAO;
import cssproject.transactions.resources.*;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.lifecycle.ServerLifecycleListener;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.h2.engine.Database;
import org.skife.jdbi.v2.DBI;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class TransactionsApplication extends Application<TransactionsConfiguration> {

    public static String consumerUri;

    public static void main(final String[] args) throws Exception {
        new TransactionsApplication().run(args);
    }

    private final HibernateBundle<TransactionsConfiguration> hibernateBundle =
            new HibernateBundle<TransactionsConfiguration>(Transaction.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(TransactionsConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };


    @Override
    public String getName() {
        return "Transactions";
    }

    @Override
    public void initialize(final Bootstrap<TransactionsConfiguration> bootstrap) {

        bootstrap.addBundle(new MigrationsBundle<TransactionsConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(TransactionsConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(final TransactionsConfiguration configuration,
                    final Environment environment) {
        environment.lifecycle().addServerLifecycleListener(new ServerLifecycleListener() {
            @Override
            public void serverStarted(Server server){
                String endpoint = "";
                for(Connector c : server.getConnectors()){
                    ServerConnector connector = (ServerConnector)c;
                    if(connector.getName().equals("application")) {
                        endpoint =  "http://127.0.0.1:" + connector.getLocalPort() + "/transactions/events/";
                    }
                }
                String url = "http://127.0.0.1:8080/subscribers";
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                        .queryParam("topic", "accounts")
                        .queryParam("endpoint", endpoint);
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<String>("", headers);
                try {
                    System.out.println("Opening a consumer.");
                    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), org.springframework.http.HttpMethod.PUT, entity, java.lang.String.class);
                    TransactionsApplication.consumerUri = response.getHeaders().getLocation().toString();
                    System.out.println("Successfully opened a consumer at " + consumerUri);
                } catch(Exception e){
                    System.out.println("Failed to connect to Kafka.");
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        });

        registerResources(environment, configuration);
    }

    private void registerResources(Environment environment, TransactionsConfiguration configuration) {

        final TransactionDAO transactionDAO = new TransactionDAO(hibernateBundle.getSessionFactory());
        environment.jersey().register(new TransactionsResource(transactionDAO));
        environment.jersey().register(new TransactionResource(transactionDAO));
        environment.jersey().register(new CommandResource(transactionDAO));
        environment.jersey().register(new EventResource(transactionDAO));

        KafkaManaged kafka = new UnitOfWorkAwareProxyFactory(hibernateBundle)
                .create(KafkaManaged.class, TransactionDAO.class, transactionDAO);

        environment.lifecycle().manage(kafka);
    }

}
