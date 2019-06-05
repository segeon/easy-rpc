package com.segeon.easyrpc.demo.consumer;

import com.google.common.base.Stopwatch;
import com.segeon.easyrpc.core.domain.entity.ApplicationConfig;
import com.segeon.easyrpc.core.domain.entity.ReferenceConfig;
import com.segeon.easyrpc.core.domain.entity.RegistryConfig;
import com.segeon.easyrpc.demo.facade.HelloRequest;
import com.segeon.easyrpc.demo.facade.HelloResponse;
import com.segeon.easyrpc.demo.facade.HelloService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Consumer {
    public static void main(String[] args) throws IOException {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("demo-consumer");
        applicationConfig.setPort(1121);
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setSchema("etcd");
        registryConfig.setIp("localhost");
        registryConfig.setPort(2379);
        applicationConfig.setRegistryConfig(registryConfig);
        applicationConfig.init();
        ReferenceConfig<HelloService> referenceConfig = new ReferenceConfig(applicationConfig);
        referenceConfig.setInterfaceType(HelloService.class);
        HelloService helloService = referenceConfig.refer();
        int i = 0;
        Stopwatch stopwatch = Stopwatch.createStarted();
        int cnt = 3;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String s = scanner.nextLine();
            if (s.equals("exit")) {
                break;
            }
            HelloRequest request = new HelloRequest();
            request.setData("msg" + i);
            request.setFrom("thh");
            request.setTime(System.currentTimeMillis());
            HelloResponse response = helloService.say(request);
            log.info("response:{}", response);
            ++i;
        }
    }
}
