package co.com.redhat.integration.configuration;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clase que configura el datasource para conectarse a la base de datos H2 embebida.
 * 
 * @author Lazaro Miguel Coronado Torres
 * @since 10/12/2020
 * @version 1.0
 */
@Configuration
public class DatabaseConnectionConfig {
    
	/**
	 * Crea el Bean que crea el datasource basado en las propiedades
	 * 
	 * @return Datasource creado
	 */
    @Bean(name="dataSourceEmpleados")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSourceEmployees() {
        return DataSourceBuilder.create().type(BasicDataSource.class).build();
    }
}
