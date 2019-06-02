package com.segeon.easyrpc.core.domain.entity;

import com.segeon.easyrpc.core.domain.value.Consts;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 数据报文前还有magicNumber(4bytes) {@link Consts#MAGIC_NUMBER}|requestSize(4bytes);
 */
@Getter
@Setter
public class RPCHeader implements Serializable {

    private long requestId;
    /**
     * 报文类型，取值参考{@link Consts#PACKET_TYPE_REQUEST}, {@link Consts#PACKET_TYPE_RESPONSE},
     */
    private byte type;
}
