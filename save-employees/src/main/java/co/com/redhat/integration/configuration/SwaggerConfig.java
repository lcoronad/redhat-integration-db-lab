package co.com.redhat.integration.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Clase para configurar el Swagger
 * 
 * @author Lazaro Miguel Coronado Torres
 * @since 10/12/2020
 * @version 1.0
 */
@Configuration
public class SwaggerConfig {

    // Redirect to access swagger UI via short URL from "/swagger-ui" to
    // "/swagger-ui/index.html?url=/api/swagger&validatorUrl="
    @Controller
    class SwaggerWelcome {
        @RequestMapping("/swagger-ui")
        public String redirectToUi() {
            return "redirect:/webjars/swagger-ui/index.html?url=/api/api-doc&validatorUrl=";
        }
    }

}
