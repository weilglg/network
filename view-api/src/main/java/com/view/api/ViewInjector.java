package com.view.api;

import android.app.Activity;
import android.util.Log;
import android.view.View;

/**
 * Created by wll on 2017/9/8.
 */

public class ViewInjector {
    private static final String SUFFIX = "$ViewInject";

    public static void injectView(Activity activity){
        ViewInject proxyActivity = findProxyActivity(activity);
        proxyActivity.inject(activity, activity);
    }

    /**
     *
     *
     * @param object
     * @param view
     */
    public static void injectView(Object object, View view){
        ViewInject proxyView = findProxyActivity(object);
        proxyView.inject(object, view);
    }

    private static ViewInject findProxyActivity(Object object) {
        Class<?> aClass = object.getClass();
        try {
            Class<?> injectClazz = aClass.forName(aClass.getName() + SUFFIX);
            return (ViewInject) injectClazz.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new RuntimeException(String.format("can not find %s , something when compiler.", object.getClass().getSimpleName() + SUFFIX));
    }
}
