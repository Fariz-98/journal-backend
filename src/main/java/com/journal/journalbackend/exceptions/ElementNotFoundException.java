package com.journal.journalbackend.exceptions;

/*
* Occurs when requested element is not found
 */
public class ElementNotFoundException extends RuntimeException {

    public ElementNotFoundException(String message) {
        super(message);
    }
}
