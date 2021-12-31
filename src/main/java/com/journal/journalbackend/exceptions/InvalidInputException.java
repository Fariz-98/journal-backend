package com.journal.journalbackend.exceptions;

/*
* Occurs when verifying an invalid input
* E.g. Invalid email
 */
public class InvalidInputException extends RuntimeException {

    public InvalidInputException(String message) {
        super(message);
    }
}
