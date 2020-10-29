package com.example.shop.aspect;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public @interface Authentication {
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AuthenticationAspect {
    }
}
