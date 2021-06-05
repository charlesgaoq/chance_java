
package com.eversec.database.sdb.util.exceptions;

public class BaseException extends Exception {

    private static final long serialVersionUID = 5031372191871194775L;
    private int code;
    private String message;

    private BaseException() {

    }

    public BaseException(int code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return code + "----" + message;
    }
}
