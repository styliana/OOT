package org.example.exceptions;

public class DownloadException extends RuntimeException {
    public DownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
