package com.youthlin.example.boot.web;

import com.youthlin.example.boot.bean.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * @author : youthlin.chen @ 2019-09-18 22:42
 */
@Slf4j
@RestControllerAdvice
public class WebExceptionHandler {
    @ExceptionHandler(Throwable.class)
    public Response handlerThrowable(Throwable t) {
        log.error("throwable: {}", t.getClass(), t);
        return new Response().setCode(1).setMsg(t.getLocalizedMessage());
    }

    /**
     * 方法参数校验
     */
    @ExceptionHandler(BindException.class)
    public Response handleMethodArgumentNotValidException(BindException e) {
        return new Response().setCode(1)
                .setMsg(e.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.joining("; ")
                        ));
    }

}
