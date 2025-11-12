package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepositery;
import com.smart.dao.MyOrderRepositery;
import com.smart.dao.UserRepositery;
import com.smart.helper.Message;
import com.smart.model.Contact;
import com.smart.model.MyOrder;
import com.smart.model.User;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;
	
	@Autowired
	private UserRepositery userRepositery;

	@Autowired
	private ContactRepositery contactRepositery;
	
	@Autowired
	private MyOrderRepositery myOrderRepo;

	// method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model m, Principal principal) {

		String userName = principal.getName();
//		System.out.println(userName);

		// get the user using username

		User user = this.userRepositery.getUserByUserName(userName);

//		System.out.println("User: " + user);

		m.addAttribute("user", user);
	}

	// dashboard home
	@RequestMapping("/index")
	public String dashboard(Model m, Principal principal) {

		m.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model m) {

		m.addAttribute("title", "Add Contact");
		m.addAttribute("contact", new Contact());

		return "normal/add_contact_form";
	}

	// processsing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {

		try {

			String name = principal.getName();
			User user = this.userRepositery.getUserByUserName(name);

			// processing and uploading file......

			if (file.isEmpty()) {

				// is there any empty file
				System.out.println("File is empty");

				contact.setImage("contact.png");

			} else {
				// upload file to folder and update name to contact

				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is uploaded");
			}

			contact.setUser(user);
			user.getContacts().add(contact);

			this.userRepositery.save(user);

			System.out.println(contact);
			System.out.println("added to database");

			// message success

			session.setAttribute("message", new Message("Your contact is added, add more", "success"));

		} catch (Exception e) {
			System.out.println("error " + e.getMessage());
			e.printStackTrace();

			// message error

			session.setAttribute("message", new Message("Some went wrong, try again", "danger"));
		}

		return "normal/add_contact_form";
	}

	// show contacts handler
	// per page 5[n] contact
	// current page=0 [page]

	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {

		m.addAttribute("title", "Show User Contacts");

		// showing contacts lists
		// we are using contacts repositery for changing contacts we also do pagination
		// in contacts

//		String userName = principal.getName();
//		User user = this.userRepositery.getUserByUserName(userName);
//		List<Contact> contacts = user.getContacts();

		String userName = principal.getName();

		User user = this.userRepositery.getUserByUserName(userName);

		// pageable information -> current page, contact per page 5
		Pageable pageable = PageRequest.of(page, 3);

		Page<Contact> contacts = this.contactRepositery.findContactsByUser(user.getId(), pageable);

		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contacts";
	}

	// showing particular contact details

	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model m, Principal principal) {

		Optional<Contact> contactOptional = this.contactRepositery.findById(cId);

		Contact contact = contactOptional.get();

		//
		String userName = principal.getName();
		User user = this.userRepositery.getUserByUserName(userName);

		if (user.getId() == contact.getUser().getId()) {

			m.addAttribute("contact", contact);
			m.addAttribute("title", contact.getName());
			System.out.println("contact " + contact.getcId());
		}

		return "normal/contact_detail";
	}

	// delete contact handler

	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId, Model m, HttpSession session, Principal principal) {

		Optional<Contact> contactOptional = this.contactRepositery.findById(cId);
		Contact contact = contactOptional.get();

		// check.... task verify if user is correct or not
//		if(p) {
//			
//			
//		}

		/*
		 * First method of deleting contact this is working file we are douing another
		 * method because durgesh thinks that by using unlinking the contact it only
		 * remove contact forgein key and do not delete from database.
		 * 
		 * contact.setUser(null);
		 * 
		 * this.contactRepositery.delete(contact);
		 */

		// second method of deleting by durgesh in my case both method running

		User user = this.userRepositery.getUserByUserName(principal.getName());

		user.getContacts().remove(contact);

		this.userRepositery.save(user);

		session.setAttribute("message", new Message("contact deleted succesfully...", "success"));

		return "redirect:/user/show-contacts/0";
	}

	// open update form
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model m) {

		m.addAttribute("title", "Update Contact");

		Contact contact = this.contactRepositery.findById(cid).get();

		m.addAttribute("contact", contact);

		return "normal/update_form";
	}

	// update contact handler
	@RequestMapping(value = "/process-update", method = RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model m, HttpSession session, Principal principal) {

		try {
			// old contact details

			Contact oldContactDetail = this.contactRepositery.findById(contact.getcId()).get();

			// image..
			if (!file.isEmpty()) {

				// file work rewrite

				// delete old photo

				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldContactDetail.getImage());
				file1.delete();

				// update new photo

				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				contact.setImage(file.getOriginalFilename());

			} else {

				contact.setImage(oldContactDetail.getImage());

			}

			User user = this.userRepositery.getUserByUserName(principal.getName());

			contact.setUser(user);

			this.contactRepositery.save(contact);

			session.setAttribute("message", new Message("Your contact is update successfully...", "success"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("CONTACT " + contact.getName());
		System.out.println("contact id " + contact.getcId());

		return "redirect:/user/" + contact.getcId() + "/contact";
	}

	// your profile
	@GetMapping("/profile")
	public String yourProfile(Model m) {

		m.addAttribute("title", "Profile Page");
		return "normal/profile";
	}
	
	
	//open settings handler
	@GetMapping("/settings")
	public String openSettings() {
		
		
		return "normal/settings";
	}
	
	//change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, 
									Principal principal, HttpSession session) {
		
		System.out.println("old password: "+oldPassword);
		System.out.println("new password: "+newPassword);
		
		String userName = principal.getName();
		User currentUser = this.userRepositery.getUserByUserName(userName);
		
		System.out.println(currentUser.getPassword());
		
		if (this.bcryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			
			//change the password
			
			currentUser.setPassword(this.bcryptPasswordEncoder.encode(newPassword));
			
			this.userRepositery.save(currentUser);
			
			session.setAttribute("message", new Message("Your password has successfully changed....", "success"));
			
		} else {
			//error
			session.setAttribute("message", new Message("Your old password is wrong.... Please enter old correct password", "danger"));
			return "redirect:/user/settings";
			
		}
		
		return "redirect:/user/index";
	}
	
	//creating order for payment
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data, Principal principal) throws Exception{
		
		System.out.println("hey order function executed");
		System.out.println(data);
		
		int amt = Integer.parseInt(data.get("amount").toString());
		
		//from java 11 we can use "var" we can also use "RazorpayClient"
		var client = new RazorpayClient("rzp_test_RdcK0hyEGEaq5u", "bvj4t6GDAsj6D2VyrkEZWaRG");
		
		JSONObject ob = new JSONObject();
		ob.put("amount", amt*100);
		ob.put("currency", "INR");
		ob.put("receipt", "txn_9009");
		
		//creating new order
		
		Order order = client.Orders.create(ob);
		System.out.println(order);
		
		//save order id and information to database
		
		MyOrder myOrder = new MyOrder();
		
		myOrder.setAmount(order.get("amount")+"");
		myOrder.setOrderId(order.get("id"));
		myOrder.setPaymentId(null);
		myOrder.setStatus("created");
		myOrder.setUser(this.userRepositery.getUserByUserName(principal.getName()));
		myOrder.setReceipt(order.get("receipt"));
		
		this.myOrderRepo.save(myOrder);
		
		return order.toString();
	}
	
	@PostMapping("/update_order")
	public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data){
		
		MyOrder myOrder = this.myOrderRepo.findByOrderId(data.get("order_id").toString());
		
		myOrder.setPaymentId(data.get("payment_id").toString());
		myOrder.setStatus(data.get("status").toString());
		
		this.myOrderRepo.save(myOrder);
		
		System.out.println(data);
		return ResponseEntity.ok(Map.of("msg","updated"));
	}
	
	
	
	
	
	
	
	

}
