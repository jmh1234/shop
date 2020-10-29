package com.example.shop.aspect;

import com.alibaba.fastjson.JSONObject;
import com.example.shop.utils.UserContext;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

@Aspect
@Component
public class AuthAdvice {

    @SneakyThrows
    @Around("@annotation(com.example.shop.aspect.Authentication)")
    public Object cacheAdvice(ProceedingJoinPoint process) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();

        if (UserContext.getCurrentUser() == null) {
            assert response != null;
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            JSONObject object = new JSONObject();
            object.put("message", "Unauthorized");
            System.out.println("Unauthorized !!!");
            return object;
        } else {
            return process.proceed();
        }
    }
}
