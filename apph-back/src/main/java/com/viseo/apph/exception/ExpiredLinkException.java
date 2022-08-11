package com.viseo.apph.exception;

public class ExpiredLinkException extends Exception{
    public ExpiredLinkException(){
        super("token.expiredLink");
    }
}
