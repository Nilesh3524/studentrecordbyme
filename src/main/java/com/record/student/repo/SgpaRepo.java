package com.record.student.repo;

import com.record.student.model.Sgpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SgpaRepo extends JpaRepository<Sgpa,Integer> {

    Sgpa findByStudentRollNo(String rollNo);

}
