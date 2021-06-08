package com.cet.devportal.controller;

//import com.my.blog.website.exception.TipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

/**
 * Created by BlueT on 2017/3/4.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

//    @ExceptionHandler(value = TipException.class)
//    public String tipException(Exception e) {
//        LOGGER.error("find exception:e={}",e.getMessage());
//        e.printStackTrace();
//        return "comm/error_500";
//    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public String constraintViolationException(Exception e){
        LOGGER.error("ConstraintViolationException: {}",e.getMessage());
        //e.printStackTrace();

        return "comm/error-404";
    }

    @ExceptionHandler(value = Exception.class)
    public String exception(Exception e){
        LOGGER.error("Exception: {}",e.getMessage());
        //e.printStackTrace();
        return "comm/error-404";
    }
}
