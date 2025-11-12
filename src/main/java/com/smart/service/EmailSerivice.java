package com.smart.service;

import java.util.Properties;

import javax.mail.*;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;


@Service
public class EmailSerivice {

	
	public boolean sendEmail(String to, String subject, String text) {

		boolean flag = false;

		// logic
	   // smtp properties
		
		String from = "shreyasnimje865@gmail.com";

		Properties properties = new Properties();
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", true);
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.host", "smtp.gmail.com");

		String username = "shreyasnimje865";
		String password = "posl pwql rlxa rrod";
		
		//String password = "***********";

		// session
		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Message message = new MimeMessage(session);

			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setFrom(new InternetAddress(from));
			message.setSubject(subject);
//			message.setText(text);
			
			message.setContent(text, "text/html");

			Transport.send(message);

			flag = true;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return flag;
	}

	
	
}
