package net.aegistudio.api.java;

public class ApiException extends Exception {
	private static final long serialVersionUID = 1L;

	public ApiException(Exception cause) {
		super(cause);
	}
	
	public ApiException(String message) {
		super(message);
	}
}
