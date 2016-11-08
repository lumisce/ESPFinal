package app.components;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import app.entities.User;
import app.repositories.UserRepository;


@Component
public class UserComponent {
	
	@Autowired
	UserRepository dao;
	@Autowired
	PasswordEncoder passEncoder;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	public String getcreatedDate(User user) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return f.format(user.getCreatedAt().getTime());
	}
	
	public User findUser(String usernameOrEmail) {
		return dao.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
	}
	
	public List<String> validate(String username, String email, String password, String confPassword) {
		List<String> errors = new ArrayList<>();
		if(username.trim().isEmpty() || email.trim().isEmpty()) {
			errors.add("Username and email cannot be empty");
		}
		if(dao.findByUsername(username) != null) {
			errors.add("Username is already in use");
		}
		if(dao.findByEmail(email) != null) {
			errors.add("Email is already in use");
		}
		if(password.length() < 6) {
			errors.add("Password must be at least 6 characters");
		}
		if(!password.equals(confPassword)) {
			errors.add("Password confirmation does not match password");
		}
		return errors;
	}
	
	public void create(String username, String email, String password, boolean isSeller, boolean isAdmin) {
		User user = new User();
		user.setUsername(username);
		user.setEmail(email);
		user.setSeller(isSeller);
		user.setAdmin(isAdmin);
		user.setHashedPassword(passEncoder.encode(password));
		dao.save(user);
	}
	
	public List<String> authenticate(String password, User user) {
		List<String> errors = new ArrayList<>();
		if(user == null || !passEncoder.matches(password, user.getHashedPassword())) {
			errors.add("Invalid Credentials");
		}
		return errors;
	}
	
	public void setSession(HttpServletRequest req, String username) {
		HttpSession session = req.getSession(true);
		session.setAttribute("username", username);
	}
	
	public User currentUser(HttpServletRequest req) {
		HttpSession session = req.getSession(true);
		String username = (String) session.getAttribute("username");
		if(username == null) return null;
		return dao.findByUsername(username);
	}
	
	public boolean isCurrentUser(String username, HttpServletRequest req) {
		return currentUser(req).getUsername().equals(username);
	}
	
	public boolean isLoggedIn(HttpServletRequest req) {
		return currentUser(req) != null;
	}
	
	public boolean isUnauthorized(String username, HttpServletRequest req) {
		return !isLoggedIn(req) || !isCurrentUser(username, req);
	}
}
