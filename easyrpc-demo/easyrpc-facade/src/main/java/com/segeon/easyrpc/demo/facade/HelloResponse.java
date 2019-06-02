package com.segeon.easyrpc.demo.facade;

import lombok.Data;

import java.io.Serializable;

@Data
public class HelloResponse implements Serializable{
    private String reply;
    private long replyTime;
}
