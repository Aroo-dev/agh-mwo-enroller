package com.company.enroller.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;


@Entity
@Table(name = "participant")
public class Participant {

	@Id
	@NotEmpty(message = "Login cannot be empty")
	private String login;

	@Column
	@JsonIgnore
	@NotEmpty(message = "Password cannot be empty")
	@Size(min = 8, message = "Password must be at least 8 characters long")
	private String password;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = new BCryptPasswordEncoder().encode(password);
	}
}
