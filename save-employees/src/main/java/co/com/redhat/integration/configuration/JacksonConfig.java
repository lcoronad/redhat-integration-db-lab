package co.com.redhat.integration.configuration;

import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clase para configurar el manejo de JSON.
 * 
 * @author Lazaro Miguel Coronado Torres
 * @since 10/12/2020
 * @version 1.0
 */
@Configuration
public class JacksonConfig {

    @Bean("responseJackson")
    public JacksonDataFormat createJacksonFormat() {
        JacksonDataFormat jacksonDataFormat = new JacksonDataFormat();
        jacksonDataFormat.setPrettyPrint(true);
        jacksonDataFormat.setUnmarshalType(co.com.redhat.integration.dto.ResponseSaveEmployees.class);
        jacksonDataFormat.setEnableJaxbAnnotationModule(true);

        return jacksonDataFormat;
    }

}
