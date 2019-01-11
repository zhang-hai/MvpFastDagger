package org.harry.fastdagger.demo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.harry.fastdagger.annotation.FastDagger;
import org.harry.fastdagger.demo.R;
import org.harry.fastdagger.demo.di.component.AppComponent;
import org.harry.fastdagger.demo.di.scope.PerActivity;
import org.harry.fastdagger.demo.mvp.model.IMainModel;
import org.harry.fastdagger.demo.mvp.view.IMainView;

@FastDagger(modelCls = IMainModel.class,
        viewCls = IMainView.class,
        activityCls = MainActivity.class,
        scopeCls = PerActivity.class,
        dependencies = {AppComponent.class})
public class MainActivity extends AppCompatActivity implements IMainView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
