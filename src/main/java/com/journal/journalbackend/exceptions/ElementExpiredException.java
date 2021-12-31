package com.journal.journalbackend.exceptions;

/*
* Used for element with expiration time/date.
* E.g. Expired token.
 */
public class ElementExpiredException extends RuntimeException {

    public ElementExpiredException(String message) {
        super(message);
    }

}
