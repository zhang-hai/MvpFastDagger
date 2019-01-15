package org.harry.fastdagger.demo.base;

public interface IModel {
    void onDestroy();

    interface DataCallbackToUi<T> {
        void onSuccess(T objects);

        void onFail(String msg);
    }
}
