package com.lollychat.dto;

public class ApiResponseWrapper<T> {
    private String message;
    private T data;

    // Конструктори
    public ApiResponseWrapper() {
    }

    public ApiResponseWrapper( String message, T data) {
        this.message = message;
        this.data = data;
    }

    // Геттери та сеттери




    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
