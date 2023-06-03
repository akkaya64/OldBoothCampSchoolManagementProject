package com.schoolmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)// Donecek olan exceptionun kodunu @ResponseStatus ile burada belirliyoruz
public class ResourceNotFoundException extends RuntimeException{ // RuntimeException dan extend edebiliriz

    public ResourceNotFoundException(String message) {
        super(message);
    }
}