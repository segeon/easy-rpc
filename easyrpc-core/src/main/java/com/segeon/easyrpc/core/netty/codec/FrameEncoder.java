package com.segeon.easyrpc.core.netty.codec;

import io.netty.handler.codec.LengthFieldPrepender;

public class FrameEncoder extends LengthFieldPrepender {
    public static final String NAME = "frameEncoder";

    public FrameEncoder() {
        super(4, 0, true);
    }
}
