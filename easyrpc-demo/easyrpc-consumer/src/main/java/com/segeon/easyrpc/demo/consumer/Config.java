package com.segeon.easyrpc.demo.consumer;

import com.segeon.easyrpc.core.domain.entity.ApplicationConfig;
import com.segeon.easyrpc.core.domain.entity.RegistryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("demo-consumer");
        applicationConfig.setPort(1121);
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setSchema("etcd");
        registryConfig.setIp("localhost");
        registryConfig.setPort(2379);
        applicationConfig.setRegistryConfig(registryConfig);
        applicationConfig.init();
        return applicationConfig;
    }


}
