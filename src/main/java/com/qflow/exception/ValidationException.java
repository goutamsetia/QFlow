package com.qflow.exception;

/**
 * Thrown when customer details, token parameters, or business invariants
 * fail validation rules.
 * 
 * Extends {@link QFlowException} to allow presentation layers to differentiate 
 * between user-correctable validation mistakes (e.g. invalid phone number) 
 * and internal system/infrastructure failures.
 */
public class ValidationException extends QFlowException {

    private final String fieldName;

    public ValidationException(String message) {
        super(message);
        this.fieldName = null;
    }

    public ValidationException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.fieldName = null;
    }

    public ValidationException(String fieldName, String message, Throwable cause) {
        super(message, cause);
        this.fieldName = fieldName;
    }

    /**
     * Returns the name of the invalid field if explicitly identified, or null otherwise.
     */
    public String getFieldName() {
        return fieldName;
    }
}
