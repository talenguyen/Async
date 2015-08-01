package com.tale.async;

/**
 * Author tale. Created on 7/31/15.
 */
public interface Callback<Data, Error> {

    void onSuccess(Data data);

    void onError(Error error);

    void onCompleted();

}
