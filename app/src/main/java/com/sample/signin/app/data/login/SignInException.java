package com.sample.signin.app.data.login;

import rx.Observable;

/**
 * Exception thrown in all cases of failures connected with logging in by the user
 * Created by srokowski.maciej@gmail.com on 19.10.16.
 */

public class SignInException extends RuntimeException {

    public SignInException(String message) {
        super(message);
    }

    @Override
    public String getLocalizedMessage() {
        return super.getLocalizedMessage();
    }

    /**
     * Returns observable containing this exception with given error message
     *
     * @param errorMessage error message
     * @param <T>          appropriate type parameter
     * @return Error observable
     */
    public static <T> Observable<T> asErrorObservable(String errorMessage) {
        return Observable.error(new SignInException(errorMessage));
    }
}
