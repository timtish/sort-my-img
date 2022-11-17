package ru.timtish.vyazanie;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "ImgCollection", version = "1.0", description = "Collection"))
public class VyazanieWebApp {

    public static void main(String[] args) {
        SpringApplication.run(VyazanieWebApp.class, args);
    }

}
