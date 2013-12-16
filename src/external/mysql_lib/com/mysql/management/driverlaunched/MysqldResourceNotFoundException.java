package com.mysql.management.driverlaunched;

public class MysqldResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MysqldResourceNotFoundException(String message) {
        super(message);
    }
}
