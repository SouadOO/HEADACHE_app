package com.unige.headache_template.model;

import com.google.gson.annotations.SerializedName;

public class UserResponse<T> {


    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private T user=null;


    public void setError(Boolean error) {
        this.error = error;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser(T user) {
        this.user = user;
    }
    public Boolean getError() {
        return error;
    }
    public String getMessage() {
        return message;
    }
    public T getUser() {
        return user;
    }

    public UserResponse(Boolean error, String message) {
        this.error = error;
        this.message = message;
        this.user = null;
    }
    public UserResponse(Boolean error, String message,T user) {
        this.error = error;
        this.message = message;
        this.user = user;
    }

}
