package com.tale.async;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Author tale. Created on 7/31/15.
 */
public class Async<Data, Error> {

  private static final String TASK = "Task";
  public static boolean DEBUG = true;
  private final Task task;
  private Callback<Data, Error> callback;
  private Action0 complete;
  private Action1 success;
  private Action1 error;

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
      this.callback = callback;
    }
    return this;
  }

  public Async<Data, Error> onSuccess(Action1 action) {
    if (action != null) {
      success = action;
    }
    return this;
  }

  public Async<Data, Error> onError(Action1 action) {
    if (action != null) {
      error = action;
    }
    return this;
  }

  public Async<Data, Error> onComplete(Action0 action) {
    if (action != null) {
      complete = action;
    }
    return this;
  }

  public AsyncTask executeOnThreadPool(Object... params) {
    return buildTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
  }

  public AsyncTask executeSerial(Object... params) {
    return buildTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, params);
  }

  Result executeTask(Object... params) {
    return task.call(params);
  }

  private AsyncTask buildTask() {
    return new AsyncTask<Object, Void, Result>() {
      @Override protected Result doInBackground(Object[] objects) {
        return executeTask(objects);
      }

      @Override protected void onPostExecute(Result result) {
        if (isCancelled()) {
          return;
        }
        handleResult(result);
      }

      @Override protected void onCancelled() {
        callback = null;
        success = null;
        error = null;
        complete = null;
      }
    };
  }

  void handleResult(Result result) {
    if (result == null) {
      throw new NullPointerException("doInBackground() must not return null");
    } else {

      if (callback != null) {
        if (result.data != null) {
          callback.onSuccess((Data) result.data);
        } else {
          callback.onError((Error) result.error);
        }
        callback.onCompleted();
      } else {
        if (result.data != null && success != null) {
          success.call(result.data);
        }
        if (result.error != null && error != null) {
          error.call(result.error);
        }
        if (complete != null) {
          complete.call();
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
