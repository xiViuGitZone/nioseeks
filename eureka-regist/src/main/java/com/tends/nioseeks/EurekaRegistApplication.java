package com.tends.nioseeks;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaRegistApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaRegistApplication.class, args);
    }

}
