package com.example.cloudcomputingA1.controller;



import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.catalina.mbeans.SparseUserDatabaseMBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.example.cloudcomputingA1.entity.Music;
import com.example.cloudcomputingA1.entity.Subscription;
import com.example.cloudcomputingA1.entity.User;
import com.example.cloudcomputingA1.repository.MusicRepository;
import com.example.cloudcomputingA1.repository.SubscriptionRepository;
import com.example.cloudcomputingA1.repository.UserRepository;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;


@Controller
public class MainController {
	
	@Autowired
    private UserRepository userRepository;
	@Autowired
	private SubscriptionRepository subscriptionRepository;
	@Autowired
	private MusicRepository musicRepository;
	
	private String email;
	
	

	 @GetMapping("/main")
	    public String showMainPage(@ModelAttribute("email") final String email,
	            final Model model) {
		 this.email = email;
		 model.addAttribute("username", userRepository.getUserByEmail(email).getUsername());
	        return "main";
	        
	    }
	 
	 @GetMapping("/user")
	    public String showUserAreaPage(@ModelAttribute("username") final String userName,
	            final Model model) {
		 model.addAttribute("username", userRepository.getUserByEmail(email).getUsername());
	        return "main";
	    }
	 @GetMapping("/subscription")
	    public String showSubscriptions(Model model) {
	        List<Subscription> subscriptions = subscriptionRepository.getSubscriptions(email);
	        for (Subscription subscription : subscriptions) {
	            String imageUrl = getPresignedUrl(subscription.getArtist().replaceAll(" ", "") + ".jpg");
	            subscription.setImageUrl(imageUrl);
	        }
	        model.addAttribute("songs", subscriptions);
	        return "main";
	    }
	 @PostMapping("/subscription/remove")
	 public String removeSong(@RequestParam("email") String email, @RequestParam("title") String title) {
		    subscriptionRepository.delete(email, title);
		    return "redirect:/subscription";
		}
	 
	 private String getPresignedUrl(String key) {
		 	Regions clientRegion = Regions.US_EAST_1;	
	        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
	        		.withRegion(clientRegion)
	        		.withCredentials(new ProfileCredentialsProvider("default"))
	        		.build();
	        String bucketName = "s3843246-mybucket";
	        

	        // Generate a presigned URL for the object that expires in one hour
	        java.util.Date expiration = new java.util.Date();
	        long expTimeMillis = expiration.getTime();
	        expTimeMillis += 1000 * 60 * 60; // 1 hour
	        expiration.setTime(expTimeMillis);

	        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, key)
	                .withMethod(HttpMethod.GET).withExpiration(expiration);
	        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
	        return url.toString();
	    }
	 
	 @GetMapping("/query")
	 public String query() {
	                    
	     return "main";
	 }
	 
	 @GetMapping("/query/search")
	 public String searchMusic(
	         @RequestParam(name = "title", required = false) String title,
	         @RequestParam(name = "artist", required = false) String artist,
	         @RequestParam(name = "year", required = false) String year,
	         Model model) {
		 
	     List<Music> musicList = null;
	     System.out.println("a");
	     if (!title.isEmpty() && !artist.isEmpty() && !year.isEmpty()) {
	    	
	         musicList = musicRepository.findByTitleAndArtistAndYear(title, artist, year);
	     } else if (!title.isEmpty() && !artist.isEmpty()) {
	    	
	         musicList = musicRepository.findByTitleAndArtist(title, artist);
	     } else if (!title.isEmpty() && !year.isEmpty()) {
	    	 
	         musicList = musicRepository.findByTitleAndYear(title, year);
	     } else if (!artist.isEmpty() && !year.isEmpty()) {
	    	 
	         musicList = musicRepository.findByArtistAndYear(artist, year);
	     } else if (!title.isEmpty()) {
	    	 
	         musicList = musicRepository.findByTitle(title);
	     } else if (!artist.isEmpty()) {
	    	 
	         musicList = musicRepository.findByArtist(artist);
	     } else if (!year.isEmpty()) {
	    	 
	         musicList = musicRepository.findByYear(year);
	     }
	     
	     	 
	    	 model.addAttribute("musicList", musicList);
	     

	     return "main";
	 }
	 
	 @PostMapping("/query/add")
	 public String addSong(@RequestParam("title") String title, @RequestParam("artist") String artist, @RequestParam("year") String year) {
		 Subscription subscription = new Subscription();
		 subscription.setEmail(email);
		 subscription.setTitle(title);
		 subscription.setArtist(artist);
		 subscription.setYear(Integer.parseInt(year));
		 System.out.println("nignaingia");
		 System.out.println(subscription.getEmail());
		 System.out.println(subscription.getTitle());
		 System.out.println(subscription.getArtist());
		 System.out.println(subscription.getYear());
		 subscriptionRepository.save(subscription);
		    
		    return "redirect:/subscription";
		}

	 
	 @GetMapping("/logout")
	 public String logout() {
	     
	     return "login";
	 }

}
