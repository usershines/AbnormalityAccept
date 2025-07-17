package com.abnormality.abnormalityaccept.annotation;

import java.lang.annotation.*;

/**
 * 认证忽略注解，用于标记某个方法不需要进行 Token 认证。
 *
 * <p>该注解可用于 Controller 层的方法上，表示该接口对外公开，无需登录即可访问。
 * 通常用于登录、注册或某些开放接口。</p>
 *
 * <p>通过设置 value 属性可以控制是否真正忽略认证，默认值为 true。</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthIgnore {

    /**
     * 是否忽略 Token 认证。
     *
     * <p>若值为 true（默认），则在认证切面中将跳过该方法的 Token 验证；
     * 若值为 false，则仍需进行认证。</p>
     *
     * @return boolean 表示是否忽略认证，默认为 true
     */
    boolean value() default true;
}
