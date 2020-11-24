package com.example.shop.aspect;

import com.example.shop.entity.Response;
import com.example.shop.exception.HttpException;
import com.example.shop.generate.Goods;
import com.example.shop.generate.Shop;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

@Aspect
@Component
public class ResponseAdvice {

    @SneakyThrows
    @Around("@annotation(com.example.shop.aspect.ResponseAnnotation)")
    public Object advice(ProceedingJoinPoint process) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
        assert response != null;
        Response<Object> resultResponse;
        try {
            Object proceed = process.proceed();
            if (proceed instanceof Shop || proceed instanceof Goods) {
                resultResponse = Response.of(proceed);
            } else {
                return proceed;
            }
        } catch (HttpException e) {
            response.setStatus(e.getStatusCode());
            resultResponse = Response.of(e.getMessage(), null);
        }
        return resultResponse;
    }
}
