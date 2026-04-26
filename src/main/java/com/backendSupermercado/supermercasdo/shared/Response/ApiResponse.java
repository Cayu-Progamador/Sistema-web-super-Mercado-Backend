package com.backendSupermercado.supermercasdo.shared.Response;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T Data;

    
    public ApiResponse() {
    }

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        Data = data;
    }
    
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public T getData() {
        return Data;
    }
    public void setData(T data) {
        Data = data;
    }

    
}
