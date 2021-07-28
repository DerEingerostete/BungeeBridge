package de.dereingerostete.bungeebridge.util;

public class FutureResult<T> {
    protected final Request request;
    protected boolean done;
    protected T result;

    public FutureResult(Request request) {
        this.request = request;
        done = false;
    }

    public Request getRequest() {
        return request;
    }

    public void finish(T result) {
        this.result = result;
        done = true;
    }

    public boolean isDone() {
        return done;
    }

    public T getResult() {
        return result;
    }

}
