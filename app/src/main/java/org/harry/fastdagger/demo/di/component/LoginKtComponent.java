package org.harry.fastdagger.demo.di.component;

import org.harry.fastdagger.demo.di.module.LoginKtModule;
import org.harry.fastdagger.demo.ui.LoginKtActivity;
import org.harry.fastdagger.demo.di.scope.PerActivity;
import dagger.Component;
import org.harry.fastdagger.demo.di.component.AppComponent;
import org.harry.fastdagger.demo.di.module.AppModule;

@PerActivity
@Component(modules = {LoginKtModule.class,AppModule.class},dependencies = {AppComponent.class})
public interface LoginKtComponent {
	void inject(LoginKtActivity loginKtActivity);
}