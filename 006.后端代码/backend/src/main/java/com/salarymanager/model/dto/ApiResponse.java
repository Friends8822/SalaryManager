package com.salarymanager.model.dto;

public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) { ApiResponse<T> r = new ApiResponse<>(); r.code = 200; r.message = "success"; r.data = data; return r; }
    public static <T> ApiResponse<T> ok(String msg, T data) { ApiResponse<T> r = ok(data); r.message = msg; return r; }
    public static <T> ApiResponse<T> fail(int code, String msg) { ApiResponse<T> r = new ApiResponse<>(); r.code = code; r.message = msg; return r; }

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
