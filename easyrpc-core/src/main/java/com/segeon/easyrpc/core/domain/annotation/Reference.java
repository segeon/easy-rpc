package com.segeon.easyrpc.core.domain.annotation;


import java.lang.annotation.*;

/**
 * 加在引用远程服务的Bean属性上，会自动注入
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})
public @interface Reference {

    String group();

    String version();
}
