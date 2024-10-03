package com.record.student.controller;

import com.record.student.helper.EmailMessage;
import com.record.student.helper.Message;
import com.record.student.model.*;
import com.record.student.sevice.*;
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

    @Autowired
    private EmailService emailService;

    @Autowired
    private AdminService adminService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @ModelAttribute
    public void getAllStudents(Model m) {

        List<Student> students = this.studentService.getAllStudents();

        m.addAttribute("students", students);

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

    // add sgpa page
    @GetMapping("/add-sgpa")
    public String addSGPAForm() {

        return "admin/sgpa";
    }

    // process add student
    @PostMapping("/process-add-student")
    public String addStudent(@Valid @ModelAttribute("s") Student student, BindingResult result,
                             @RequestParam("certificates") MultipartFile certificates, HttpSession session, Model m) throws IOException {

        // System.out.println(student);

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

        Certificate certificate = new Certificate(certificates.getContentType(), certificates.getOriginalFilename(),
                certificates.getBytes(), student);

        student.setCertificate(certificate);

        this.studentService.addStudent(student);

        session.setAttribute("message", new Message("alert-success", "Student Added successfully"));

        return "redirect:all-students";

    }

    // process add sgpa
    @PostMapping("/process-add-sgpa")
    public String processAddSgpa(@RequestParam("rollNo") String rollNo,
                                 @RequestParam(value = "sgpa1", required = false) MultipartFile sgpa1,
                                 @RequestParam(value = "sgpa2", required = false) MultipartFile sgpa2,
                                 @RequestParam(value = "sgpa3", required = false) MultipartFile sgpa3,
                                 @RequestParam(value = "sgpa4", required = false) MultipartFile sgpa4,
                                 @RequestParam(value = "sgpa5", required = false) MultipartFile sgpa5,
                                 @RequestParam(value = "sgpa6", required = false) MultipartFile sgpa6,
                                 @RequestParam(value = "sgpa7", required = false) MultipartFile sgpa7,
                                 @RequestParam(value = "sgpa8", required = false) MultipartFile sgpa8, HttpSession session) throws IOException {

        if (this.studentService.isStudentExits(rollNo)) {

            Student student = this.studentService.getStudentByRollNo(rollNo).get();


            Optional.ofNullable(sgpa1).ifPresent(sg1 -> {

                Optional.ofNullable(this.sgpaFileService.getSgpaFileByNameAndRollNo(sgpa1.getName(), student.getRollNo())).ifPresentOrElse(pre -> {

                    pre.setName(sg1.getName());
                    try {
                        pre.setData(sg1.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    pre.setType(sg1.getContentType());
                    pre.setStudent(student);
                    this.sgpaFileService.storeSgpaFile(pre);

                }, () -> {

                    SgpaFile SGPA1 = new SgpaFile();

                    SGPA1.setName(sg1.getName());
                    try {
                        SGPA1.setData(sg1.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    SGPA1.setType(sg1.getContentType());
                    SGPA1.setStudent(student);
                    student.getSgpa().add(SGPA1);
                    this.sgpaFileService.storeSgpaFile(SGPA1);

                });

            });

            Optional.ofNullable(sgpa2).ifPresent(sg2 -> {

                Optional.ofNullable(this.sgpaFileService.getSgpaFileByNameAndRollNo(sg2.getName(), student.getRollNo())).ifPresentOrElse(pre2 -> {

                    pre2.setName(sg2.getName());
                    pre2.setType(sg2.getContentType());
                    pre2.setStudent(student);
                    this.sgpaFileService.storeSgpaFile(pre2);


                }, () -> {

                    SgpaFile SGPA2 = new SgpaFile();

                    SGPA2.setName(sg2.getName());
                    try {
                        SGPA2.setData(sg2.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    SGPA2.setType(sg2.getContentType());
                    SGPA2.setStudent(student);
                    student.getSgpa().add(SGPA2);
                    this.sgpaFileService.storeSgpaFile(SGPA2);

                });

            });


            Optional.ofNullable(sgpa3).ifPresent(sg3 -> {

                Optional.ofNullable(this.sgpaFileService.getSgpaFileByNameAndRollNo(sg3.getName(), student.getRollNo())).ifPresentOrElse(pre3 -> {

                    pre3.setName(sg3.getName());
                    pre3.setType(sg3.getContentType());
                    pre3.setStudent(student);
                    this.sgpaFileService.storeSgpaFile(pre3);


                }, () -> {
                    SgpaFile SGPA3 = new SgpaFile();
                    SGPA3.setName(sg3.getName());
                    try {
                        SGPA3.setData(sg3.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    SGPA3.setType(sg3.getContentType());
                    SGPA3.setStudent(student);
                    student.getSgpa().add(SGPA3);
                    this.sgpaFileService.storeSgpaFile(SGPA3);

                });

            });

            Optional.ofNullable(sgpa4).ifPresent(sg4 -> {

                Optional.ofNullable(this.sgpaFileService.getSgpaFileByNameAndRollNo(sg4.getName(), student.getRollNo())).ifPresentOrElse(pre4 -> {
                    pre4.setName(sg4.getName());
                    pre4.setType(sg4.getContentType());
                    pre4.setStudent(student);
                    this.sgpaFileService.storeSgpaFile(pre4);
                }, () -> {
                    SgpaFile SGPA4 = new SgpaFile();
                    SGPA4.setName(sg4.getName());
                    try {
                        SGPA4.setData(sg4.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    SGPA4.setType(sg4.getContentType());
                    SGPA4.setStudent(student);
                    student.getSgpa().add(SGPA4);
                    this.sgpaFileService.storeSgpaFile(SGPA4);

                });

            });

            Optional.ofNullable(sgpa5).ifPresent(sg5 -> {

                Optional.ofNullable(this.sgpaFileService.getSgpaFileByNameAndRollNo(sg5.getName(), student.getRollNo())).ifPresentOrElse(pre5 -> {
                    pre5.setName(sg5.getName());
                    pre5.setType(sg5.getContentType());
                    pre5.setStudent(student);
                    this.sgpaFileService.storeSgpaFile(pre5);
                }, () -> {
                    SgpaFile SGPA5 = new SgpaFile();
                    SGPA5.setName(sg5.getName());
                    try {
                        SGPA5.setData(sg5.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    SGPA5.setType(sg5.getContentType());
                    SGPA5.setStudent(student);
                    student.getSgpa().add(SGPA5);
                    this.sgpaFileService.storeSgpaFile(SGPA5);
                });

            });

            Optional.ofNullable(sgpa6).ifPresent(sg6 -> {

                Optional.ofNullable(this.sgpaFileService.getSgpaFileByNameAndRollNo(sg6.getName(), student.getRollNo())).ifPresentOrElse(pre6 -> {
                    pre6.setName(sg6.getName());
                    pre6.setType(sg6.getContentType());
                    pre6.setStudent(student);
                    this.sgpaFileService.storeSgpaFile(pre6);
                }, () -> {
                    SgpaFile SGPA6 = new SgpaFile();
                    SGPA6.setName(sg6.getName());
                    try {
                        SGPA6.setData(sg6.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    SGPA6.setType(sg6.getContentType());
                    SGPA6.setStudent(student);
                    student.getSgpa().add(SGPA6);
                    this.sgpaFileService.storeSgpaFile(SGPA6);

                });

            });

            Optional.ofNullable(sgpa7).ifPresent(sg7 -> {

                Optional.ofNullable(this.sgpaFileService.getSgpaFileByNameAndRollNo(sg7.getName(), student.getRollNo())).ifPresentOrElse(pre7 -> {
                    pre7.setName(sg7.getName());
                    pre7.setType(sg7.getContentType());
                    pre7.setStudent(student);
                    this.sgpaFileService.storeSgpaFile(pre7);
                }, () -> {
                    SgpaFile SGPA7 = new SgpaFile();
                    SGPA7.setName(sg7.getName());
                    try {
                        SGPA7.setData(sg7.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    SGPA7.setType(sg7.getContentType());
                    SGPA7.setStudent(student);
                    student.getSgpa().add(SGPA7);
                    this.sgpaFileService.storeSgpaFile(SGPA7);
                });

            });


            Optional.ofNullable(sgpa8).ifPresent(sg8 -> {

                Optional.ofNullable(this.sgpaFileService.getSgpaFileByNameAndRollNo(sg8.getName(), student.getRollNo())).ifPresentOrElse(pre8 -> {
                    pre8.setName(sg8.getName());
                    pre8.setType(sg8.getContentType());
                    pre8.setStudent(student);
                    this.sgpaFileService.storeSgpaFile(pre8);
                }, () -> {
                    SgpaFile SGPA8 = new SgpaFile();
                    SGPA8.setName(sg8.getName());
                    try {
                        SGPA8.setData(sg8.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    SGPA8.setType(sg8.getContentType());
                    SGPA8.setStudent(student);
                    student.getSgpa().add(SGPA8);
                    this.sgpaFileService.storeSgpaFile(SGPA8);
                });

            });


            session.setAttribute("message",
                    new Message("alert-success", "SGPA Added successfully for student " + student.getRollNo()));

            return "redirect:/admin/all-students";

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

                double avgAttendence = (attendence.getA1() + attendence.getA2() + attendence.getA3()
                        + attendence.getA4()
                        + attendence.getA5() + attendence.getA6() + attendence.getA7() + attendence.getA8()) / 8;

                student.setAvgAttendence(Math.round(avgAttendence));

                this.studentService.addStudent(student);

                session.setAttribute("message",
                        new Message("alert-success", "Attendence Added successfully for student: " + rollNo));

                return "redirect:all-students";

            } else {

                session.setAttribute("message", new Message("alert-warning",
                        "Student " + rollNo + " record Already have Attendence record !! you can update it."));

                return "admin/addAttendence";

            }

        } else {

            session.setAttribute("message",
                    new Message("alert-danger", "Student " + rollNo + " record doesn't exists !!"));

            return "admin/addAttendence";
        }

    }

    // cources page
    @GetMapping("/cources")
    public String cources() {
        return "admin/cources";
    }

    // all students page
    @GetMapping("/all-students")
    public String allStudents() {
        return "admin/allStudents";
    }

    // Backlog students page
    @GetMapping("/backlog-students")
    public String backlogStudents() {
        return "admin/backlogStudents";
    }

    // delete student
    @GetMapping("/student/delete/{rollNo}")
    public String deleteStudent(@PathVariable String rollNo, HttpSession session) {

        // System.out.println(rollNo);

        if (this.studentService.isStudentExits(rollNo)) {

            this.studentService.deleteStudent(rollNo);

            session.setAttribute("message", new Message("alert-warning", "Student record deleted Successfully"));

            return "redirect:/admin/all-students";

        } else {

            session.setAttribute("message",
                    new Message("alert-danger", "Student " + rollNo + " record doesn't exists !!"));

            return "redirect:dashboard";

        }

    }

    // delete sgpa
    @GetMapping("/student/record/delete/{rollNo}")
    public String deleteStudentRecord(@PathVariable String rollNo, HttpSession session) {

        if (this.studentService.isStudentExits(rollNo)) {

            List<SgpaFile> sgpa = this.sgpaFileService.getSgpaFileByRollNo(rollNo);

            for (SgpaFile sgpaFile : sgpa) {
                this.sgpaFileService.deleteSgpaFile(sgpaFile);
            }

            session.setAttribute("message", new Message("alert-warning", "Record deleted Successfully"));

            return "redirect:/admin/student/details/" + rollNo;

        } else {

            session.setAttribute("message",
                    new Message("alert-danger", "Student " + rollNo + " record doesn't exists !!"));

            return "redirect:dashboard";

        }

    }

    // student update

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

            oldStudent.setYear(student.getYear());
            oldStudent.setBacklog(student.isBacklog());
            oldStudent.setBackSub(student.getBackSub());
            oldStudent.setCurrentBacklog(student.getCurrentBacklog());

            // Save the updated student back to the database
            this.studentService.addStudent(oldStudent);

            this.emailService.sendEmail(oldStudent.getEmail(), "Studentrecordbyme | Update On Your Record",
                    EmailMessage.getMessage(student));

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
    public Map<String, Object> updateAttendence(@ModelAttribute Attendence attendence,
                                                @RequestParam("sRollNo") String rollNo) {

        // Fetch the existing student by roll number
        Optional<Student> optionalStudent = this.studentService.getStudentByRollNo(rollNo);

        // Prepare a response map
        Map<String, Object> response = new HashMap<>();

        if (optionalStudent.isPresent()) {

            Student oldStudent = optionalStudent.get();

            // Update the attendence details
            oldStudent.setAttendence(attendence);

            attendence.setStudent(oldStudent);

            // calculate avg
            double avgAttendence = (attendence.getA1() + attendence.getA2() + attendence.getA3() + attendence.getA4()
                    + attendence.getA5() + attendence.getA6() + attendence.getA7() + attendence.getA8()) / 8;

            oldStudent.setAvgAttendence(Math.round(avgAttendence));

            // Save the updated student back to the database
            this.studentService.addStudent(oldStudent);

            this.emailService.sendEmail(oldStudent.getEmail(), "Studentrecordbyme | Update On Your Record",
                    EmailMessage.getMessage(oldStudent));

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

    // upload result
    @GetMapping("/upload-result")
    public String uploadResultPage() {

        return "admin/uploadResult";
    }

    // process upload result form
    @PostMapping("/process-upload-result")
    public String uploadResult(@RequestParam("name") String name, @RequestParam("image") MultipartFile file,
                               HttpSession session) {

        try {

            this.fileService.storeFile(file, name);

            session.setAttribute("message", new Message("alert-success", "Result uploaded Successfully"));

            return "redirect:dashboard";

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // students details page
    @GetMapping("/student/details/{rollNo}")
    public String studentDetails(@PathVariable String rollNo, HttpSession session, Model m) {

        if (this.studentService.isStudentExits(rollNo)) {

            Student student = this.studentService.getStudentByRollNo(rollNo).get();

            List<SgpaFile> sgpa = this.sgpaFileService.getSgpaFileByRollNo(rollNo);

            System.out.println(sgpa);

            m.addAttribute("s", student);

            m.addAttribute("sgpa", sgpa);

            return "admin/studentDetails";

        } else {

            session.setAttribute("message", new Message("alert-danger", "Student record doesn't exists !!"));

            return "redirect:all-students";
        }

    }

    // delete result
    @GetMapping("/file/delete/{fileId}")
    public String deleteFile(@PathVariable int fileId, HttpSession session) throws IOException {

        this.fileService.deleteFile(fileId);

        session.setAttribute("message", new Message("alert-success", "File deleted successfully"));

        return "redirect:/admin/dashboard";
    }

    // delete separate sgpa
    @GetMapping("/sgpa/delete/{id}")
    public String deleteSgpaFile(@PathVariable int id, HttpSession session){

        Optional.ofNullable(this.sgpaFileService.getSgpaFileById(id)).ifPresentOrElse(sg->{
            this.sgpaFileService.deleteSgpaFile(sg);
        },()->{});

        session.setAttribute("message", new Message("alert-success", "File deleted successfully"));

        return "redirect:/admin/all-students";
    }

    // download result
    @GetMapping("/files/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable int fileId) throws IOException {
        File file = this.fileService.getFile(fileId).get();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getType())) // Set the correct content type
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(file.getData());
    }

    // download certificate
    @GetMapping("/certificate/download/{rollNo}")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable String rollNo) throws IOException {

        Certificate certificate = this.certificateService.getCertificateByRollNo(rollNo).get();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(certificate.getType())) // Set the correct content type
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + certificate.getName() + "\"")
                .body(certificate.getData());
    }

    // download sgpa
    @GetMapping("/sgpa/download/{rollNo}/{id}")
    public ResponseEntity<byte[]> downloadSgpa(@PathVariable("rollNo") String rollNo, @PathVariable("id") int id)
            throws IOException {

        SgpaFile sgpa = this.sgpaFileService.getSgpaFileByRollNoAndId(rollNo, id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(sgpa.getType())) // Set the correct content type
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + sgpa.getName() + "\"")
                .body(sgpa.getData());
    }


}
