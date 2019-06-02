package com.segeon.easyrpc.core.utils;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    private static AtomicLong id = new AtomicLong(0);

    public static long getId() {
        return id.getAndIncrement();
    }
}
