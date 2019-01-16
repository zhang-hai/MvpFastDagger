package org.harry.fastdagger.demo.ui;

import android.content.Intent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.harry.fastdagger.annotation.FastDagger;
import org.harry.fastdagger.demo.R;
import org.harry.fastdagger.demo.base.BaseActivity;
import org.harry.fastdagger.demo.di.component.AppComponent;
import org.harry.fastdagger.demo.di.component.DaggerMainComponent;
import org.harry.fastdagger.demo.di.module.MainModule;
import org.harry.fastdagger.demo.di.scope.PerActivity;
import org.harry.fastdagger.demo.mvp.model.IMainModel;
import org.harry.fastdagger.demo.mvp.presenter.MainPresenter;
import org.harry.fastdagger.demo.mvp.view.IMainView;


@FastDagger(modelCls = IMainModel.class,
        viewCls = IMainView.class,
        activityCls = MainActivity.class,
        scopeCls = PerActivity.class,
        dependencies = {AppComponent.class})
@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity<MainPresenter> implements IMainView {

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .mainModule(new MainModule(this))
                .build()
                .inject(this);
    }

    @AfterViews
    void init() {
        // 初始化数据
        initData();
    }

    @Override
    protected void initData() {

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
