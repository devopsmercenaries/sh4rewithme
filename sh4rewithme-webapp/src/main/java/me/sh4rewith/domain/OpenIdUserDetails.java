package me.sh4rewith.domain;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class OpenIdUserDetails extends User {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3362719067105597082L;

	private String email;
	private String name;
	private boolean newUser;

	public OpenIdUserDetails(String username, Collection<? extends GrantedAuthority> authorities) {
		super(username, "no-password", true, true, true, true, authorities);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isNewUser() {
		return newUser;
	}

	public void setNewUser(boolean newUser) {
		this.newUser = newUser;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name + "-" + email + "-new:" + newUser;
	}
}
