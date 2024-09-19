package com.record.student.repo;

import com.record.student.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepo extends JpaRepository<Admin, Integer> {

    Optional<Admin> findByName(String name);

}
