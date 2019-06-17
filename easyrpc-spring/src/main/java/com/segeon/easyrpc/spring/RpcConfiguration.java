package com.segeon.easyrpc.spring;

import com.segeon.easyrpc.spring.processor.RpcReferenceBeanPostProcessor;
import com.segeon.easyrpc.spring.processor.RpcServiceBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcConfiguration {

    @Bean
    public static RpcServiceBeanPostProcessor rpcServiceBeanPostProcessor() {
        return new RpcServiceBeanPostProcessor();
    }

    @Bean
    public static RpcReferenceBeanPostProcessor rpcReferenceBeanPostProcessor() {
        return new RpcReferenceBeanPostProcessor();
    }
}
