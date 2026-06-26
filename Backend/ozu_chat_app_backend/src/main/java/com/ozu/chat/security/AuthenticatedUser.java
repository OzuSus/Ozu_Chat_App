package com.ozu.chat.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import com.ozu.chat.user.model.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticatedUser implements UserDetails {

	private final String id;
	private final String email;
	private final String username;
	private final Set<UserRole> roles;

	public AuthenticatedUser(String id, String email, String username, Set<UserRole> roles) {
		this.id = id;
		this.email = email;
		this.username = username;
		this.roles = Set.copyOf(roles);
	}

	public String id() {
		return id;
	}

	public String email() {
		return email;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream()
				.map(role -> new SimpleGrantedAuthority(role.name()))
				.collect(Collectors.toUnmodifiableSet());
	}

	@Override
	public String getPassword() {
		return "";
	}

	@Override
	public String getUsername() {
		return username;
	}
}
