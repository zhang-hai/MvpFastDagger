package org.harry.fastdagger.demo.ui

import android.content.Context
import android.content.Intent
import org.harry.fastdagger.annotation.MvpFastDagger
import org.harry.fastdagger.demo.base.*
import org.harry.fastdagger.demo.di.component.AppComponent
import org.harry.fastdagger.demo.di.module.AppModule
import org.harry.fastdagger.demo.di.scope.PerActivity
import org.harry.fastdagger.demo.mvp.presenter.LoginKtPresenter
import org.harry.fastdagger.demo.mvp.view.ILoginKtView


@MvpFastDagger(
    name = "loginKt",
    basePresenterClazz = BasePresenter::class,
    iBaseViewClazz = [IViewAdvance::class],
    iBaseModelClazz = [IModel::class],
    baseModelImpClazz = BaseModel::class,
    scopeClazz = PerActivity::class,
    modules = [AppModule::class],
    dependencies = [AppComponent::class],
    language = MvpFastDagger.KOTLIN
)
class LoginKtActivity : BaseActivity<LoginKtPresenter>(),ILoginKtView{

    override fun setupActivityComponent(appComponent: AppComponent?) {

    }

    override fun initData() {

    }

    override fun showLoading(msg: String?) {
        TODO("Not yet implemented")
    }

    override fun showLoading() {
        TODO("Not yet implemented")
    }

    override fun hideLoading() {
        TODO("Not yet implemented")
    }

    override fun showMessage(message: String?) {
        TODO("Not yet implemented")
    }

    override fun launchActivity(intent: Intent?) {
        TODO("Not yet implemented")
    }

    override fun launchActivity(clz: Class<*>?) {
        TODO("Not yet implemented")
    }

    override fun killMyself() {
        TODO("Not yet implemented")
    }

    override fun isNetworkConnected(): Boolean {
        TODO("Not yet implemented")
    }

    override fun forceShowLoading(msg: String?) {
        TODO("Not yet implemented")
    }

    override fun showToast(message: String?) {
        TODO("Not yet implemented")
    }

    override fun showShortcutMenu() {
        TODO("Not yet implemented")
    }

    override fun hideShortcutMenu() {
        TODO("Not yet implemented")
    }

    override fun hideSoftInput() {
        TODO("Not yet implemented")
    }

    override fun getAttachedContext(): Context {
        TODO("Not yet implemented")
    }
}