package com.journal.journalbackend.exceptions;

/*
* Thrown when a key-value is expected from the request header but the key is missing.
* E.g. Jwt token authorization header.
 */
public class InvalidAuthenticationHeaderException extends RuntimeException {

    public InvalidAuthenticationHeaderException(String message) {
        super(message);
    }
}
