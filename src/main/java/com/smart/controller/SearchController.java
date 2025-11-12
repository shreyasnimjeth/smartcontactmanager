package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.ContactRepositery;
import com.smart.dao.UserRepositery;
import com.smart.model.Contact;
import com.smart.model.User;

@RestController
public class SearchController {

	@Autowired
	private UserRepositery userRepositery;

	@Autowired
	private ContactRepositery contactRepositery;

	// search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal) {

		// System.out.println(query);

		User user = this.userRepositery.getUserByUserName(principal.getName());

		List<Contact> contacts = this.contactRepositery.findByNameContainingAndUser(query, user);

		return ResponseEntity.ok(contacts);
	}

}
