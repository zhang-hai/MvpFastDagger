package org.harry.fastdagger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zhanghai on 2019/1/14.
 * function：快速生成mvp相应文件的注解,规定必须要先手动创建一个Activity 或 Fragment类,
 * 将该注解使用在类上面
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface MvpFastDagger {
    //支持生成的语言类型
    int JAVA = 1;
    int KOTLIN = 2;

    /**
     * 模块的功能名称，会根据这个名字生成对应的presenter 、view、model、modelImp
     * 分别存放在package .mvp下presenter model view目录
     * @return
     */
    String name();

    /**
     * 要生成的presenter类的父类
     * @return
     */
    Class<?> basePresenterClazz() default Class.class;

    /**
     * 需要生成的view接口的父接口,是一个数组，可以继承多个其他接口
     * @return
     */
    Class<?>[] iBaseViewClazz() default {};

    /**
     * 生成的imodel类的父接口，是一数组，可以有多个接口
     * @return
     */
    Class<?>[] iBaseModelClazz() default {};

    /**
     * 生成的mode接口的实现类的父类
     * @return
     */
    Class<?> baseModelImpClazz() default Class.class;

    /**
     *
     * 此字段已经不使用了
     *
     * 生成的Activity或Fragment类的父类，必填项，作为Activity或Fragment必定有一个父类
     * @return
     */
//    Class<?> activityExtendCls();

    //作用域范围类
    Class<?> scopeClazz();


    /**
     * 依赖的module类，自身生成的module类会自动包含进去
     *
     */
    Class<?>[] modules() default {};

    /**
     * 依赖的Component类
     */
    Class<?>[] dependencies() default {};

    //设置生成的类使用的语言，默认采用JAVA
    int language() default MvpFastDagger.JAVA;
}
