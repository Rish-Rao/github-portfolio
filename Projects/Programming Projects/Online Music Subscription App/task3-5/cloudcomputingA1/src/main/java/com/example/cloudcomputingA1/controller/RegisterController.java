package com.example.cloudcomputingA1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.cloudcomputingA1.entity.User;
import com.example.cloudcomputingA1.repository.UserRepository;

@Controller
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/register")
    public String showRegisterForm() {
       
        return "register";
    }
    
    
    @PostMapping("/register")
    public String registerUser(@RequestParam String email, @RequestParam String username, @RequestParam String password, Model model) {
	    	User userCheckUser = userRepository.getUserByEmail(email);
	    	if (userCheckUser != null) {
	            model.addAttribute("errorMessage", "Email already registered");
	            return "register";
	        }
	    	userRepository.save(new User(email, username, password));
	    	return "login";
    }
}
    	
    	










