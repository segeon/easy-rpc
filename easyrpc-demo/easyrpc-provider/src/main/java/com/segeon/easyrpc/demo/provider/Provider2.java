package com.segeon.easyrpc.demo.provider;

import com.segeon.easyrpc.core.domain.entity.ApplicationConfig;
import com.segeon.easyrpc.core.domain.entity.RegistryConfig;
import com.segeon.easyrpc.core.domain.entity.ServiceConfig;
import com.segeon.easyrpc.demo.facade.HelloService;

public class Provider2 {

    public static void main(String[] args) {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("demo-provider");
        applicationConfig.setPort(1123);
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setSchema("etcd");
        registryConfig.setIp("localhost");
        registryConfig.setPort(2379);
        applicationConfig.setRegistryConfig(registryConfig);
        applicationConfig.init();
        ServiceConfig serviceConfig = new ServiceConfig(applicationConfig);
        serviceConfig.setInterfaceType(HelloService.class);
        serviceConfig.setRefImpl(new HelloServiceImpl());
        serviceConfig.export();
    }
}
