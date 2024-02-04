package com.contact.contactInfo.Controller;

import com.contact.contactInfo.Helper.Message;
import com.contact.contactInfo.Repository.UserRepository;
import com.contact.contactInfo.entities.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @RequestMapping("/home")
    public String home(Model model){
         model.addAttribute("title","Home - Smart Contact Manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model){
        model.addAttribute("title","About - Smart Contact Manager");
        return "about";
    }
    @RequestMapping("/signup")
    public String signup(Model model){
        model.addAttribute("title","Signup - Smart Contact Manager");
        model.addAttribute("user",new User());
        return "signup";
    }

    //custom logout
    @GetMapping("/logout")
    public String logout() {

        return "redirect:/home";
    }
    //Handler for registration user
    @RequestMapping(value = "/do_register",method = RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult bindingResult, @RequestParam(value = "agreement",defaultValue = "false") boolean agreement, Model model, HttpSession session){
       try{
           if (!agreement){
               System.out.println("You have not agreed the terms and conditions");
               throw new Exception("You have not agreed the terms and conditions");
           }
           if(bindingResult.hasErrors()){
               System.out.println("ERROR "+bindingResult.toString());
               model.addAttribute("user",user);
               return "signup";
           }
           user.setRole("ROLE_USER");
           user.setEnabled(true);
           user.setImageUrl("default.png");
           user.setPassword(passwordEncoder.encode(user.getPassword()));

           System.out.println("Agreement "+agreement);
           System.out.println("User"+user);

           User result = this.userRepository.save(user);
           model.addAttribute("user",new User());
           session.setAttribute("message",new Message("Successfully Registered!!","alert-success"));
           return "signup";
       }catch (Exception e){
           e.printStackTrace();
           model.addAttribute("user",user);
           session.setAttribute("message",new Message("Something went wrong !!"+e.getMessage(),"alert-danger"));
           return "signup";
       }

    }

    //handler for custom login
    @GetMapping("/signin")
    public String customLogin(Model model){
        model.addAttribute("title","Login Page");
        return "login";
    }

}
