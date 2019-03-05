package com.liteon.icgwearable.exception;

public class UserNotFoundException extends ICGException {

	private static final long serialVersionUID = 4998423119660262662L;

    public UserNotFoundException()
    {
    }

    public UserNotFoundException(String message )
    {
        super(message);
    }

    public UserNotFoundException(Throwable cause )
    {
        super(cause);
    }
}
