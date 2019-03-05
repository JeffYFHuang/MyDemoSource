package com.liteon.icgwearable.exception;


public class StaleResourceStateException extends ICGException
{
    private final String message;
    private final String resourceState;

    public StaleResourceStateException(String message, String resourceState)
    {
        super(message);
        this.resourceState = resourceState;
        this.message = getMessage();
    }

    /**
     * @return the resourceState
     */
    public String getResourceState()
    {
        return this.resourceState;
    }

    @Override
    public String getMessage()
    {
        return message == null ? new StringBuilder(super.getMessage()).append(":\n").append(getResourceState()).toString() : message;
    }

}
