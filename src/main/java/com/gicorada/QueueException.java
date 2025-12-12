package com.gicorada;

public class QueueException extends RuntimeException {
    public final String code;
    public final int status;


    public QueueException(String code, String message, int status) {
        super(message);
        this.code = code;
        this.status = status;
    }
}
