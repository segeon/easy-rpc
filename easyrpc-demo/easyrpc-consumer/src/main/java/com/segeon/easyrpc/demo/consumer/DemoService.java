package com.segeon.easyrpc.demo.consumer;

import com.segeon.easyrpc.demo.facade.HelloRequest;
import com.segeon.easyrpc.demo.facade.HelloResponse;
import com.segeon.easyrpc.demo.facade.HelloService;
import com.segeon.easyrpc.spring.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DemoService {
    @RpcReference
    private HelloService helloService;

    public String sayHello(String msg) {
        HelloRequest helloRequest = new HelloRequest();
        helloRequest.setFrom("thh");
        helloRequest.setData(msg);
        helloRequest.setTime(System.currentTimeMillis());
        HelloResponse response = helloService.say(helloRequest);
        log.info("response: {}", response.getReply());
        return response.getReply();
    }
}
