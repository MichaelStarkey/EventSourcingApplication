package main.mongo.config;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configs {
    private final Properties prop;

    public Configs(){
        prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("mongoConfig.properties");
            prop.load(input);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String address(){
        return this.prop.getProperty("dbAddress");
    }

    public int port(){
        return Integer.parseInt(this.prop.getProperty("dbPort"));
    }
}
