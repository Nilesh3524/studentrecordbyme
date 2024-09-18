package com.record.student.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Student implements UserDetails {

    @Id
    @Column(unique = true)
    @NotBlank(message = "id is not blank!!")
    private String rollNo;

    @NotBlank(message = "name is not empty !!")
    @Size(min = 3,message = "name should be greater than 3 characters !!")
    private String name;

    private String branch;

    @NotBlank(message = "mention the address!!")
    private String address;

    @Column(unique = true)
    @Email(message = "enter valid email !!")
    @NotBlank(message = "mention the email!!")
    private String email;

    private String password;

    @Size(max = 12,message = "phone must be less than 10 characters !!")
    @NotBlank(message = "mention the phone!!")
    private String phone;

    private boolean forum;

    private boolean backlog;

    private String backSub;

    private String currentBacklog;

    @Size(max = 12,message = "phone must be less than 10 characters !!")
    @NotBlank(message = "mention the parents contact!!")
    private String parents;

    @NotBlank(message = "mention mother name !!")
    private String mother;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true,mappedBy = "student")
    private Certificate certificate;

    private String participation;

    private String year;

    private double avgAttendence;

    private String role;


    //attendence
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true,mappedBy = "student")
    private Attendence attendence;


    //sgpa
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "student")
    private List<SgpaFile> sgpa=new ArrayList<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("STUDENT"));
    }

    @Override
    public String getUsername() {
        return getRollNo();
    }


}
