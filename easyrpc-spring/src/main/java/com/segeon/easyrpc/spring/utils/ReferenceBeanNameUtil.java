package com.segeon.easyrpc.spring.utils;

import com.segeon.easyrpc.core.domain.entity.ReferenceConfig;
import com.segeon.easyrpc.core.utils.StringUtils;

public class ReferenceBeanNameUtil {
    public static final String PREFIX = "ez.r.";

    public static String beanName(ReferenceConfig referenceConfig) {
        String name = referenceConfig.getInterfaceType().getCanonicalName();
        int groupLength = StringUtils.hasText(referenceConfig.getGroup()) ? referenceConfig.getGroup().length() + 1 : 0;
        int versionLength = StringUtils.hasText(referenceConfig.getVersion()) ? referenceConfig.getVersion().length() + 1 : 0;
        int urlLength = StringUtils.hasText(referenceConfig.getUrl()) ? referenceConfig.getUrl().length() + 1 : 0;
        int length = PREFIX.length() + name.length() +  groupLength + versionLength + urlLength;
        StringBuilder builder = new StringBuilder(length + 32);
        builder.append(PREFIX).append(name);
        if (StringUtils.hasText(referenceConfig.getGroup())) {
            builder.append(".").append(referenceConfig.getGroup());
        }
        if (StringUtils.hasText(referenceConfig.getVersion())) {
            builder.append(".").append(referenceConfig.getVersion());
        }
        if (StringUtils.hasText(referenceConfig.getUrl())) {
            builder.append(".").append(referenceConfig.getUrl());
        }
        return builder.toString();
    }
}
