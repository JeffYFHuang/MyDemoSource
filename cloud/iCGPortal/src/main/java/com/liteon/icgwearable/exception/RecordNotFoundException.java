package com.liteon.icgwearable.exception;


public class RecordNotFoundException extends ICGException
{
    private static final long serialVersionUID = 4998423119660262662L;

    public RecordNotFoundException()
    {
    }

    public RecordNotFoundException( String message )
    {
        super(message);
    }

    public RecordNotFoundException( Throwable cause )
    {
        super(cause);
    }

    public RecordNotFoundException( String message, Throwable cause )
    {
        super(message, cause);
    }

    public RecordNotFoundException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace )
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
