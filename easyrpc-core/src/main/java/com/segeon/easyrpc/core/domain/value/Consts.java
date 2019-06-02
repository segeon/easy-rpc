package com.segeon.easyrpc.core.domain.value;

public class Consts {
    public static final int MAGIC_NUMBER = 0x00DABBED;
    public static final byte PACKET_TYPE_REQUEST = 0x00;
    public static final byte PACKET_TYPE_RESPONSE = 0x01;

    public static final String REGISTRY_PREFIX = "/easyrpc-registry";

    public static final int MAX_FRAME_LENGTH = 64 * 1024 * 1024;

    public static final int DEFAULT_RPC_TIMEOUT_MILLIS = 60000;
}
