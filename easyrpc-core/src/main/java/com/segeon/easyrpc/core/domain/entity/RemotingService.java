package com.segeon.easyrpc.core.domain.entity;

/**
 * 代表Provider侧提供的一个服务。interfaceType的名称，group, version三者共同确定唯一一个服务
 */
public interface RemotingService {

    /**
     * 服务的接口
     * @return
     */
    Class<?> interfaceType();

    /**
     * 具体实现上面接口的实例
     * @return
     */
    Object target();

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

    static String genServiceKey(RemotingService service) {
        return genServiceKey(service.interfaceType().getCanonicalName(), service.group(), service.version());
    }

    static String genServiceKey(String interfaceName, String group, String version) {
        StringBuilder builder = new StringBuilder(128);
        builder.append(interfaceName).append(":").append(group).append(":").append(version);
        return builder.toString();
    }
}
