package com.journal.journalbackend.exceptions;

/*
* Occurs when a finished action is repeated.
* E.g. Confirming email twice.
 */
public class DuplicateActionException extends RuntimeException {

    public DuplicateActionException(String message) {
        super(message);
    }


}
