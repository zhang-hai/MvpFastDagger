package org.harry.fastdagger.demo.mvp.presenter;

import org.harry.fastdagger.demo.mvp.view.ILoginView;
import org.harry.fastdagger.demo.mvp.model.ILoginModel;
import javax.inject.Inject;
import org.harry.fastdagger.demo.base.BasePresenter;

public class LoginPresenter extends BasePresenter<ILoginModel,ILoginView> {

	@Inject
	public LoginPresenter(ILoginModel model,ILoginView view){
		 super(model,view);
	}


	public boolean useEventBus(){
		return false;
	}
}