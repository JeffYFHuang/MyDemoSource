package com.liteon.icgwearable.exception;

public class MailNotSendException extends ICGException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3726169262053612496L;
	public MailNotSendException()
    {}
    public MailNotSendException(String message )
    {
        super(message);
    }
    public MailNotSendException(Throwable cause )
    {
        super(cause);
    }
}
