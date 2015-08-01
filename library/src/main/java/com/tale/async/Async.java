package com.tale.async;

import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Author tale. Created on 7/31/15.
 */
public class Async<Data, Error> {

    private static final String TASK = "Task";
    public static boolean DEBUG = true;
    private final Task task;
    private WeakReference<Callback<Data, Error>> callbackWeakReference;
    private WeakReference<Action0> completeWeakReference;
    private WeakReference<Action1> successWeakReference;
    private WeakReference<Action1> errorWeakReference;

    private Async(Task task) {
        if (task == null) {
            throw new NullPointerException("task must not be null");
        }
        this.task = task;
    }

    public static <Data, Error> Async<Data, Error> newInstance(Task task) {
        return new Async<>(task);
    }

    public Async<Data, Error> callback(Callback<Data, Error> callback) {
        if (callback != null) {
            callbackWeakReference = new WeakReference<>(callback);
        }
        return this;
    }

    public Async<Data, Error> onSuccess(Action1 action) {
        if (action != null) {
            successWeakReference = new WeakReference<>(action);
        }
        return this;
    }

    public Async<Data, Error> onError(Action1 action) {
        if (action != null) {
            errorWeakReference = new WeakReference<>(action);
        }
        return this;
    }

    public Async<Data, Error> onComplete(Action0 action) {
        if (action != null) {
            completeWeakReference = new WeakReference<>(action);
        }
        return this;
    }

    public AsyncTask executeOnThreadPool(Object... params) {
        return buildTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                params);
    }

    public AsyncTask executeSerial(Object... params) {
        return buildTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, params);
    }

    Result executeTask(Object... params) {
        return task.call(params);
    }

    private AsyncTask buildTask() {
        return new AsyncTask<Object, Void, Result>() {
            @Override
            protected Result doInBackground(Object[] objects) {
                return executeTask(objects);
            }

            @Override
            protected void onPostExecute(Result result) {
                super.onPostExecute(result);
                if (isCancelled()) {
                    return;
                }
                handleResult(result);
            }

            @Override
            protected void onCancelled(Result result) {
                super.onCancelled(result);
            }
        };
    }

    void handleResult(Result result) {
        if (result == null) {
            throw new NullPointerException("doInBackground() must not return null");
        } else {

            if (callbackWeakReference != null) {
                final Callback<Data, Error> callback = callbackWeakReference.get();

                if (result.data != null) {
                    if (callback != null) {
                        callback.onSuccess((Data) result.data);
                    }

                } else {
                    if (callback != null) {
                        callback.onError((Error) result.error);
                    }
                }
                if (callback != null) {
                    callback.onCompleted();
                }
            } else {
                if (result.data != null && successWeakReference != null) {
                    final Action1 success = successWeakReference.get();
                    if (success != null) {
                        success.call(result.data);
                    }
                }
                if (result.error != null && errorWeakReference != null) {
                    final Action1 error = errorWeakReference.get();
                    if (error != null) {
                        error.call(result.error);
                    }
                }
                if (completeWeakReference != null) {
                    final Action0 complete = completeWeakReference.get();
                    if (complete != null) {
                        complete.call();
                    }
                }
            }
        }
    }

    void log(String message) {
        if (DEBUG) {
            Log.d(TASK, message);
        }
    }
}
