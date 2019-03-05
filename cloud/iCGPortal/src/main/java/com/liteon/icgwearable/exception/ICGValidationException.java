package com.liteon.icgwearable.exception;


public class ICGValidationException extends ICGException
{
    private static final long serialVersionUID = 1755102373827791075L;

    public ICGValidationException()
    {
        super();
    }

    public ICGValidationException( String message )
    {
        super(message);
    }

    public ICGValidationException( Throwable cause )
    {
        super(cause);
    }

    public ICGValidationException( String message, Throwable cause )
    {
        super(message, cause);
    }

    public ICGValidationException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace )
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
