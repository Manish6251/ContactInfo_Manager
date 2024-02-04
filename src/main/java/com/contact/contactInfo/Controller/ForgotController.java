package com.contact.contactInfo.Controller;

import com.contact.contactInfo.Helper.Message;
import com.contact.contactInfo.Repository.UserRepository;
import com.contact.contactInfo.services.EmailService;
import com.contact.contactInfo.entities.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class ForgotController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping("/forgot")
    public String openEmailForm(){
    return "forgot_email_form";
}

    @PostMapping("/send_otp")
    public String sendOTP(@RequestParam("email") String email, HttpSession session){

        System.out.println("Email "+email);

        //generating otp of 4 digit
        //    Random random = new Random(1000);
       //     int otp = random.nextInt(9999);
        Random random = new Random();
        int otp = random.nextInt(10000 - 1000) + 1000;
        System.out.println("OTP "+otp);

       //write code for sent otp to email
       String subject = "OTP From SCM ";
       String message = " "
               +"<div style='border:1px solid #e2e2e2; padding:20px'>"
               +"OTP is "
               +"<h1>"
               +"<b>"+otp
               +"</n>"
               +"</h1>"
               +"</div>";
       String to = email;

      boolean flag = this.emailService.sendEmail(subject,message,to);
      if (flag){
          session.setAttribute("my_otp",otp);
          session.setAttribute("email",email);
          return "verify_otp";
      }else {
          session.setAttribute("message","Check your email id");
          return "forgot_email_form";
      }
    }

    //verify otp
    @PostMapping("/verify_otp")
    public String verifyOtp(@RequestParam("otp") int otp,HttpSession session){
        int myOtp = (int) session.getAttribute("my_otp");
        String email =(String) session.getAttribute("email");
        if(myOtp==otp){

            //password change form
            User user=this.userRepository.getUserByUserName(email);
            if (user==null) {
                //send error message

                session.setAttribute("message", "user does not exists..!!");
                return "forgot_email_form";

            }
            return  "password_change_form";
        }else {

            session.setAttribute("message","You have entered wrong otp !!");
            return "verify_otp";
        }
    }
    //change password
    @PostMapping("/change_password")
    public String changePassword(@RequestParam("newPassword") String newPassword, HttpSession session, Model model){
        model.addAttribute("title","Change Password");
        //validate password
        if (newPassword == null || newPassword.length() < 8) {
           session.setAttribute("message",new Message("Please input a valid password with at least 8 characters.","danger"));
            return "password_change_form";
        }
     String email = (String) session.getAttribute("email");
     User user = this.userRepository.getUserByUserName(email);
     user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
         this.userRepository.save(user);
         session.invalidate();
         return "redirect:/signin?change=password change successfully...";
    }

}
