package es.rust.exceptions;

/**
 * Common Exceptions for project
 * Every other custom exception should extends this one.
 * 
 * @author Cristian Batista Herrera <cristianbatista@outlook.es>
 */
public class RustException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RustException(Exception e){
		super(e);
	}
	
	public RustException(String message) {
		super(message);
	}
	
	public RustException(String header, Throwable cause) {
		super(header, cause);
	}
}
