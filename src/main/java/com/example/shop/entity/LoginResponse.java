package com.example.shop.entity;

import com.example.shop.generate.User;
import lombok.Getter;

@Getter
@SuppressWarnings("unused")
public class LoginResponse {
    private boolean login;
    private User user;
    private String code;

    private LoginResponse(boolean login) {
        this.login = login;
    }

    private LoginResponse(boolean login, String code) {
        this.login = login;
        this.code = code;
    }

    private LoginResponse(boolean login, User user) {
        this.login = login;
        this.user = user;
    }

    public LoginResponse() {
    }

    public static LoginResponse notLogin() {
        return new LoginResponse(false, new User());
    }

    public static LoginResponse login(User user) {
        return new LoginResponse(true, user);
    }

    public static LoginResponse getCodeSuccess(String code) {
        return new LoginResponse(true, code);
    }

    public static LoginResponse success() {
        return new LoginResponse(true);
    }

    public static LoginResponse failure() {
        return new LoginResponse(false);
    }
}
