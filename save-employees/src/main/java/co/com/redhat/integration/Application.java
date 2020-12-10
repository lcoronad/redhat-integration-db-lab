package co.com.redhat.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal que inicia la aplicación de SpringBoot
 * 
 * @author Lazaro Miguel Coronado Torres
 * @since 10/12/2020
 * @version 1.0
 */
@SpringBootApplication
@EnableAutoConfiguration
public class Application {

    /**
     * A main method to start this application.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
