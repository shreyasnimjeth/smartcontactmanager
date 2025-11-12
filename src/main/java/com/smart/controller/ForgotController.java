package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepositery;
import com.smart.model.User;
import com.smart.service.EmailSerivice;

@Controller
public class ForgotController {
	
	@Autowired
	private EmailSerivice emailService;
	
	@Autowired
	private UserRepositery userRepositery;
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	Random random = new Random(1000);
	
	
	//email if form open handler
	@RequestMapping("/forgot")
	public String openEmailForm() {
		
		return "forgot_email_form";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session) {
		
		System.out.println("email "+email);
		
		//generating otp of 4 digit
		
		int otp = random.nextInt(9999);
		
		System.out.println("otp "+otp);
		
		//send email service code
		
		String subject = "OTP From SCM";
		String text = ""
				+ "<div style = 'border:1px solid #e2e2e2; padding:20px'>"
				+ "<h1>"
				+ "OTP is "
				+ "<b>" +otp
				+ "</b>"
				+ "</h1>"
				+ "</div>";
		String to = email;
		
		boolean flag = this.emailService.sendEmail(to, subject, text);
		
		if(flag) {
			
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		} else {
			
			session.setAttribute("message", "check your email if is correct or not");
			return "forgot_email_form";
		}
		
		
	}
	
	//verify otp
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession session) {
		
		int myOtp = (int)session.getAttribute("myotp");
		String email = (String)session.getAttribute("email");
		
		if(myOtp == otp) {
			
			//password change form
			
			User user = this.userRepositery.getUserByUserName(email);
			
			if(user==null) {
				
				//send error message
				session.setAttribute("message", "User does not exist with this email");
				return "forgot_email_form";
				
			}else {
				
				//send password change password form
			}
			
			return "password_change_form";
			
		}else {
			
			session.setAttribute("message", "you have entered wrong otp...");
			return "verify_otp";
		}
		
		
	}
	
	
	//change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword, HttpSession session) {
		
		String email = (String)session.getAttribute("email");
		User user = this.userRepositery.getUserByUserName(email);
		user.setPassword(this.bcrypt.encode(newpassword));
		this.userRepositery.save(user);
		
		return "redirect:/signin?change=password changed successfully....";
	}
}
