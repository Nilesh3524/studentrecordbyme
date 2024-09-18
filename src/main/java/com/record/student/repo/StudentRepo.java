package com.record.student.repo;

import java.util.List;
import java.util.Optional;

import com.record.student.model.SgpaFile;
import com.record.student.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;



public interface StudentRepo extends JpaRepository<Student,String>{

    Optional<Student> findByRollNo(String rollNo);

    Optional<Student> findByRollNoAndEmail(String rollNo, String email);

    Optional<Student> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Student> findByYear(String year);


}
