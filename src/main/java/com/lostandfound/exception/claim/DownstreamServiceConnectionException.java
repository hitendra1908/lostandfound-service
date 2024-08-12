package com.lostandfound.exception.claim;

public class DownstreamServiceConnectionException extends RuntimeException {
    public DownstreamServiceConnectionException(final String message) {
        super(message);
    }
}
