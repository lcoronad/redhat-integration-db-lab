package co.com.redhat.integration;

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

import co.com.redhat.integration.dto.RequestSaveEmployees;
import co.com.redhat.integration.dto.ResponseSaveEmployees;

/**
 * Test unitario para probar los endpoint.
 * 
 * @author Lazaro Miguel Coronado Torres
 * @since 10/12/2020
 * @version 1.0
 */
@RunWith(SpringRunner.class)
@Configuration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = { "server.port=8080" })
public class ApplicationTest {
	/**
	 * Define el puerto por donde suben las pruebas
	 */
	@Value("${server.port}")
	private String serverPort;
	
	/**
	 * Crea el restTemplate para invocar los endpoints
	 */
	@Autowired
	private TestRestTemplate restTemplate;
	
	/**
	 * Crea el log
	 */
	private Logger log = LoggerFactory.getLogger(ApplicationTest.class);
	
	/**
	 * Prueba el correcto funcionamiento de la ruta,.
	 * 
	 * @throws Exception
	 */
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
	
	/**
	 * Prueba la validación de los campos.
	 * 
	 * @throws Exception
	 */
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

}