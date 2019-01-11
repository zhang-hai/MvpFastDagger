package org.harry.fastdagger.demo.di.component;

import android.app.Application;

import org.harry.fastdagger.demo.BaseApplication;
import org.harry.fastdagger.demo.di.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    Application Application();

    void inject(BaseApplication application);
}
