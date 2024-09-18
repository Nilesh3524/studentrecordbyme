package com.record.student.sevice;

import com.record.student.model.SgpaFile;
import com.record.student.repo.SgpaFileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SgpaFileService {

    @Autowired
    private SgpaFileRepo sgpaFileRepo;


    public SgpaFile storeSgpaFile(SgpaFile sgpaFile) {
        return this.sgpaFileRepo.save(sgpaFile);
    }


    public List<SgpaFile> getSgpaFileByRollNo(String rollNo) {
        return this.sgpaFileRepo.findByStudentRollNo(rollNo);
    }

    public SgpaFile getSgpaFileByRollNoAndId(String rollNo, int id){
        return this.sgpaFileRepo.findByStudentRollNoAndId(rollNo, id);
    }

    public void deleteSgpaFile(SgpaFile sgpaFile) {
        this.sgpaFileRepo.delete(sgpaFile);
    }

}
