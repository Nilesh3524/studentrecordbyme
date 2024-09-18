package com.record.student.sevice;

import java.util.List;
import java.util.Optional;

import com.record.student.model.Student;
import com.record.student.repo.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




@Service
public class StudentService {

    @Autowired
    private StudentRepo studentRepo;

    public Student addStudent(Student student){

        return this.studentRepo.save(student);

    }

    public boolean isStudentExits(String rollNo){
        return this.studentRepo.existsById(rollNo);
    }


    public boolean isStudentExitsByEmail(String email){
        return this.studentRepo.existsByEmail(email);
    }


    public void deleteStudent(String rollNo){
        this.studentRepo.deleteById(rollNo);
    }


    public Optional<Student> getStudentByRollNo(String rollNo){
        return this.studentRepo.findByRollNo(rollNo);
    }

    public Optional<Student> getStudentByRollNoAndEmail(String rollNo, String email){
        return this.studentRepo.findByRollNoAndEmail(rollNo, email);
    }

    public Optional<Student> getStudentByEmail(String email){
        return this.studentRepo.findByEmail(email);
    }

    public List<Student> getAllStudents(){
        return this.studentRepo.findAll();
    }

    public List<Student> getStudentsByYear(String year) {
        return studentRepo.findByYear(year);
    }


}