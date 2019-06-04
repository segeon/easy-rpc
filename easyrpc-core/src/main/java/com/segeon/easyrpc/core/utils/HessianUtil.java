package com.segeon.easyrpc.core.utils;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class HessianUtil {

    public static ByteBuf serialize(Object o) throws IOException {
        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.heapBuffer(128);
        ByteBufOutputStream stream = new ByteBufOutputStream(buffer);
        Hessian2Output hessian2Output = new Hessian2Output(stream);
        hessian2Output.startMessage();
        hessian2Output.writeObject(o);
        hessian2Output.completeMessage();
        hessian2Output.close();
        stream.flush();
        stream.close();
        return buffer;
    }

    public static Object deserialize(byte[] bytes) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Hessian2Input hessian2Input = new Hessian2Input(is);
        hessian2Input.startMessage();
        Object o = hessian2Input.readObject();
        hessian2Input.completeMessage();
        hessian2Input.close();
        is.close();
        return o;
    }
}
