package com.liteon.icgwearable.exception;
public class ICGException extends RuntimeException
{
    private static final long serialVersionUID = 3861316472242161156L;

    public ICGException()
    {
    }

    /**
     * @param message
     */
    public ICGException( String message )
    {
        super(message);
    }

    /**
     * @param cause
     */
    public ICGException( Throwable cause )
    {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public ICGException( String message, Throwable cause )
    {
        super(message, cause);
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public ICGException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace )
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
