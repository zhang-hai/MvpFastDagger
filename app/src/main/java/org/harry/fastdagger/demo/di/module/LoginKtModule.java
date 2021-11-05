package org.harry.fastdagger.demo.di.module;

import org.harry.fastdagger.demo.mvp.model.ILoginKtModel;
import org.harry.fastdagger.demo.mvp.model.LoginKtModelImp;
import org.harry.fastdagger.demo.mvp.view.ILoginKtView;
import dagger.Module;
import dagger.Provides;
import org.harry.fastdagger.demo.di.scope.PerActivity;

@Module
public class LoginKtModule  {

	private ILoginKtView view;

	public LoginKtModule(ILoginKtView view) {
		this.view = view;
	}

	@PerActivity
	@Provides
	ILoginKtView provideLoginKtView(){
		return this.view;
	}

	@PerActivity
	@Provides
	ILoginKtModel provideLoginKtModel(LoginKtModelImp model){
		return model;
	}

}