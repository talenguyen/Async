package com.tale.async;

/**
 * Author tale. Created on 7/31/15.
 */
public class Result {

    final Object data;
    final Object error;

    Result(Object data, Object error) {
        this.data = data;
        this.error = error;
    }
}
