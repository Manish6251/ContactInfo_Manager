package com.contact.contactInfo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "CONTACT")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cId;
//    @NotBlank(message = "First name is required")
//    @Size(min=2,max = 20,message = "min 2 and max 20 characters are allowed !!")
    private String name;

//    @NotBlank(message = "Last name is required")
//    @Size(min=2,max = 20,message = "min 2 and max 20 characters are allowed !!")
    private String secondName;

    private String work;
//    @Column(unique = true)
//    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
//    @NotBlank(message = "Email cannot be empty")
    private String email;

//    @Pattern(regexp="^[0-9]{10}$", message="Phone number must be 10 digits")
    private String phone;

    private String image;
    @Column(length = 500)
    private String description;

    @ManyToOne()
    @JsonIgnore
    private User user;

    public int getcId() {
        return cId;
    }

    public void setcId(int cId) {
        this.cId = cId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object obj) {
        return this.cId==((Contact)obj).getcId();
    }
}
