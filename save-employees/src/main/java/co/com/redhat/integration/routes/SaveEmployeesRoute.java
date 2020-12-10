package co.com.redhat.integration.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.stereotype.Component;

import co.com.redhat.integration.beans.ResponseHandler;
import co.com.redhat.integration.dto.RequestSaveEmployees;

/**
 * Ruta que procesa la insercion de empleados.
 * 
 * @author Lazaro Miguel Coronado Torres
 * @since 10/12/2020
 * @version 1.0
 */
@Component
public class SaveEmployeesRoute extends RouteBuilder {
	/**
	 * Inyecta el logger
	 */
    @Autowired
    private Logger logger;
    
    /**
     * Inyecta el camelContex por defecto
     */
    @Autowired
    private CamelContext camelContext;
    
    /**
     * Crea el dataformat para el request
     */
    private JacksonDataFormat jsonDataFormat = new JacksonDataFormat(RequestSaveEmployees.class);
    //private JacksonDataFormat jsonDataFormatBeanValidationException = new JacksonDataFormat(BeanValidationException.class);

    @Override
    public void configure() throws Exception {
        camelContext.setUseMDCLogging(Boolean.TRUE);
        
        //Manejo de error de validacion de campos
        onException(BeanValidationException.class)
	        .handled(true)
	    	.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
	        .log(LoggingLevel.ERROR, log, "Error Field Validator: ${exception.message} \n \n")
	        .bean(ResponseHandler.class, "buildResponseValidation(${exception})");
        
        //Manejo de error de JDBC
        onException(CannotGetJdbcConnectionException.class)
	        .handled(true)
	    	.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
	        .log(LoggingLevel.ERROR, log, "Error: ${exception.message} \n \n")
	        .bean(ResponseHandler.class, "buildResponseErrorDB(${exception})");
        
        //Inicio de la ruta de consulta de empleados
        from("direct:save-employees")
        	.id("save-employees")
        	.streamCaching("true")
        		.log(LoggingLevel.INFO, logger, " | Message: mensaje que llega ${body}")
        	//Se convierte el request a JSON
        	.marshal(jsonDataFormat)
        		.log(LoggingLevel.INFO, logger, " | Message: Payload De Entrada: ${body}")
        	//Se convierte el JSON a objeto
        	.unmarshal(jsonDataFormat)
        	//Se validan los campos de entrada segun las anotacion del objeto request
        	.to("bean-validator://validatorFields")
        		.log(LoggingLevel.INFO, logger , " | Message: se invoca la consulta SQL con los valores Nombre: ${body.nombre} - Cedula: ${body.cedula} y salario: ${body.salario}")
        	//Se ejecuta la sentencia SQL
        	.to("sql:insert into empleados values(:#${body.nombre}, :#${body.cedula}, :#${body.salario})?dataSource=#dataSourceEmpleados")
        		.log(LoggingLevel.INFO, logger , "respuesta bd : ${body}")
        	//Se arma la respuesta
        	.bean(ResponseHandler.class, "response(${exchange})")
        		.log(LoggingLevel.INFO, logger , " | Message: Response : ${body}")
        .end();
    }
}
