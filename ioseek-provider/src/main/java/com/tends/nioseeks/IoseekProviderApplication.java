package com.tends.nioseeks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class IoseekProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(IoseekProviderApplication.class, args);
    }

}
