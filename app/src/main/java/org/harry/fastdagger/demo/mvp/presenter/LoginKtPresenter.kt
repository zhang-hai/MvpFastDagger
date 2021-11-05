package org.harry.fastdagger.demo.mvp.presenter

import org.harry.fastdagger.demo.mvp.view.ILoginKtView
import org.harry.fastdagger.demo.mvp.model.ILoginKtModel
import javax.inject.Inject
import org.harry.fastdagger.demo.base.BasePresenter

class LoginKtPresenter @Inject constructor(model: ILoginKtModel,view: ILoginKtView): BasePresenter<ILoginKtModel,ILoginKtView>(model,view) {


	override  fun useEventBus():Boolean{
		return false
	}
}