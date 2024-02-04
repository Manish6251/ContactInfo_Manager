package com.contact.contactInfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ContactInfoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContactInfoApplication.class, args);
		System.out.println("server started");
	}

}
