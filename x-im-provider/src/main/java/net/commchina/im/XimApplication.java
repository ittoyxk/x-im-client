package net.commchina.im;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author xiaokang
 */
@EnableFeignClients(basePackages = "net.commchina")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "net.commchina")
public class XimApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(XimApplication.class, args);
    }

}

