package com.segeon.easyrpc.demo.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Scanner;

@Slf4j
public class AnnotationConsumer {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.segeon.easyrpc.demo.consumer", "com.segeon.easyrpc.spring");
        DemoService demoService = context.getBean(DemoService.class);
        Scanner scanner = new Scanner(System.in);
        int i = 0;
        while (true) {
            String s = scanner.nextLine();
            if (s.equals("exit")) {
                break;
            }
            String s1 = demoService.sayHello("msg" + i);
            log.info("response:{}", s1);
            ++i;
        }
    }
}
