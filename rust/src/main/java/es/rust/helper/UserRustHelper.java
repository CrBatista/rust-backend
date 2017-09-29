package es.rust.helper;

import javax.servlet.http.HttpServletRequest;

import es.rust.models.User;

public class UserRustHelper {

	private static final String MISSING_REQUEST_ERROR_MESSAGE = "Cannot construct without Request parameter";
	private static final String USERNAME_HEADER = "username";
	private static final String PASSWORD_HEADER = "password";
	private HttpServletRequest request;
	
	public UserRustHelper() {
		throw new AssertionError(MISSING_REQUEST_ERROR_MESSAGE);
	}
	
	/**
	 * UserHelper constructor
	 * 
	 * @param request Mandatory param
	 * @throws AssertionError if param is not present
	 * @author Cristian Batista Herrera <cristianbatista@outlook.es>
	 */
	public UserRustHelper(HttpServletRequest request) {
		if (request == null) {
			throw new AssertionError(MISSING_REQUEST_ERROR_MESSAGE);
		}
		this.request = request;
	}
	
	public User findDataFromRequest() {
		User databaseUser = new User();
		databaseUser.setUsername(request.getHeader(USERNAME_HEADER));
		databaseUser.setUsername(request.getHeader(PASSWORD_HEADER));
		return databaseUser;
	}
	
	
}
