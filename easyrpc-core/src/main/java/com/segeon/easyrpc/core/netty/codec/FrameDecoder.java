package com.segeon.easyrpc.core.netty.codec;

import com.segeon.easyrpc.core.domain.value.Consts;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 *
 * | requestLength; 代表整个数据包的大小(4bytes) | magicNumber = 0x00DABBED (4bytes)  | body|
 */

public class FrameDecoder extends LengthFieldBasedFrameDecoder {
    public static final String NAME = "frameDecoder";

    public FrameDecoder() {
        super(Consts.MAX_FRAME_LENGTH, 0, 4, -4, 4, true);
    }
}
