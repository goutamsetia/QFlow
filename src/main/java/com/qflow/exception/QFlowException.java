package com.qflow.exception;

/**
 * Root unchecked exception for all domain and operational errors in QFlow.
 * 
 * Acting as the superclass for all custom application exceptions, it allows 
 * top-level presentation and controller layers to trap any system error 
 * cleanly without forcing checked exception declarations across internal service methods.
 */
public class QFlowException extends RuntimeException {

    public QFlowException(String message) {
        super(message);
    }

    public QFlowException(String message, Throwable cause) {
        super(message, cause);
    }

    public QFlowException(Throwable cause) {
        super(cause);
    }
}
