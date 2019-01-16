package org.harry.fastdagger.demo.ui;

import android.content.Context;
import android.content.Intent;

import org.harry.fastdagger.annotation.FastDagger;
import org.harry.fastdagger.annotation.MvpFastDagger;
import org.harry.fastdagger.demo.base.BaseActivity;
import org.harry.fastdagger.demo.base.BaseModel;
import org.harry.fastdagger.demo.base.BasePresenter;
import org.harry.fastdagger.demo.base.IModel;
import org.harry.fastdagger.demo.base.IViewAdvance;
import org.harry.fastdagger.demo.di.component.AppComponent;
import org.harry.fastdagger.demo.di.module.AppModule;
import org.harry.fastdagger.demo.di.scope.PerActivity;
import org.harry.fastdagger.demo.mvp.presenter.LoginPresenter;
import org.harry.fastdagger.demo.mvp.view.ILoginView;

/**
 * Created by zhanghai on 2019/1/16.
 * function：
 */
@MvpFastDagger(name = "login",
        basePresenterClazz = BasePresenter.class,
        iBaseViewClazz = IViewAdvance.class,
        iBaseModelClazz = IModel.class,
        baseModelImpClazz = BaseModel.class,
        scopeClazz = PerActivity.class,
        modules = AppModule.class,
        dependencies = AppComponent.class)
public class LoginActivity extends BaseActivity<LoginPresenter> implements ILoginView {

    @Override
    public void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void forceShowLoading(String msg) {

    }

    @Override
    public void showToast(String message) {

    }

    @Override
    public void showShortcutMenu() {

    }

    @Override
    public void hideShortcutMenu() {

    }

    @Override
    public void hideSoftInput() {

    }

    @Override
    public Context getAttachedContext() {
        return null;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void launchActivity(Intent intent) {

    }

    @Override
    public void launchActivity(Class clz) {

    }

    @Override
    public void killMyself() {

    }

    @Override
    public boolean isNetworkConnected() {
        return false;
    }
}
