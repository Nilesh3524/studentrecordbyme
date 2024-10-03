package com.record.student.repo;

import com.record.student.model.SgpaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SgpaFileRepo extends JpaRepository<SgpaFile, Integer> {

    @Query("from SgpaFile s where s.student.rollNo = :rollNo ORDER BY s.name ASC")
    List<SgpaFile> findByStudentRollNo(@Param("rollNo") String rollNo);

    @Query("from SgpaFile s where s.student.rollNo = :rollNo and s.id = :id")
    SgpaFile findByStudentRollNoAndId(@Param("rollNo") String rollNo, @Param("id") Integer id);

    @Query("from SgpaFile s where s.name = :name and s.student.rollNo = :rollNo")
    SgpaFile findByNameAndStudentRollNo(String name, String rollNo);

}
