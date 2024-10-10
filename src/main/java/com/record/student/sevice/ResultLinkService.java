package com.record.student.sevice;

import com.record.student.model.ResultLink;
import com.record.student.repo.ResultLinkRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultLinkService {

    @Autowired
    private ResultLinkRepo linkRepo;

    public ResultLink saveLink(ResultLink link){
        return this.linkRepo.save(link);
    }

    public List<ResultLink> getAllLinks(){
        return this.linkRepo.findAll();
    }

    public void deleteLink(int id){
        this.linkRepo.deleteById(id);
    }

    public boolean isExists(int id){
        return this.linkRepo.existsById(id);
    }

}
