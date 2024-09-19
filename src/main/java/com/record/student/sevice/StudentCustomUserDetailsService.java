package com.record.student.sevice;

import com.record.student.model.Student;
import com.record.student.repo.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private StudentRepo studentRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Student student = this.studentRepo.findByRollNo(username).get();

        if (student == null) {
            throw new UsernameNotFoundException(username+" not found");
        }


        return User
                .builder()
                .username(student.getRollNo())
                .password(student.getPassword())
                .roles("STUDENT")
                .build();
    }
}
