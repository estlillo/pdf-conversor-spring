package cl.barbatos.pdfconversor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenAPIConfig {

  @Bean
  public OpenAPI myOpenAPI() {

    Contact contact = new Contact();
    contact.setEmail("barbatosdev@gmail.com");
    contact.setName("barbatos");
    contact.setUrl("https://barbatos-dev.com");

    License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

    Info info = new Info()
        .title("PDF-CONVERSOR-SERVICE API")
        .version("0.0.3")
        .contact(contact)
        .description("Convertidor de HTML a PDF").termsOfService("https://barbatos-dev.com")
        .license(mitLicense);

    return new OpenAPI().info(info);
  }
}
