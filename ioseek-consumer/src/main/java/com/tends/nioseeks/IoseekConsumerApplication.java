package com.tends.nioseeks;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient

@ServletComponentScan//防止 @WebListener 无效
public class IoseekConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IoseekConsumerApplication.class, args);
    }

}
