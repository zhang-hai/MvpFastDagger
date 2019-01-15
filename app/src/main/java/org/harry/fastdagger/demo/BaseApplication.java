package org.harry.fastdagger.demo;

import android.app.Application;
import android.content.Context;

import org.harry.fastdagger.demo.di.component.AppComponent;
import org.harry.fastdagger.demo.di.component.DaggerAppComponent;
import org.harry.fastdagger.demo.di.module.AppModule;

/**
 * Created by zhanghai on 2019/1/11.
 * function：
 */
//@MvpFastDagger(name = "login.login",presenterExtendCls = BasePresenter.class,activityExtendCls = AppCompatActivity.class)
public class BaseApplication extends Application {
    private static BaseApplication mApplication;
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        mAppComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))//提供application
                .build();
        mAppComponent.inject(this);
    }

    /**
     * 将AppComponent返回出去,供其它地方使用, AppComponent接口中声明的方法返回的实例,
     * 在getAppComponent()拿到对象后都可以直接使用
     *
     * @return
     */
    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    /**
     * 返回上下文
     *
     * @return
     */
    public static Context getContext() {
        return mApplication;
    }
}
