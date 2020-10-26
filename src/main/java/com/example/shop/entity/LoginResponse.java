package com.example.shop.entity;

import com.example.shop.generate.User;
import lombok.Getter;

@Getter
public class LoginResponse {
    private final boolean login;
    private String tel;
    private String code;
    private User user;

    private LoginResponse(boolean login, String tel, String code) {
        this.login = login;
        this.tel = tel;
        this.code = code;
    }

    private LoginResponse(boolean login, User user) {
        this.login = login;
        this.user = user;
    }

    private LoginResponse(boolean login, String tel) {
        this.login = login;
        this.tel = tel;
    }

    public static LoginResponse notLogin() {
        return new LoginResponse(false, new User());
    }

    public static LoginResponse login(User user) {
        return new LoginResponse(true, user);
    }

    public static LoginResponse getCodeFailure() {
        return new LoginResponse(false, null, null);
    }

    public static LoginResponse getCodeSuccess(String tel, String code) {
        return new LoginResponse(true, tel, code);
    }

    public static LoginResponse success(String tel) {
        return new LoginResponse(true, tel);
    }

    public static LoginResponse failure() {
        return new LoginResponse(false, "");
    }
}
