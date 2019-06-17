package com.segeon.easyrpc.demo.provider;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AnnotationProvider {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.segeon.easyrpc.demo.provider", "com.segeon.easyrpc.spring");
    }
}
