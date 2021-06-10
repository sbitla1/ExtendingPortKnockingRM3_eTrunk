package com.codility.exceptionhandling;
import com.codility.rest.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.xml.ws.Response;

// This class can be modified
@ControllerAdvice
public class ExceptionHandlingControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value
            = {IllegalArgumentException.class})
    protected ResponseEntity<Object> handleIllegalArgumentException(
            RuntimeException ex, WebRequest request) {
        Response responseBody = Response.fail(ex.getMessage());

        return handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.OK, request);
    }

    //second case
    @ExceptionHandler(value = {TaskAlreadyExists.class})
    protected ResponseEntity<Object> handleTaskAlreadyExistsException(
            RuntimeException ex, WebRequest request) {
        TaskAlreadyExists taskAlreadyExists = (TaskAlreadyExists) ex ;
        Response responseBody = Response.fail("Task with title: "+taskAlreadyExists+" already exists.");


        return handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.OK, request);
    }

    //Third case
    //second case
    @ExceptionHandler(value
            = {TaskNotFound.class})
    protected ResponseEntity<Object> handleTaskNotFoundException(
            RuntimeException ex, WebRequest request) {


        return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

}