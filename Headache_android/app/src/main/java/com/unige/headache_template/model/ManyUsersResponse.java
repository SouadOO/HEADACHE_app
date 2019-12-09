package com.unige.headache_template.model;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;


public class ManyUsersResponse<T>{


    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("users")
    private T[] users;


    public ManyUsersResponse(Boolean error, String message, T[] users) {
        this.error = error;
        this.message = message;
        this.users = users;
    }

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public T[] getUsers() {
        return users;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUsers(T[] users) {

        for(int i=0;i<users.length;i++)
            this.users[i] = users[i];
    }

    @Override
    public String toString() {
        return "ManyUsersResponse{" +
                "users=" + Arrays.toString(users) +
                '}';
    }
}
