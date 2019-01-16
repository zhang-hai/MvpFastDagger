package org.harry.fastdagger.demo.mvp.model;

import javax.inject.Inject;
import org.harry.fastdagger.demo.mvp.model.ILoginModel;
import org.harry.fastdagger.demo.base.BaseModel;

public class LoginModelImp extends BaseModel  implements ILoginModel{

	@Inject
	public LoginModelImp(){
	}

}