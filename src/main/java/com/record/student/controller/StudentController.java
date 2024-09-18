package com.record.student.controller;

import com.record.student.model.File;
import com.record.student.model.SgpaFile;
import com.record.student.sevice.FileService;
import com.record.student.sevice.SgpaFileService;
import com.record.student.sevice.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {


    @Autowired
    private StudentService studentService;

    @Autowired
    private SgpaFileService sgpaFileService;

    @Autowired
    private FileService fileService;


    //LoggedIn student
    @ModelAttribute
    public void LoggedStudent(Principal principal, Model model){

        if (principal==null) {
            return;
        }

        model.addAttribute("sgpa",this.sgpaFileService.getSgpaFileByRollNo(principal.getName()));

        model.addAttribute("s",this.studentService.getStudentByRollNo(principal.getName()).get());

    }

    // Display all files on a page
    @ModelAttribute
    public void listAllFiles(Model model) throws IOException {
        List<File> files = this.fileService.getAllFiles();
        model.addAttribute("files", files);
    }


    //dashboard page
    @GetMapping("/dashboard")
    public String dashboard(){

        return "student/dashboard";
    }

    //login page
    @GetMapping("/login")
    public String studentLogin(Model m) {

        return "/student/login";
    }


    //profile page
    @GetMapping("/profile")
    public String studentProfile(Model m) {


        return "student/profile";
    }

    //attendance page
    @GetMapping("/attendance")
    public String attendance(Model m) {

        return "student/attendance";
    }

    //courses page
    @GetMapping("/courses")
    public String courses(Model m) {

        return "student/courses";
    }

    //semester result page
    @GetMapping("/sem-result")
    public String studentSemResult(Model m) {

        return "student/sem_result";
    }

    //academic result page
    @GetMapping("/declared-result")
    public String studentAcademicResult(Model m) {
        return "/student/declared_result";
    }

    //download sgpa
    @GetMapping("/sgpa/download/{rollNo}/{id}")
    public ResponseEntity<byte[]> downloadSgpa(@PathVariable("rollNo") String rollNo, @PathVariable("id") int id) throws IOException {

        SgpaFile sgpa = this.sgpaFileService.getSgpaFileByRollNoAndId(rollNo, id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(sgpa.getType()))  // Set the correct content type
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + sgpa.getName() + "\"")
                .body(sgpa.getData());
    }

    //download result
    @GetMapping("/files/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable int fileId) throws IOException {
        File file = this.fileService.getFile(fileId).get();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getType()))  // Set the correct content type
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(file.getData());
    }

}