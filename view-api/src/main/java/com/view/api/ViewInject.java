package com.view.api;

/**
 * Created by wll on 2017/9/7.
 */

public interface ViewInject<T> {

    void inject(T t, Object source);

}
