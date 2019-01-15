package org.harry.fastdagger.demo.mvp.presenter;

import org.harry.fastdagger.demo.base.BasePresenter;
import org.harry.fastdagger.demo.mvp.model.IMainModel;
import org.harry.fastdagger.demo.mvp.view.IMainView;

import javax.inject.Inject;

/**
 * Created by zhanghai on 2019/1/10.
 * function：
 */
public class MainPresenter extends BasePresenter<IMainModel,IMainView> {

    @Inject
    public MainPresenter(IMainModel model,IMainView view){
        super(model,view);
    }

    @Override
    public boolean useEventBus() {
        return false;
    }
}
