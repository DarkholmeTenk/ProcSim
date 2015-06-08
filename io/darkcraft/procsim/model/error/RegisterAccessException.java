package io.darkcraft.procsim.model.error;

public class RegisterAccessException extends RuntimeException
{
	private static final long	serialVersionUID	= 7727722160229402781L;

	public RegisterAccessException(String message)
	{
		super(message);
	}
}
