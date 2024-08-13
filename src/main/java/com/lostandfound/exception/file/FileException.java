package com.lostandfound.exception.file;

public sealed class FileException extends RuntimeException permits FileNotFoundException,
        UnSupportedFileFormatException, NoValidItemFoundException  {
    public FileException(String message) {
        super(message);
    }
}
