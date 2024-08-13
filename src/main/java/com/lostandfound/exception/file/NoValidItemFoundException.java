package com.lostandfound.exception.file;

public non-sealed class NoValidItemFoundException extends FileException {
    public NoValidItemFoundException(final String message) {
        super(message);
    }
}
