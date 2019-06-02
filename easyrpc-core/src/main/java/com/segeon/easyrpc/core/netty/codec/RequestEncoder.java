package com.segeon.easyrpc.core.netty.codec;

import com.caucho.hessian.io.Hessian2Output;
import com.segeon.easyrpc.core.domain.entity.RPCRequest;
import com.segeon.easyrpc.core.domain.value.Consts;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestEncoder extends MessageToByteEncoder<RPCRequest> {
    public static final String NAME = "requestEncoder";

    @Override
    protected void encode(ChannelHandlerContext ctx, RPCRequest msg, ByteBuf out) throws Exception {
        out.writeInt(Consts.MAGIC_NUMBER);
        ByteBufOutputStream stream = new ByteBufOutputStream(out);
        try {
            Hessian2Output hessian2Output = new Hessian2Output(stream);
            hessian2Output.startMessage();
            hessian2Output.writeObject(msg);
            hessian2Output.completeMessage();
            hessian2Output.close();
        } catch (Exception e) {
            log.error("序列化RPC请求失败: {}", msg, e);
        }
    }
}
