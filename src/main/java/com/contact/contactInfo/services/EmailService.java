package com.contact.contactInfo.services;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {

    public  boolean  sendEmail(String subject,String message,String to){
        //rest of the code..
        boolean f=false;
        String from ="mishramanishkumar029@gmail.com";
        String host = "smtp.gmail.com";

        //get the system properties
        Properties properties = System.getProperties();
        System.out.println("PROPERTIES "+properties);

        //setting important information to properties object
        //host set
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port","587");
        properties.put("mail.smtp.ssl.enable","true");
        properties.put("mail.smtp.auth","true");

        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        //step: 1: to get the session object..
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("mishramanishkumar029@gmail.com", "jfhi htwu uicr okqa");
            }
        });
        session.setDebug(true);

        //step 2:compose the message[text,multimedia]
        MimeMessage mimeMessage = new MimeMessage(session);
        try{
            //from email
            mimeMessage.setFrom(from);

            //adding recipient to message
            mimeMessage.addRecipient(Message.RecipientType.TO,new InternetAddress(to));

            //adding subject to message
            mimeMessage.setSubject(subject);

            //adding text to message
            //mimeMessage.setText(message);
            mimeMessage.setContent(message, "text/html");
            //send
            //step 3: send the message using transport class
            Transport.send(mimeMessage);

            System.out.println("Sent success.........");
            f = true;
        }catch (Exception e){
            e.printStackTrace();

        }
        return f;
    }

}
