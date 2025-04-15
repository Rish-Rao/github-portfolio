
package com.example.cloudcomputingA1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.cloudcomputingA1.entity.User;
import com.example.cloudcomputingA1.repository.UserRepository;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String showLoginForm() {
  
    	System.out.println("check");
       
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("email") @RequestParam String email, @RequestParam String password,
            final Model model, 
            final RedirectAttributes redirectAttributes) {
    
    	
    		
    		User savedUser = userRepository.getUserByEmail(email);
    		
   
	        if (savedUser == null || !savedUser.getPassword().equals(password)) {
	            model.addAttribute("errorMessage", "Invalid username or password");
	            return "login";
	        }
	        
	        redirectAttributes.addFlashAttribute("email", email);
	       
	        return "redirect:/main";
    	
    
    }
}




