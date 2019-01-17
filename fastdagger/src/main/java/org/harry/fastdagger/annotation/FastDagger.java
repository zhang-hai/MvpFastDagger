package org.harry.fastdagger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by harry on 2019/1/10.
 * function：快速生成Dagger中的Module文件和Component文件
 * 仅生成module文件和component文件，现已废弃，不在使用
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
@Deprecated
public @interface FastDagger {

    //IModel接口类
    Class<?> modelCls();

    //IView接口类
    Class<?> viewCls();

    //activity类或Fragment类
    Class<?> activityCls();

    //作用域范围类
    Class<?> scopeCls();

    /**
     * A list of types that are to be used as <a href="#component-dependencies">component
     * dependencies</a>.
     */
    Class<?>[] dependencies() default {};
}
