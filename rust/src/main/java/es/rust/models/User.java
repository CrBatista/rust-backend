package es.rust.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="USER")
public class User {

	@Id
	@GeneratedValue
	@Column(name="ID", length=12)
	private Long id;
	
	@Column(name="USERNAME", nullable=false, length=55)
	private String username;
	
	@Column(name="SALT", nullable=false, length=15)
	private String salt;
	
	@Column(name="PASSWORD_ENCODED", nullable=false, length=255)
	private String passwordEncoded;
	
	@Column(name="EMAIL", nullable=true, length=255)
	private String email;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getPasswordEncoded() {
		return passwordEncoded;
	}

	public void setPasswordEncoded(String passwordEncoded) {
		this.passwordEncoded = passwordEncoded;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
	
}
