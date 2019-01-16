package org.harry.fastdagger.demo.di.component;

import org.harry.fastdagger.demo.di.module.LoginModule;
import org.harry.fastdagger.demo.ui.LoginActivity;
import org.harry.fastdagger.demo.di.scope.PerActivity;
import dagger.Component;
import org.harry.fastdagger.demo.di.component.AppComponent;
import org.harry.fastdagger.demo.di.module.AppModule;

@PerActivity
@Component(modules = {LoginModule.class,AppModule.class},dependencies = {AppComponent.class})
public interface LoginComponent {
	void inject(LoginActivity loginActivity);
}