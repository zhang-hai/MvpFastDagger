package org.harry.fastdagger.demo.base;


public abstract class BasePresenter<M extends IModel, V extends IView> implements IPresenter {
    protected static final int PAGE_SIZE = 15;

    protected final String TAG = this.getClass().getSimpleName();

    protected M mModel;

    protected V mRootView;

    public BasePresenter(M model, V rootView) {
        this.mModel = model;
        this.mRootView = rootView;
        onAttach();
    }

    public BasePresenter(V rootView) {
        this.mRootView = rootView;
        onAttach();
    }

    public BasePresenter() {
        onAttach();
    }


    @Override
    public void onAttach() {

    }

    @Override
    public void onDetach() {
        if (mModel != null)
            mModel.onDestroy();
        this.mModel = null;
        this.mRootView = null;
    }

    /**
     * 是否使用eventBus,默认为使用(true)，
     *
     * @return
     */
    public abstract boolean useEventBus();

//    public abstract byte getNextByte(int a);
//
//    public abstract String getNextString(String a,double b);
//
//    public abstract void test();
//
//    public abstract long getNextLong();
//
//    public abstract float getNextFloat();
//
//    public abstract double getNextDouble();
//
//    public abstract IModel getNextModel(IModel model);
}
