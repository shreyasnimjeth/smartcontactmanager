package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.dao.UserRepositery;
import com.smart.model.User;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepositery userRepositery;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = this.userRepositery.getUserByUserName(username);

		if (user == null) {

			throw new UsernameNotFoundException("Could not found user");
		}

		CustomUserDetails customUserDetails = new CustomUserDetails(user);

		return customUserDetails;
	}

}
