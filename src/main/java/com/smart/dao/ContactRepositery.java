package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.model.Contact;
import com.smart.model.User;

public interface ContactRepositery extends JpaRepository<Contact, Integer> {

	// implementing pegination useing contact repositery so we created this repo

	@Query("from Contact as c where c.user.id =:userId ")
	public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);

	// pageable information -> current page, contact per page 5

	// searching
	public List<Contact> findByNameContainingAndUser(String name, User user);

}
