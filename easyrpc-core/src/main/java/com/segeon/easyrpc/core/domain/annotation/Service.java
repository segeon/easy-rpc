package com.segeon.easyrpc.core.domain.annotation;

import java.lang.annotation.*;

/**
 * 加在提供远程服务的Bean上
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE})
public @interface Service {

    Class<?> interfaceType();

    String group();

    String version();
}
