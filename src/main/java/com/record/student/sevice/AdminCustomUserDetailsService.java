package com.record.student.sevice;


import com.record.student.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminCustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminService adminService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        Admin admin = this.adminService.getAdminByName(username).get();


        if (admin == null) {
            throw new UsernameNotFoundException(username+" not found");
        }


        return User
                .builder()
                .username(admin.getUsername())
                .password(admin.getPassword())
                .roles("ADMIN")
                .build();

    }
}
