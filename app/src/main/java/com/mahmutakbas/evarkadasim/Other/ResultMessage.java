package com.mahmutakbas.evarkadasim.Other;

import javax.annotation.Nullable;

public class ResultMessage<T> {
    private boolean success;
    private String message;
    private T data;
    public ResultMessage(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ResultMessage(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
    public T getData(){
        return data;
    }
}
