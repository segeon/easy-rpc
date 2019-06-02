package com.segeon.easyrpc.core.netty.codec;

import com.caucho.hessian.io.Hessian2Input;
import com.segeon.easyrpc.core.domain.entity.RPCRequest;
import com.segeon.easyrpc.core.domain.value.Consts;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class RequestDecoder extends ByteToMessageDecoder {
    public static final String NAME = "requestDecoder";

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int m = in.readInt();
        if (m != Consts.MAGIC_NUMBER) {
            log.error("不是合法的报文, magic number异常， 即将关闭连接!");
            ReferenceCountUtil.release(in);
            ctx.close();
            return;
        }
        try {
            ByteBufInputStream is = new ByteBufInputStream(in);
            Hessian2Input hessian2Input = new Hessian2Input(is);
            hessian2Input.startMessage();
            RPCRequest o = (RPCRequest) hessian2Input.readObject();
            out.add(o);
            hessian2Input.completeMessage();
            hessian2Input.close();
            is.close();
        } catch (Exception e) {
            log.error("反序列化请求报文异常!", e);
        }
    }
}
