package com.record.student.sevice;

import com.record.student.model.Sgpa;
import com.record.student.repo.SgpaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SgpaService {

    @Autowired
    private SgpaRepo sgpaRepo;

    public Sgpa saveSgpa(Sgpa sgpa){
        return this.sgpaRepo.save(sgpa);
    }

    public Sgpa getSgpaByRollNo(String rollNo){

        return this.sgpaRepo.findByStudentRollNo(rollNo);

    }

}
