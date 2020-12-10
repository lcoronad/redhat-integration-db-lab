package co.com.redhat.integration.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Request del endpoint de guardar empleados.
 * 
 * @author Lazaro Miguel Coronado Torres
 * @since 10/12/2020
 * @version 1.0
 */
@XmlRootElement
@JsonAutoDetect
@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestSaveEmployees {

    @JsonProperty
    @NotEmpty(message = "El nombre no puede ser vacio")
    private String nombre;
    
    @JsonProperty
    @NotEmpty(message = "La cedula no puede ser vacio")
    private String cedula;
    
    @JsonProperty
    private int salario;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public int getSalario() {
		return salario;
	}

	public void setSalario(int salario) {
		this.salario = salario;
	}
}
