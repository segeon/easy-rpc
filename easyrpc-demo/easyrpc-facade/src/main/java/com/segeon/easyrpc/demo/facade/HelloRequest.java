package com.segeon.easyrpc.demo.facade;

import lombok.Data;

import java.io.Serializable;

@Data
public class HelloRequest implements Serializable {
    private String from;
    private String data;
    private long time;
}
