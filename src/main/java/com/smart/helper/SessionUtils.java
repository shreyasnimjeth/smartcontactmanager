package com.smart.helper;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component("sessionUtils")
public class SessionUtils {

	public void clearMessage() {
		// Safely get current session
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attr != null) {
			HttpSession session = attr.getRequest().getSession(false);
			if (session != null) {
				session.removeAttribute("message");
			}
		}
	}
}