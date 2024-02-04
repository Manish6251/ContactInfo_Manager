package com.contact.contactInfo.Repository;

import com.contact.contactInfo.entities.Contact;
import com.contact.contactInfo.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact,Integer> {
    //get contact method
    @Query("from Contact as c where c.user.id =:userId")
    // current page=page
    //contact per page=5
    public Page<Contact> findContactByUser(@Param("userId") int userId, Pageable pageable);
    //search method
    public  List<Contact> findByNameContainingAndUser(String name, User user);
}
