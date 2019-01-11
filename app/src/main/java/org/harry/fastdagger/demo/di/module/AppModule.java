package org.harry.fastdagger.demo.di.module;

import android.app.Application;
import android.content.Context;

import org.harry.fastdagger.demo.di.scope.ApplicationContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private Application mApplication;

    public AppModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Singleton
    @Provides
    public Application provideApplication() {
        return mApplication;
    }




}
