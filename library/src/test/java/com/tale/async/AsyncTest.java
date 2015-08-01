package com.tale.async;

import junit.framework.TestCase;

import org.mockito.Mockito;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Author tale. Created on 8/1/15.
 */
public class AsyncTest extends TestCase {

    public static final String SUCCESS = "Success";
    public static final String ERROR = "Error";

    Task<String, String> sampleTask;
    Async<String, String> async;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        sampleTask = new Task<String, String>() {
            @Override
            public Result call(Object... params) {
                String type = (String) params[0];
                if (type.equals(SUCCESS)) {
                    return success(type);
                } else {
                    return error(type);
                }
            }
        };

        async = Async.newInstance(sampleTask);
    }

    public void testCallbackSuccess() throws Exception {
        Callback<String, String> callback = Mockito.mock(Callback.class);
        async.callback(callback);
        async.handleResult(new Result(SUCCESS, null));
        verify(callback).onSuccess(SUCCESS);
        verify(callback).onCompleted();
        verify(callback, never()).onError(anyString());
    }

    public void testCallbackError() throws Exception {
        Callback<String, String> callback = Mockito.mock(Callback.class);
        async.callback(callback);
        async.handleResult(new Result(null, ERROR));
        verify(callback).onError(ERROR);
        verify(callback).onCompleted();
        verify(callback, never()).onSuccess(anyString());
    }

    public void testOnSuccess() throws Exception {
        Action1 success = Mockito.mock(Action1.class);
        async.onSuccess(success);
        async.handleResult(new Result(SUCCESS, null));
        verify(success).call(SUCCESS);
    }

    public void testOnError() throws Exception {
        Action1 error = Mockito.mock(Action1.class);
        async.onError(error);
        async.handleResult(new Result(null, ERROR));
        verify(error).call(ERROR);
    }

    public void testOnComplete() throws Exception {
        Action0 complete = Mockito.mock(Action0.class);
        async.onComplete(complete);
        async.handleResult(new Result(SUCCESS, null));
        verify(complete).call();
    }
}