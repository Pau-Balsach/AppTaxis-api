package com.apptaxis.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String error;
    private LocalDateTime timestamp;

    private ApiResponse(boolean success, String message, T data, String error) {
        this.success   = success;
        this.message   = message;
        this.data      = data;
        this.error     = error;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> ok(String message) {
        return new ApiResponse<>(true, message, null, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, null, message);
    }

    public boolean isSuccess()          { return success; }
    public String getMessage()          { return message; }
    public T getData()                  { return data; }
    public String getError()            { return error; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
