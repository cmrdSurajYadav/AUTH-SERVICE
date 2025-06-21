package org.smarthire.AUTH_SERVICE.EXCEPTION;

public class EmailAlreadyExistsException extends RuntimeException{

    public EmailAlreadyExistsException(String msg){
        super(msg);
    }
}
