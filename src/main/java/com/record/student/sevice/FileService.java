package com.record.student.sevice;

import com.record.student.model.File;
import com.record.student.repo.FileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {

    @Autowired
    private FileRepo fileRepo;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    LocalDateTime now = LocalDateTime.now();

    public File storeFile(MultipartFile file,String name) throws IOException {

        return fileRepo.save(new File(name,file.getContentType() ,file.getBytes(),dtf.format(now).toString()));
    }

    public List<File> getAllFiles() throws IOException {
        return fileRepo.findAll();
    }

    public Optional<File> getFile(int id) throws IOException {
        return this.fileRepo.findById(id);
    }

    public void deleteFile(int id) throws IOException {
        this.fileRepo.deleteById(id);
    }
}
