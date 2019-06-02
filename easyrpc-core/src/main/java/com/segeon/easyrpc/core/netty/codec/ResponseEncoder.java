package com.segeon.easyrpc.core.netty.codec;

import com.caucho.hessian.io.Hessian2Output;
import com.segeon.easyrpc.core.domain.entity.RPCResponse;
import com.segeon.easyrpc.core.domain.value.Consts;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResponseEncoder extends MessageToByteEncoder<RPCResponse> {
    public static final String NAME = "responseEncoder";

    @Override
    protected void encode(ChannelHandlerContext ctx, RPCResponse msg, ByteBuf out) throws Exception {
        out.writeInt(Consts.MAGIC_NUMBER);
        try {
            ByteBufOutputStream stream = new ByteBufOutputStream(out);
            Hessian2Output hessian2Output = new Hessian2Output(stream);
            hessian2Output.startMessage();
            hessian2Output.writeObject(msg);
            hessian2Output.completeMessage();
            hessian2Output.close();
            stream.close();
        } catch (Exception e) {
            log.error("序列化响应报文异常: {}", msg, e);
        }
    }
}
