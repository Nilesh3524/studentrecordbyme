package com.record.student.repo;

import com.record.student.model.Certificate;
import com.record.student.model.SgpaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CertificateRepo extends JpaRepository<Certificate,Integer> {

    @Query("from Certificate c where c.student.rollNo = :rollNo")
    Optional<Certificate> findByStudentRollNo(@Param("rollNo") String rollNo);

}
