package com.segeon.easyrpc.demo.provider;

import com.segeon.easyrpc.core.domain.entity.ApplicationConfig;
import com.segeon.easyrpc.core.domain.entity.RegistryConfig;
import com.segeon.easyrpc.core.domain.entity.ServiceConfig;
import com.segeon.easyrpc.demo.facade.HelloService;

public class Provider {

    public static void main(String[] args) {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("demo-provider");
        applicationConfig.setPort(1121);
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setIp("localhost");
        registryConfig.setPort(1121);
        registryConfig.setSchema("mock");
        applicationConfig.setRegistryConfig(registryConfig);
        applicationConfig.init();
        ServiceConfig serviceConfig = new ServiceConfig(applicationConfig);
        serviceConfig.setInterfaceType(HelloService.class);
        serviceConfig.setRefImpl(new HelloServiceImpl());
        serviceConfig.export();
    }
}
