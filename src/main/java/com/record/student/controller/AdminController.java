package com.record.student.controller;

import com.record.student.helper.Message;
import com.record.student.model.*;
import com.record.student.sevice.CertificateService;
import com.record.student.sevice.FileService;
import com.record.student.sevice.SgpaFileService;
import com.record.student.sevice.StudentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private FileService fileService;

    @Autowired
    private SgpaFileService sgpaFileService;

    @Autowired
    private CertificateService certificateService;



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @ModelAttribute
    public void getAllStudents(Model m){

        List<Student> students = this.studentService.getAllStudents();

        m.addAttribute("students",students);

    }




    // Display all files on a page
    @ModelAttribute
    public void listAllFiles(Model model) throws IOException {
        List<File> files = this.fileService.getAllFiles();
        model.addAttribute("files", files);
    }


    // home page
    @GetMapping("/dashboard")
    public String dashboard(Model m) {

        m.addAttribute("title", "DASHBOARD");

        return "admin/home";
    }

    // admin login page
    @GetMapping("/login")
    public String login() {

        return "admin/login";
    }

    // add student page
    @GetMapping("/add-student")
    public String addStudentForm(Model m) {

        m.addAttribute("title", "Add Student");

        m.addAttribute("s", new Student());


        return "admin/addStudent";
    }

    // process add student
    @PostMapping("/process-add-student")
    public String addStudent(@Valid @ModelAttribute("s") Student student, BindingResult result, @RequestParam("certificates") MultipartFile certificates, HttpSession session, Model m) throws IOException {

//        System.out.println(student);

        if (result.hasErrors()) {

            m.addAttribute("s", student);

            session.setAttribute("message", new Message("alert-danger", "Something went wrong !!"));

            return "admin/addStudent";
        }

        if (this.studentService.isStudentExits(student.getRollNo())
                || this.studentService.isStudentExitsByEmail(student.getEmail())) {
            m.addAttribute("s", student);
            session.setAttribute("message", new Message("alert-danger", "Student Already Exists !!"));
            return "admin/addStudent";
        }

        student.setPassword(passwordEncoder().encode(student.getEmail()));

        student.setRole("STUDENT");


        Certificate certificate = new Certificate(certificates.getContentType(), certificates.getOriginalFilename(), certificates.getBytes(), student);

        student.setCertificate(certificate);

        this.studentService.addStudent(student);


        session.setAttribute("message", new Message("alert-success", "Student Added successfully"));

        return "redirect:all-students";

    }


    // add sgpa page
    @GetMapping("/add-sgpa")
    public String addSGPAForm() {

        return "admin/addSGPA";


    }


    //process add sgpa
    @PostMapping("/process-add-sgpa")
    public String processAddSgpa(@RequestParam("rollNo") String rollNo,
                                 @RequestParam("sgpa1") MultipartFile sgpa1,
                                 @RequestParam("sgpa2") MultipartFile sgpa2,
                                 @RequestParam("sgpa3") MultipartFile sgpa3,
                                 @RequestParam("sgpa4") MultipartFile sgpa4,
                                 @RequestParam("sgpa5") MultipartFile sgpa5,
                                 @RequestParam("sgpa6") MultipartFile sgpa6,
                                 @RequestParam("sgpa7") MultipartFile sgpa7,
                                 @RequestParam("sgpa8") MultipartFile sgpa8, HttpSession session) throws IOException {


        if (this.studentService.isStudentExits(rollNo)) {

            Student student = this.studentService.getStudentByRollNo(rollNo).get();

            if (student.getSgpa().isEmpty()) {


                SgpaFile SGPA1 = new SgpaFile(sgpa1.getOriginalFilename(), sgpa1.getContentType(), sgpa1.getBytes(), student);
                SgpaFile SGPA2 = new SgpaFile(sgpa2.getOriginalFilename(), sgpa2.getContentType(), sgpa2.getBytes(), student);
                SgpaFile SGPA3 = new SgpaFile(sgpa3.getOriginalFilename(), sgpa3.getContentType(), sgpa3.getBytes(), student);
                SgpaFile SGPA4 = new SgpaFile(sgpa4.getOriginalFilename(), sgpa4.getContentType(), sgpa4.getBytes(), student);
                SgpaFile SGPA5 = new SgpaFile(sgpa5.getOriginalFilename(), sgpa5.getContentType(), sgpa5.getBytes(), student);
                SgpaFile SGPA6 = new SgpaFile(sgpa6.getOriginalFilename(), sgpa6.getContentType(), sgpa6.getBytes(), student);
                SgpaFile SGPA7 = new SgpaFile(sgpa7.getOriginalFilename(), sgpa7.getContentType(), sgpa7.getBytes(), student);
                SgpaFile SGPA8 = new SgpaFile(sgpa8.getOriginalFilename(), sgpa8.getContentType(), sgpa8.getBytes(), student);

                student.getSgpa().addAll(List.of(SGPA1, SGPA2, SGPA3, SGPA4, SGPA5, SGPA6, SGPA7, SGPA8));

                this.sgpaFileService.storeSgpaFile(SGPA1);
                this.sgpaFileService.storeSgpaFile(SGPA2);
                this.sgpaFileService.storeSgpaFile(SGPA3);
                this.sgpaFileService.storeSgpaFile(SGPA4);
                this.sgpaFileService.storeSgpaFile(SGPA5);
                this.sgpaFileService.storeSgpaFile(SGPA6);
                this.sgpaFileService.storeSgpaFile(SGPA7);
                this.sgpaFileService.storeSgpaFile(SGPA8);

                session.setAttribute("message", new Message("alert-success", "SGPA Added successfully for student " + student.getRollNo()));

                return "redirect:/admin/all-students";

            } else {

                session.setAttribute("message", new Message("alert-warning", "Student " + rollNo + " record Already have Sgpa record !! you can remove record to add again."));

                return "admin/addSGPA";

            }


        } else {

            session.setAttribute("message", new Message("alert-danger", "Student " + rollNo + " record doesn't exist"));

            return "admin/addSGPA";

        }

    }


    // add attendence page
    @GetMapping("/add-attendence")
    public String addAttendenceForm() {

        return "admin/addAttendence";

    }

    // process add attendence
    @PostMapping("/process-add-attendence")
    public String processAddAttendence(@ModelAttribute("attendence") Attendence attendence,
                                       @RequestParam("rollNo") String rollNo, HttpSession session, Model m) {


        if (this.studentService.isStudentExits(rollNo)) {

            Student student = this.studentService.getStudentByRollNo(rollNo).get();

            if (student.getAttendence() == null) {

                attendence.setStudent(student);

                student.setAttendence(attendence);

                double avgAttendence = (attendence.getA1() + attendence.getA2() + attendence.getA3() + attendence.getA4()
                        + attendence.getA5() + attendence.getA6() + attendence.getA7() + attendence.getA8()) / 8;

                student.setAvgAttendence(Math.round(avgAttendence));

                this.studentService.addStudent(student);

                session.setAttribute("message", new Message("alert-success", "Attendence Added successfully for student: " + rollNo));


                return "redirect:all-students";


            } else {

                session.setAttribute("message", new Message("alert-warning", "Student " + rollNo + " record Already have Attendence record !! you can update it."));

                return "admin/addAttendence";

            }


        } else {

            session.setAttribute("message", new Message("alert-danger", "Student " + rollNo + " record doesn't exists !!"));

            return "admin/addAttendence";
        }

    }


    // cources page
    @GetMapping("/cources")
    public String cources() {
        return "admin/cources";
    }

    //all students page
    @GetMapping("/all-students")
    public String allStudents() {
        return "admin/allStudents";
    }


    //Backlog students page
    @GetMapping("/backlog-students")
    public String backlogStudents() {
        return "admin/backlogStudents";
    }


    //delete student
    @GetMapping("/student/delete/{rollNo}")
    public String deleteStudent(@PathVariable String rollNo, HttpSession session) {

//        System.out.println(rollNo);

        if (this.studentService.isStudentExits(rollNo)) {

            this.studentService.deleteStudent(rollNo);

            session.setAttribute("message", new Message("alert-warning", "Student record deleted Successfully"));


            return "redirect:/admin/all-students";

        } else {

            session.setAttribute("message", new Message("alert-danger", "Student " + rollNo + " record doesn't exists !!"));

            return "redirect:dashboard";

        }

    }

    //delete sgpa
    @GetMapping("/student/record/delete/{rollNo}")
    public String deleteStudentRecord(@PathVariable String rollNo, HttpSession session) {

        if (this.studentService.isStudentExits(rollNo)) {


            List<SgpaFile> sgpa = this.sgpaFileService.getSgpaFileByRollNo(rollNo);

            for (SgpaFile sgpaFile : sgpa) {
                this.sgpaFileService.deleteSgpaFile(sgpaFile);
            }

            session.setAttribute("message", new Message("alert-warning", "Record deleted Successfully"));

            return "redirect:/admin/student/details/"+rollNo;

        }else {

            session.setAttribute("message", new Message("alert-danger", "Student " + rollNo + " record doesn't exists !!"));

            return "redirect:dashboard";

        }


    }

    //student update

    @PostMapping("/student/update")
    @ResponseBody // This ensures the method returns JSON, not a view.
    public Map<String, Object> updateStudent(@ModelAttribute Student student) {


        // Fetch the existing student by roll number
        Optional<Student> optionalStudent = this.studentService.getStudentByRollNo(student.getRollNo());


        // Prepare a response map
        Map<String, Object> response = new HashMap<>();

        if (optionalStudent.isPresent()) {

            Student oldStudent = optionalStudent.get();

            // Update the student details
            oldStudent.setBacklog(student.isBacklog());
            oldStudent.setBackSub(student.getBackSub());
            oldStudent.setCurrentBacklog(student.getCurrentBacklog());

            // Save the updated student back to the database
            this.studentService.addStudent(oldStudent);

            // Return success response
            response.put("success", true);
            response.put("message", "Student updated successfully.");

        } else {
            // If student is not found, return failure response
            response.put("success", false);
            response.put("message", "Student not found.");
        }

        return response; // Returning JSON response
    }


    // update attendence

    @PostMapping("/student/update/attendence")
    @ResponseBody // This ensures the method returns JSON, not a view.
    public Map<String, Object> updateAttendence(@ModelAttribute Attendence attendence, @RequestParam("sRollNo") String rollNo) {


        // Fetch the existing student by roll number
        Optional<Student> optionalStudent = this.studentService.getStudentByRollNo(rollNo);


        // Prepare a response map
        Map<String, Object> response = new HashMap<>();

        if (optionalStudent.isPresent()) {

            Student oldStudent = optionalStudent.get();

            // Update the attendence details
            oldStudent.setAttendence(attendence);

            attendence.setStudent(oldStudent);

            //calculate avg
            double avgAttendence = (attendence.getA1() + attendence.getA2() + attendence.getA3() + attendence.getA4()
                    + attendence.getA5() + attendence.getA6() + attendence.getA7() + attendence.getA8()) / 8;

            oldStudent.setAvgAttendence(Math.round(avgAttendence));


            // Save the updated student back to the database
            this.studentService.addStudent(oldStudent);

            // Return success response
            response.put("success", true);

            response.put("message", "Attendence updated successfully.");

        } else {
            // If student is not found, return failure response
            response.put("success", false);
            response.put("message", "Student not found.");
        }

        return response; // Returning JSON response
    }

    //upload result
    @GetMapping("/upload-result")
    public String uploadResultPage() {


        return "admin/uploadResult";
    }

    //process upload result form
    @PostMapping("/process-upload-result")
    public String uploadResult(@RequestParam("name") String name, @RequestParam("image") MultipartFile file, HttpSession session) {

        try {

            this.fileService.storeFile(file, name);

            session.setAttribute("message", new Message("alert-success", "Result uploaded Successfully"));

            return "redirect:dashboard";

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    //students details page
    @GetMapping("/student/details/{rollNo}")
    public String studentDetails(@PathVariable String rollNo, HttpSession session, Model m) {

        if (this.studentService.isStudentExits(rollNo)) {

            Student student = this.studentService.getStudentByRollNo(rollNo).get();

            List<SgpaFile> sgpa = this.sgpaFileService.getSgpaFileByRollNo(rollNo);

            System.out.println(sgpa);


            m.addAttribute("s", student);

            m.addAttribute("sgpa", sgpa);

            return "admin/studentDetails";

        }else {

            session.setAttribute("message", new Message("alert-danger", "Student record doesn't exists !!"));

            return "redirect:all-students";
        }

    }


    //delete result
    @GetMapping("/file/delete/{fileId}")
    public String deleteFile(@PathVariable int fileId,HttpSession session) throws IOException {

        this.fileService.deleteFile(fileId);

        session.setAttribute("message", new Message("alert-success", "File deleted successfully"));

        return "redirect:/admin/dashboard";
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


    //download certificate
    @GetMapping("/certificate/download/{rollNo}")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable String rollNo) throws IOException {

        Certificate certificate = this.certificateService.getCertificateByRollNo(rollNo).get();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(certificate.getType()))  // Set the correct content type
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + certificate.getName() + "\"")
                .body(certificate.getData());
    }


    //download sgpa
    @GetMapping("/sgpa/download/{rollNo}/{id}")
    public ResponseEntity<byte[]> downloadSgpa(@PathVariable("rollNo") String rollNo,@PathVariable("id") int id) throws IOException {

        SgpaFile sgpa = this.sgpaFileService.getSgpaFileByRollNoAndId(rollNo, id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(sgpa.getType()))  // Set the correct content type
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + sgpa.getName() + "\"")
                .body(sgpa.getData());
    }



}
