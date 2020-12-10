package co.com.redhat.integration.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clase para configurar el manejo de los logs.
 * 
 * @author Lazaro Miguel Coronado Torres
 * @since 10/12/2020
 * @version 1.0
 */
@Configuration
public class LoggerConfig {

    @Value("${camel.springboot.name}")
    private String loggerName;

    @Bean
    public Logger logger() {
        return LoggerFactory.getLogger(loggerName);
    }

}
