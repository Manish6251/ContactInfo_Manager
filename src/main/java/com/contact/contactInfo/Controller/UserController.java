package com.contact.contactInfo.Controller;

import com.contact.contactInfo.Helper.Message;
import com.contact.contactInfo.Repository.ContactRepository;
import com.contact.contactInfo.Repository.UserRepository;
import com.contact.contactInfo.entities.Contact;
import com.contact.contactInfo.entities.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    //method for adding common data response
    @ModelAttribute
    public void addCommonData(Model model, Principal principal){
        String userName = principal.getName();
        System.out.println("USERNAME "+userName);
        //get user using username(email)
        User user = userRepository.getUserByUserName(userName);
        System.out.println("USER "+user);
        model.addAttribute("user",user);
    }

    //dashboard home
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal){
      model.addAttribute("title","User Dashboard");
      return "normal/user_dashboard";
    }

    //open add form handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model){
        model.addAttribute("title","Add Contact");
        model.addAttribute("contact",new Contact());
        return "normal/add_contact_form";
    }

    //processing add contact form
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file, Principal principal, HttpSession session){
        try {
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);
            contact.setUser(user);

            //processing and uploading file
            if(file.isEmpty()){

               //if the file is empty then try
               System.out.println("File is empty");
               contact.setImage("contact.png");
            }
            else {
                contact.setImage(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/image").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("image is uploaded");
            }
            user.getContacts().add(contact);

            this.userRepository.save(user);
            System.out.println("DATA " + contact);
            System.out.println("Added to database");
            //message success...
            session.setAttribute("message",new Message("Your contact is added !! Add more..","success"));
        }
        catch(Exception e){
            System.out.println("ERROR "+e.getMessage());
            e.printStackTrace();
            //message error
            session.setAttribute("message",new Message("Something went wrong!! try again..","danger"));

        }
        return "normal/add_contact_form";
    }
    //show contact handler
    @GetMapping("/show_contacts/{page}")
    public String showContacts( @PathVariable("page") Integer page, Model model, Principal principal){
        model.addAttribute("title","Show User Contacts");
        //sending contact list
      String userName= principal.getName();
      User user = this.userRepository.getUserByUserName(userName);
//        List<Contact> contacts = user.getContacts();
        //current page=page
        //contact per page=5
       Pageable pageable =  PageRequest.of(page,2);
       Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(),pageable);
       model.addAttribute("Contacts",contacts);
       model.addAttribute("currentPage",page);
       model.addAttribute("totalPages",contacts.getTotalPages());
       System.out.println("contact showed");
        return "normal/show_contacts";
    }
    //showing particular contact details
    @RequestMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") Integer cId, Model model,Principal principal){
        System.out.println("CID "+cId);
        Optional<Contact> contactOptional= this.contactRepository.findById(cId);
        Contact contact = contactOptional.get();

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        if(user.getId()==contact.getUser().getId()){
            model.addAttribute("title",contact.getName());
            model.addAttribute("contact",contact);
            System.out.println(contact);
        }

        return "normal/contact_detail";

    }

    //delete contact
    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") Integer cId,Model model,Principal principal,HttpSession session){
        Optional<Contact> contactOptional = this.contactRepository.findById(cId);
        Contact contact = contactOptional.get();

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        if(user.getId()==contact.getUser().getId()) {
            System.out.println("Contact "+contact.getcId());
            user.getContacts().remove(contact);
            this.userRepository.save(user);
            session.setAttribute("message",new Message("Contact deleted successfully...","success"));
        }
        return "redirect:/user/show_contacts/0";
    }

    //open update form handler
    @PostMapping("/update_contact/{cId}")
    public String updateForm(@PathVariable("cId") Integer cId, Model model){
        model.addAttribute("title","Update Contact");
        Contact contact = this.contactRepository.findById(cId).get();
         model.addAttribute("contact",contact);
        return "normal/update_form";
    }

    //update contact handler
    @RequestMapping(value = "/process-update", method = RequestMethod.POST)
    public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file, Model model, HttpSession session,Principal principal){
        try{
            //fetch old contact detail
            Contact oldContactDetail = this.contactRepository.findById(contact.getcId()).get();
            if(!file.isEmpty()){
               //rewrite image
              //delete old image
              File deleteFile = new ClassPathResource("static/image").getFile();
              File file1 = new File(deleteFile,oldContactDetail.getImage());
              file1.delete();

               //update new image
               File saveFile = new ClassPathResource("static/image").getFile();
               Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
               Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
               contact.setImage(file.getOriginalFilename());

            }
             else {
                contact.setImage(oldContactDetail.getImage());
            }
            User user = this.userRepository.getUserByUserName(principal.getName());
            contact.setUser(user);
            this.contactRepository.save(contact);
            session.setAttribute("message",new Message("Your contact is updated...","success"));

        }catch (Exception e){
          e.printStackTrace();
          session.setAttribute("message",new Message("Error updating contact. Please try again","danger"));
        }
        System.out.println("CONTACT NAME "+contact.getName());
        System.out.println("CONTACT ID "+contact.getcId());

        return  "redirect:/user/"+contact.getcId()+"/contact";
    }

    //profile handler
    @GetMapping("/profile")
    public String profile(Model model){
        model.addAttribute("title", "Profile Page");
        return "normal/profile";
    }
    //setting handler
    @GetMapping("/settings")
    public String openSettings(){

        return "normal/settings";
    }
    //change password handler
    @PostMapping("/change_password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword, Principal principal,HttpSession session){
      System.out.println("OLD PASSWORD "+oldPassword);
      System.out.println("NEW PASSWORD "+newPassword);
      String userName = principal.getName();
      User currentUser =this.userRepository.getUserByUserName(userName);
      if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())){
          //change password
          currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
          this.userRepository.save(currentUser);
          session.setAttribute("message",new Message("Your password successfully changed","success"));
      }else {
          //error
          session.setAttribute("message",new Message("Please enter correct old password","danger"));
          return "redirect:/user/settings";
      }
      return "redirect:/user/index";
    }
}
