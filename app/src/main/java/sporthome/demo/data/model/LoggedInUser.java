/*
 * Copyright (C) 2021 Baidu, Inc. All Rights Reserved.
 */
package sporthome.demo.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private int userId;
    private String displayName;
    private String token;

    public LoggedInUser() {
    }

    public LoggedInUser(int userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }

    public LoggedInUser(int userId, String displayName, String token) {
        this.userId = userId;
        this.displayName = displayName;
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getToken() {
        return token;
    }

    public void setUserId(int id) {
        this.userId = id;
    }

    public void setDisplayName(String name) {
        this.displayName = name;
    }

    public void setToken(String tok) {
        this.token = token;
    }
}