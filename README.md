# redhat-integration-db-lab

## Descripción
Ruta camel que inserta un registro de un empleado en una base de datos H2 embebida.


## URL

```
http://localhost:8080/api/employees/save-employees
```

## Request

```JSON
{
    "nombre":"Lazaro Miguel Coronado Torres",
    "cedula": "79541235",
    "salario": "5500000"
}
```



## Response

```JSON
{
    "codigo": "200",
    "descripcion": "Ok"
}
```

## Pasos para implementar la ruta

A continuación se describen los pasos para realizar implementación de la ruta y ejecución local, una vez se haya clonado este repositorio

> importar el proyecto en el IDE de su preferencia, preferible en Red Hat Code Ready Studio

> Crear la clase RequestSaveEmployees en el paquete co.com.redhat.integration.dto

> Incluir las siguientes anotaciones a nivel de la clase

```
@JsonAutoDetect
@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_NULL)
```

> Crear los atributos nombre, cedula y salario como se muestra a continuación.

```
@JsonProperty
@NotEmpty(message = "El nombre no puede ser vacio")
private String nombre;
```

```
@JsonProperty
@NotEmpty(message = "La cedula no puede ser vacio")
private String cedula;
```

```
@JsonProperty
private int salario;
```

> Cree los getters y setters de los tres (3) atributos

> Crear la clase ResponseSaveEmployees en el paquete co.com.redhat.integration.dto

> Incluir las siguientes anotaciones a nivel de la clase

```
@JsonAutoDetect
@JsonSerialize
@ApiModel(description = "Response DTO Object")
@JsonInclude(JsonInclude.Include.NON_NULL)
```

> Crear los atributos codigo y descripción como se muestra a continuación.

```
@JsonProperty
@ApiModelProperty(dataType = "String")
private String codigo;
```

```
@JsonProperty
@ApiModelProperty(dataType = "String"  , required = true )
@NotEmpty
private String descripcion;
```

> Cree los getters y setters de los dos (2) atributos


> Cree la clase RestConfigurationRoute en el paquete co.com.redhat.integration.routes

> Extienda la clase de org.apache.camel.builder.RouteBuilder y anotela con @Component

```
@Component
public class RestConfigurationRoute extends RouteBuilder {
```

> Inyecte las propiedades

```
@Autowired
private Environment env;
```

> Cree el siguiente método

```
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
```

> Verifique que tenga los siguientes imports

```
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import co.com.redhat.integration.dto.RequestSaveEmployees;
import co.com.redhat.integration.dto.ResponseSaveEmployees;
```

> Cree la clase SaveEmployeesRoute en el paquete co.com.redhat.integration.routes

> Extienda la clase de org.apache.camel.builder.RouteBuilder y anotela con @Component

```
@Component
public class SaveEmployeesRoute extends RouteBuilder {
```

> Inyecte los siguientes atributos

```
@Autowired
private Logger logger;
    
@Autowired
private CamelContext camelContext;
    
private JacksonDataFormat jsonDataFormat = new JacksonDataFormat(RequestSaveEmployees.class);
```

> Cree el siguiente método

```
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
```

> Verifique que tenga los siguientes imports

```
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
```

> Cree la clase HealthRoute en el paquete co.com.redhat.integration.routes

> Extienda la clase de org.apache.camel.builder.RouteBuilder y anotela con @Component

```
@Component
public class HealthRoute extends RouteBuilder {
```

> Inyecte los siguientes atributos

```
@Autowired
private Logger logger;

@Autowired
private CamelContext camelContext;
```

> Cree el siguiente método

```
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
```

> Verifique que tenga los siguientes imports

```
import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
```

## Test unitarios

> Cree la clase ApplicationTest en el paquete co.com.redhat.integration del source de test

> Anotela con las siguientes anotaciones.

```
@RunWith(SpringRunner.class)
@Configuration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = { "server.port=8080" })
```

> Inyecte los siguientes atributos

```
@Value("${server.port}")
private String serverPort;

@Autowired
private TestRestTemplate restTemplate;

private Logger log = LoggerFactory.getLogger(ApplicationTest.class);
```

> Cree los siguientes métodos

```
	@Test
	public void testConsultaEmpleados() throws Exception {
		//Se arma el request
		RequestSaveEmployees request = new RequestSaveEmployees();
		request.setNombre("SER71894");
		request.setCedula("722222");
		request.setSalario(111111);
		//Se consume el servicio expuesto
		ResponseEntity<ResponseSaveEmployees> response = restTemplate.postForEntity(
				"http://localhost:8080/api/employees/save-employees", request, ResponseSaveEmployees.class);
		//Log de la respuesta
		log.info("testConsultaEmpleados response {}", response);
		//Verificación de la respuesta que debe ser codigo 200
		assertThat(response.getBody().getCodigo().equals("200")).isTrue();
	}
```

```
	@Test
	public void testValidarCampos() throws Exception {
		//Se arma el request
		RequestSaveEmployees request = new RequestSaveEmployees();
		request.setNombre("");
		request.setCedula("");
		request.setSalario(111111);
		//Se consume el servicio expuesto
		ResponseEntity<ResponseSaveEmployees> response = restTemplate.postForEntity(
				"http://localhost:8080/api/employees/save-employees", request, ResponseSaveEmployees.class);
		//Log de la respuesta
		log.info("testValidarCampos response {}", response);
		//Verificación de la respuesta que debe ser codigo 400
		assertThat(response.getBody().getCodigo().equals("400")).isTrue();
	}
```

> Tenga en cuenta que se deben tener los siguientes imports

```
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
```

## Compilar

> Compile el proyecto y genere el artefacto con el siguiente comando ubicandose dentro de la carpeta save-employees

```
cd save-employees
```

```
mvn clean install -DskipTests=true
```

## Ejecutar pruebas unitarias

> Ejecute las pruebas unitarias

```
mvn clean test
```

> Verifique se ejecuten exitosamente

## Iniciar la aplicación

> Inicie la aplicación

```
mvn clean package -DskipTests=true spring-boot:run
```

## Consumir el servicio

> Consuma el servicio con su aplicación de preferencia o ejecute el siguiente comando

```
curl --location --request POST 'http://localhost:8080/api/employees/save-employees' \
--header 'Content-Type: application/json' \
--data-raw '{
    "nombre": "Nombre a insertar",
    "cedula": "5252522",
    "salario": "1222222"
}'
```

## Verificar la inserción

> Verifique que se haya insertado la información entrando a la siguiente URL sin bajar la aplicación

```
http://localhost:8080/h2-console/
```

> Haga clic en connect y realice la siguiente consulta

```
select * from empleados;
```

## Author

* **Lázaro Miguel Coronado Torres** - *Middleware Senior Consultant - lcoronad@redhat.com* 
