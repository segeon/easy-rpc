package com.segeon.easyrpc.spring.annotation;

import java.lang.annotation.*;

/**
 * 加在提供远程服务的Bean上
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE})
public @interface RpcService {

    Class<?> interfaceType();

    String group() default "";

    String version() default "";
}
