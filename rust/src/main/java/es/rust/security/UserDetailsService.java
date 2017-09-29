package es.rust.security;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;

@Service("userDetailsService")
@SessionScope
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

	@Override
	@Transactional
	public UserDetails loadUserByUsername(final String login) {
		log.info("START - UserDetailsService (loadUserByUsername(final String login = " + login + ")");
		Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		log.info("END - UserDetailsService (loadUserByUsername()");
		return new org.springframework.security.core.userdetails.User(login, "somePassword", grantedAuthorities);
	}

}
