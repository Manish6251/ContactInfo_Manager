package com.contact.contactInfo.Controller;

import com.contact.contactInfo.Repository.ContactRepository;
import com.contact.contactInfo.Repository.UserRepository;
import com.contact.contactInfo.entities.Contact;
import com.contact.contactInfo.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    //search handler
    @GetMapping("/search/{query}")
    public ResponseEntity<?> search( @PathVariable("query") String query, Principal principal){
    System.out.println(query);
        User user = this.userRepository.getUserByUserName(principal.getName());
        List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query,user);
      return ResponseEntity.ok(contacts);
    }
}
