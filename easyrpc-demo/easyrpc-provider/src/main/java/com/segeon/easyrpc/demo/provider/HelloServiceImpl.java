package com.segeon.easyrpc.demo.provider;

import com.segeon.easyrpc.demo.facade.HelloRequest;
import com.segeon.easyrpc.demo.facade.HelloResponse;
import com.segeon.easyrpc.demo.facade.HelloService;
import com.segeon.easyrpc.spring.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RpcService(interfaceType = HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public HelloResponse say(HelloRequest r) {
        log.info("received: {}", r);
        HelloResponse helloResponse = new HelloResponse();
        helloResponse.setReply("ok");
        helloResponse.setReplyTime(System.currentTimeMillis());
        //throw new RuntimeException("fake error");
        return helloResponse;
    }
}
