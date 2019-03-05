package com.liteon.icgwearable.exception;

public class RecordAlreadyExistsException extends ICGException
{
    private static final long serialVersionUID = -6564745793834797186L;
    
    public RecordAlreadyExistsException(String msg)
    {
        super(msg);
    }
}
