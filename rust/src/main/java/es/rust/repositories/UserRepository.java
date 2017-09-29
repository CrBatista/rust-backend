package es.rust.repositories;

import org.springframework.data.repository.CrudRepository;

import es.rust.models.User;

public interface UserRepository extends CrudRepository<User, Long> {

	public User findByUsername(String username);
	
	public User findByUsernameAndPasswordEncoded(String username, String passwordEncoded);
}
