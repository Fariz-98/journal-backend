package com.journal.journalbackend.exceptions;

/*
* Occurs when an element has already existed in the database.
* E.g. Email & Username
 */
public class ElementTakenException extends RuntimeException {

    public ElementTakenException(String message) {
        super(message);
    }

}
