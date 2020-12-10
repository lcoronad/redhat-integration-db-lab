package co.com.redhat.integration.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import co.com.redhat.integration.dto.RequestSaveEmployees;
import co.com.redhat.integration.dto.ResponseSaveEmployees;

/**
 * Ruta principal que expone el servicio rest.
 * 
 * @author Lazaro Miguel Coronado Torres
 * @since 10/12/2020
 * @version 1.0
 */
@Component
public class RestConfigurationRoute extends RouteBuilder {
	/**
	 * Inyecta las propiedades
	 */
    @Autowired
    private Environment env;

    @Override
    public void configure() throws Exception {
    	//Configuracion del servicio rest
    	restConfiguration()
	        .component("servlet")
	        .bindingMode(RestBindingMode.json)
	        .skipBindingOnErrorCode(false)
	        .dataFormatProperty("prettyPrint", "true")
	        .apiContextPath("api-doc");
    	//Se expone el contexto del servicio rest
        rest(env.getProperty("service.rest.uri"))
            .description(env.getProperty("service.rest.description"))
            .consumes(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
       //Se expone el endpoint post para la consulta
       .post(env.getProperty("service.rest.save.uri"))
           .description(env.getProperty("service.rest.save.description"))
           .type(RequestSaveEmployees.class)
           .outType(ResponseSaveEmployees.class)
           .description(env.getProperty("service.rest.save.description"))
           .responseMessage()
               .code(200)
               .message("All users successfully created")
           .endResponseMessage()
           .to("direct:save-employees")
        //Se expone el endpoint get para el health check
        .get(env.getProperty("service.rest.health.uri"))
            .description(env.getProperty("service.rest.health.description"))
            .to("direct:health");
    }
}
