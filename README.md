# 📱 Smart Contact Manager
A modern full-stack Spring Boot web application that helps users securely save, organize, and manage their personal contacts online — with image uploads, live search, OTP email verification, and Razorpay payment integration. Designed with a clean UI and optimized workflows for seamless contact management.

## 🌐 GitHub Repository
👉 https://github.com/shreyasnimjeth/smartcontactmanager.git

## ✨ Key Features
- Secure Login & Registration (Spring Security + BCrypt)
- Forgot Password with Email OTP Verification
- Create, Update, Delete Contacts
- Profile Image Upload
- Real-Time AJAX Live Search
- Pagination for Contact Listing
- Razorpay Payment Gateway Integration
- Responsive Dashboard (Bootstrap + Thymeleaf)

## 🛠 Tech Stack
- Backend: Spring Boot, Spring Security, JPA
- Frontend: Thymeleaf, Bootstrap, jQuery
- Database: MySQL
- Email: JavaMail API (SMTP)
- Payment: Razorpay API
- Build Tool: Maven

## ⚙️ Setup & Installation

### 1. Clone the Repository
```bash
git clone https://github.com/shreyasnimjeth/smartcontactmanager.git
cd smartcontactmanager
```

### 2. Setup MySQL Database
```sql
CREATE DATABASE smartcontact;
```

Update *application.properties*:
```
spring.datasource.url=jdbc:mysql://localhost:3306/smartcontact
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

### 3. Configure Email SMTP (OTP Reset)
Use Gmail App Password:
```
smtp.gmail.com
port 587
username=your_email
password=your_app_password
```

### 4. Configure Razorpay Keys
Inside controller:
```java
var client = new RazorpayClient("YOUR_KEY", "YOUR_SECRET");
```

### 5. Run the Application
```bash
mvn spring-boot:run
```
Visit → http://localhost:8080

## 📂 Core Modules
- User Authentication & Profile
- Contact Management (CRUD + Image Upload)
- OTP-Based Forgot Password
- Payment Module (Razorpay Orders API)

## 👨‍💻 Author
**Shreyas Nimje**  
GitHub: https://github.com/shreyasnimjeth  
India  

## ⭐ GitHub Description
Smart Contact Manager built with Spring Boot — features secure login, OTP email verification, contact CRUD, image upload, live search, pagination, Razorpay payments, and a responsive dashboard.

