package com.abnormality.abnormalityaccept.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Level {
    int[] allowLevel() default {};
    int minLevel() default -1;
    int maxLevel() default -1;

}
