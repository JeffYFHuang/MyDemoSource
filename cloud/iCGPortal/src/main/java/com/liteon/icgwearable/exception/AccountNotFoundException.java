package com.liteon.icgwearable.exception;

public class AccountNotFoundException extends ICGException {
	private static final long serialVersionUID = 4998423119660262662L;

	    public AccountNotFoundException()
	    {
	    }

	    public AccountNotFoundException(String message )
	    {
	        super(message);
	    }

	    public AccountNotFoundException(Throwable cause )
	    {
	        super(cause);
	    }
}
