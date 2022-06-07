package com.viseo.apph.exception;

public class MaxSizeExceededException extends Exception {
    public MaxSizeExceededException(String errorMessage) {
        super(errorMessage);
    }
}
