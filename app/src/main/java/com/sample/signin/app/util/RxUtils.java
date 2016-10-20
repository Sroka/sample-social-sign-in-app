package com.sample.signin.app.util;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by srokowski.maciej@gmail.com on 18.10.16.
 */

public class RxUtils {

    private static final Observable.Transformer<Observable, Observable> schedulersTransformer = observableObservable -> observableObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());

    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T, T> applyIoMainSchedulers() {
        return (Observable.Transformer<T, T>) schedulersTransformer;
    }
}
