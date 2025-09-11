package com.abnormality.abnormalityaccept.annotation;

import java.lang.annotation.*;

/**
 * 权限级别注解，用于在方法上声明访问该方法所需的权限等级。
 *
 * <p>该注解可用于定义三种权限控制规则：</p>
 * <ul>
 *   <li>{@code allowLevel}：指定允许访问的权限级别数组</li>
 *   <li>{@code minLevel}：指定允许访问的最小权限级别，默认为 -1（不启用）</li>
 *   <li>{@code maxLevel}：指定允许访问的最大权限级别，默认为 -1（不启用）</li>
 * </ul>
 *
 * <p>这些规则可以单独使用，也可以组合使用，以实现灵活的权限控制策略。</p>
 * @author s
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Level {

    /**
     * 获取允许访问的权限级别数组。
     *
     * <p>若设置此属性，表示只有用户权限级别在该数组中的用户才被允许访问目标方法。</p>
     *
     * @return 允许的权限级别数组，默认为空数组（不限定具体权限）
     */
    int[] allowLevel() default {};

    /**
     * 获取允许访问的最小权限级别。
     *
     * <p>若设置此属性（值不等于默认值 -1），表示用户权限必须大于或等于该值才能访问目标方法。</p>
     *
     * @return 最小权限级别，默认为 -1（不启用该项权限限制）
     */
    int minLevel() default -1;

    /**
     * 获取允许访问的最大权限级别。
     *
     * <p>若设置此属性（值不等于默认值 -1），表示用户权限必须小于或等于该值才能访问目标方法。</p>
     *
     * @return 最大权限级别，默认为 -1（不启用该项权限限制）
     */
    int maxLevel() default -1;
}
