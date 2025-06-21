package org.smarthire.AUTH_SERVICE.EXCEPTION;

public class PhoneNumberAlreadyExistsException extends RuntimeException{

    public PhoneNumberAlreadyExistsException(String phoneNumber){
        super(phoneNumber);
    }
}
