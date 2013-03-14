package me.sh4rewith.web.forms;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import me.sh4rewith.utils.DigestUtils;
import me.sh4rewith.utils.validation.Email;
import me.sh4rewith.utils.validation.FieldMatch;

import org.hibernate.validator.constraints.NotEmpty;

@FieldMatch.List({
		@FieldMatch(first = "password", second = "confirmedPassword", message = "The password fields must match")
})
public class UserRegistration {
	@NotNull
	@NotEmpty
	@Size(min = 5, max = 20)
	private String id;
	@NotNull
	@NotEmpty
	@Email
	private String email;
	@NotNull
	@NotEmpty
	@Size(min = 1, max = 256)
	private String firstname;
	@NotNull
	@Size(min = 1, max = 256)
	private String lastname;
	@NotNull
	@NotEmpty
	private String password;
	@NotNull
	@NotEmpty
	private String confirmedPassword;

	public void setPassword(String password) {
		if (password != null && !password.equals("")) {
			this.password = DigestUtils.sha256Hex(password);
		} else {
			this.password = "";
		}
	}

	public void setConfirmedPassword(String confirmedPassword) {
		if (confirmedPassword != null && !confirmedPassword.equals("")) {
			this.confirmedPassword = DigestUtils.sha256Hex(confirmedPassword);
		} else {
			this.confirmedPassword = "";
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstName) {
		this.firstname = firstName;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastName) {
		this.lastname = lastName;

	}

	public String getPassword() {
		return password;
	}

	public String getConfirmedPassword() {
		return confirmedPassword;
	}

}
