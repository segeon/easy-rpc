package com.segeon.easyrpc.spring.annotation;


import java.lang.annotation.*;

/**
 * 加在引用远程服务的Bean属性上，会自动注入
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})
public @interface RpcReference {

    String group() default "";

    String version() default "";

    /**
     * 直连地址
     * @return
     */
    String url() default "";
}
