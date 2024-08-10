package com.lostandfound.exception.file;

public sealed class FileException extends RuntimeException permits FileNotFoundException, UnSupportedFileFormatException  {
    public FileException(String message) {
        super(message);
    }
}
