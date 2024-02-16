package com.sky.handler;

import com.sky.exception.BusinessException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public Result otherExceptionHandler(Exception ex){
        log.error("出现未知异常：{}",ex);
        return Result.error(500,ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public Result businessExceptionHandler(BusinessException ex){
        log.error("出现业务异常：{}",ex);
        return Result.error(ex.getCode(),ex.getMessage());
    }

}
