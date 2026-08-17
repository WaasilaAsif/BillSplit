package com.example.billsplit.data.remote.dto;

/** Every success response from the backend is {"status":"success","data": ...}. */
public class ApiResponse<T> {
    private String status;
    private T data;

    public String getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }
}
