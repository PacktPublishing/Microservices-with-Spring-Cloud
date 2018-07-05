package com.packtpub.yummy.ratings.config;

import lombok.Getter;
import lombok.ToString;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorAdvice {

    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody
    String elementNotFound(IncorrectResultSizeDataAccessException e) {
        return "Could not find element! " + e.getMessage();
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody
    String elementNotFound(OptimisticLockingFailureException e) {
        return "Conflict! " + e.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    List<ErrorMessage> badRequest(MethodArgumentNotValidException e) {
        return e.getBindingResult().getAllErrors().stream()
                .map(err -> new ErrorMessage(err))
                .collect(Collectors.toList());

    }

    @Getter
    @ToString
    public static class ErrorMessage {
        String message, code, field, objectName;

        public ErrorMessage(ObjectError objectError) {
            message = objectError.getDefaultMessage();
            code = objectError.getCode();
            objectName = objectError.getObjectName();
            if (objectError instanceof FieldError) {
                field = ((FieldError) objectError).getField();
            }
        }
    }
}
