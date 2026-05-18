package com.backendSupermercado.supermercasdo.exceptions;

public class ResourceConflictException extends RuntimeException  {
    public ResourceConflictException(String message) {
        super(message);
    }
}
