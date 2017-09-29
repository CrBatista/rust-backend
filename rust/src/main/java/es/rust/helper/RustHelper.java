package es.rust.helper;

/**
 * Provide method for refactoring code
 * 
 * @author Cristian Batista Herrera
 *
 */
public class RustHelper {

	public static Boolean isNullOrBlank(Object value) {
		return (value == null) || (value == "");
	}
	
}
