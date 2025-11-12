package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepositery;
import com.smart.helper.Message;
import com.smart.model.User;

@Controller
public class HomeController {

	// bcrypt password encoder

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepositery userRepositery;

	// home page
	@RequestMapping("/")
	public String home(Model m) {
		m.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

	// about page
	@RequestMapping("/about")
	public String about(Model m) {
		m.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}

	// signup page
	@RequestMapping("/signup")
	public String signup(Model m) {
		m.addAttribute("title", "Register - Smart Contact Manager");
		m.addAttribute("user", new User());
		return "signup";
	}

	// handler for registering user
	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model m,
			HttpSession session) {

		try {

			if (!agreement) {
				System.out.println("You have not aggree the terms and conditions");
				throw new Exception("You have not aggree the terms and conditions");
			}

			if (bindingResult.hasErrors()) {
				m.addAttribute("user", user);
				return "signup";
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");

			user.setPassword(passwordEncoder.encode(user.getPassword()));

			User result = this.userRepositery.save(user);

			System.out.println("Agreement " + agreement);
			System.out.println("USER " + result);

			m.addAttribute("user", new User());

			session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));
			return "signup";

		} catch (Exception e) {

			e.printStackTrace();
			m.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong. " + e.getMessage(), "alert-danger"));
			return "signup";
		}

	}

	// handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model m) {

		m.addAttribute("title", "Login Page");
		return "login";
	}

}
