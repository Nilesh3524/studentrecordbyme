package com.record.student.sevice;

import com.record.student.model.Certificate;
import com.record.student.repo.CertificateRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepo certificateRepo;


    public Certificate storeCertificate(Certificate certificate) {
        return this.certificateRepo.save(certificate);
    }

    public Optional<Certificate> getCertificateByRollNo(String rollNo) {
        return this.certificateRepo.findByStudentRollNo(rollNo);
    }
}
