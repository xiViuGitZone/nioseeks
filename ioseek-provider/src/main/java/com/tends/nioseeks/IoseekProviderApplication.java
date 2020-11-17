package com.tends.nioseeks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient

//@EnableElasticsearchRepositories(value = "com.tends.nioseeks.elasticsearch.mapper")
public class IoseekProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(IoseekProviderApplication.class, args);
    }

}
