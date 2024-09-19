package com.record.student.sevice;

import com.record.student.model.Admin;
import com.record.student.repo.AdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepo adminRepo;

    public Optional<Admin> getAdminById(int id) {
        return this.adminRepo.findById(id);
    }

    public Admin createAdmin(Admin admin) {
        return this.adminRepo.save(admin);
    }

    public Optional<Admin> getAdminByName(String name) {
        return this.adminRepo.findByName(name);
    }

    public List<Admin> getAllAdmins() {
        return this.adminRepo.findAll();
    }

    public void deleteAdminById(int id) {
        this.adminRepo.deleteById(id);
    }

}
