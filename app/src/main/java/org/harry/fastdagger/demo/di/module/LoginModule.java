package org.harry.fastdagger.demo.di.module;

import org.harry.fastdagger.demo.mvp.model.ILoginModel;
import org.harry.fastdagger.demo.mvp.model.LoginModelImp;
import org.harry.fastdagger.demo.mvp.view.ILoginView;
import dagger.Module;
import dagger.Provides;
import org.harry.fastdagger.demo.di.scope.PerActivity;

@Module
public class LoginModule  {

	private ILoginView view;

	public LoginModule(ILoginView view) {
		this.view = view;
	}

	@PerActivity
	@Provides
	ILoginView provideLoginView(){
		return this.view;
	}

	@PerActivity
	@Provides
	ILoginModel provideLoginModel(LoginModelImp model){
		return model;
	}

}