package com.journal.journalbackend.exceptions;

import com.journal.journalbackend.exceptions.payload.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;


@RestControllerAdvice
public class AllExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest webRequest) {
        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                webRequest.getDescription(false)
        );
    }

    @ExceptionHandler(ElementTakenException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponse handleElementExistsException(ElementTakenException ex, WebRequest webRequest) {
        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT,
                ex.getMessage(),
                webRequest.getDescription(false)
        );
    }

    @ExceptionHandler(ElementNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleElementNotFoundException(ElementNotFoundException ex, WebRequest webRequest) {
        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                webRequest.getDescription(false)
        );
    }

    @ExceptionHandler(InvalidInputException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidInputException(InvalidInputException ex, WebRequest webRequest) {
        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                webRequest.getDescription(false)
        );
    }

    @ExceptionHandler(DuplicateActionException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ErrorResponse handleDuplicateActionException(DuplicateActionException ex, WebRequest webRequest) {
        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN,
                ex.getMessage(),
                webRequest.getDescription(false)
        );
    }

    @ExceptionHandler(ElementExpiredException.class)
    @ResponseStatus(value = HttpStatus.GONE)
    public ErrorResponse handleElementExpiredException(ElementExpiredException ex, WebRequest webRequest) {
        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.GONE,
                ex.getMessage(),
                webRequest.getDescription(false)
        );
    }

    @ExceptionHandler(InvalidAuthenticationHeaderException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleMissingAuthenticationHeaderException(InvalidAuthenticationHeaderException ex, WebRequest webRequest) {
        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                webRequest.getDescription(false)
        );
    }

    @ExceptionHandler(NoPermissionException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ErrorResponse handleInsufficientPermissionException(NoPermissionException ex, WebRequest webRequest) {
        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN,
                ex.getMessage(),
                webRequest.getDescription(false)
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException ex, WebRequest webRequest) {
        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN,
                "Insufficient Permission",
                webRequest.getDescription(false)
        );
    }

}










































