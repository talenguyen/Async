package com.tale.async;

/**
 * Author tale. Created on 7/31/15.
 */
public abstract class Task<Data, Error> {

    public abstract Result call(Object... params);

    public Result error(Error error) {
        if (error == null) {
            throw new NullPointerException("error(error) error must not be null");
        }
        return new Result(null, error);
    }

    public Result success(Data data) {
        if (data == null) {
            throw new NullPointerException("succes(data) data must not be null");
        }
        return new Result(data, null);
    }
}
