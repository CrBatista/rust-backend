package es.rust.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;

import es.rust.helper.RustHelper;
import es.rust.models.User;
import es.rust.repositories.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository _userRepository;
	
	private ShaPasswordEncoder _passwordEncoder;
	
	public UserService () {
		_passwordEncoder = new ShaPasswordEncoder(256);
	}
	
	public User findByUsernameAndPassword(User user) {
		user.setUsername("Cristian");
		user.setPasswordEncoded("rust");
		String passwordEncoded = _passwordEncoder.encodePassword(user.getPasswordEncoded(), _getSalt(user));
		return _userRepository.findByUsernameAndPasswordEncoded(user.getUsername(), passwordEncoded);
	}
	
	
	private String _getSalt(User user) {
		User databaseUser = _userRepository.findByUsername(user.getUsername());
		if (RustHelper.isNullOrBlank(databaseUser)) {
			return null;
		} else {
			return databaseUser.getSalt();
		}
	}
}
