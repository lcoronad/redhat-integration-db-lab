package co.com.redhat.integration.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Ruta que procesa las peticiones de health check
 * 
 * @author Lazaro Miguel Coronado Torres
 * @since 10/12/2020
 * @version 1.0
 */
@Component
public class HealthRoute extends RouteBuilder {

	/**
	 * Inyecta el logger
	 */
	@Autowired
	private Logger logger;

	/**
	 * Inyecta el camel context por defecto
	 */
	@Autowired
	private CamelContext camelContext;

	@Override
	public void configure() throws Exception {

		camelContext.setUseMDCLogging(Boolean.TRUE);
		// Ruta del health check
		from("direct:health")
			.id("health")
			.streamCaching("true")
			//Arma la respuesta constante
			.setBody().constant("OK")
				.log(LoggingLevel.DEBUG, logger, "Response Health Check: ${body}")
		.end();
	}
}
