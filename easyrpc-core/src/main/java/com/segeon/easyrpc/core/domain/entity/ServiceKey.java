package com.segeon.easyrpc.core.domain.entity;

/**
 * 服务的唯一标识
 */
public interface ServiceKey {
    /**
     * 服务的接口
     * @return
     */
    Class<?> interfaceType();

    /**
     * 服务的分组
     * @return
     */
    default String group() {
        return "";
    }

    /**
     * 服务的版本号
     * @return
     */
    default String version() {
        return "";
    }

    default String getKey() {
        return genKey(interfaceType().getCanonicalName(), group(), version());
    }

    static String genKey(String interfaceName, String group, String version) {
        StringBuilder builder = new StringBuilder(128);
        builder.append(interfaceName).append(":").append(group).append(":").append(version);
        return builder.toString();
    }
}
