package com.record.student.controller;

import com.record.student.helper.*;
import com.record.student.helper.parent.*;
import com.record.student.helper.student.*;
import com.record.student.model.*;
import com.record.student.sevice.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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
    private SgpaService sgpaService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ResultLinkService linkService;
    private double cgpa;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @ModelAttribute
    public void getAllStudents(Model m) {

        List<Student> students = this.studentService.getAllStudents();

        m.addAttribute("students", students);

    }


    @ModelAttribute
    public void getAllLinks(Model m) {

        List<ResultLink> links = this.linkService.getAllLinks();

        m.addAttribute("links", links);

    }

    // Display all files on a page
    @ModelAttribute
    public void listAllFiles(Model model) throws IOException {
        List<File> files = this.fileService.getAllFiles();
        model.addAttribute("files", files);
    }

    //add link
    @PostMapping("/add-link")
    @ResponseBody
    public Map<String, Object> addLink(@ModelAttribute ResultLink link, HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        this.linkService.saveLink(link);

        List<Student> students = this.studentService.getAllStudents();

        for (Student s : students) {

            this.emailService.sendEmail(s.getEmail(), "Result Has Been Declared", ResultLinkMessageForStudent.getMessage(s.getName(), link.getLink()));
            this.emailService.sendEmail(s.getParentEmail(), "Result Has Been Declared", ResultLinkMessageForParent.getMessage("parents", link.getLink()));

        }

        response.put("success", true);
        response.put("message", "Link Added Successfully");

        return response;

    }

    @GetMapping("/link/delete/{id}")
    public String deleteLink(@PathVariable int id, HttpSession session) {

        if (this.linkService.isExists(id)) {

            this.linkService.deleteLink(id);

            session.setAttribute("message", new Message("alert-success", "Link Deleted Successfully"));

            return "redirect:/admin/dashboard";

        } else {

            session.setAttribute("message", new Message("alert-danger", "Link Not Exists !!"));

            return "redirect:/admin/dashboard";

        }


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
    public String addSGPAForm(Model m) {

        m.addAttribute("sgpa",new Sgpa());
        m.addAttribute("rollNo","");
        m.addAttribute("avg",0);

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

    //add sgpa
    @PostMapping("/process-add-sgpa")
    public String addSgpa(@RequestParam("rollNo") String rollNo,
                          @RequestParam("avg-sgpa") double cgpa,
                          @ModelAttribute Sgpa sgpa,
                          Model m,
                          HttpSession session) {


        if (this.studentService.isStudentExits(rollNo)) {

            Student student = this.studentService.getStudentByRollNo(rollNo)
                    .get();

            if (student.getSgpa() == null){

                sgpa.setStudent(student);

                student.setCgpa(cgpa);

                student.setSgpa(sgpa);

                this.sgpaService.saveSgpa(sgpa);

                this.studentService.addStudent(student);

                session.setAttribute("message",
                        new Message("alert-success", "Sgpa Added Successfully."));

                return "redirect:/admin/all-students";


            }else {

                m.addAttribute("sgpa",sgpa);
                m.addAttribute("rollNo",rollNo);
                m.addAttribute("avg",cgpa);

                session.setAttribute("message",
                        new Message("alert-danger", "Sgpa already Added for student with roll no '"+rollNo+"' !!"));

                return "admin/sgpa";
            }

        }

        session.setAttribute("message",
                new Message("alert-danger", "Student with " + rollNo + " is not Exists !!"));


        return "admin/sgpa";
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
                    StudentUpdateMessageForStudent.getMessage(student,Website.link));

            this.emailService.sendEmail(oldStudent.getParentEmail(), "Studentrecordbyme | Update On Student Record",
                    StudentUpdateMessageForParent.getMessage("parents",Website.link));

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

            this.emailService.sendEmail(oldStudent.getEmail(), "Studentrecordbyme | Update On Your Attendance",
                    AttendanceUpdateMessageForStudent.getMessage(oldStudent,Website.link));

            this.emailService.sendEmail(oldStudent.getParentEmail(), "Studentrecordbyme | Update On Student Attendance",
                    AttendanceUpdateMessageForParent.getMessage("parents",Website.link));

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


    // update sgpa
    @PostMapping("/student/update/sgpa")
    @ResponseBody // This ensures the method returns JSON, not a view.
    public Map<String, Object> updateSgpa(@ModelAttribute Sgpa sgpa,
                                                @RequestParam("cgpa") double cgpa,
                                                @RequestParam("studentRollNo") String rollNo) {

        // Fetch the existing student by roll number
        Optional<Student> optionalStudent = this.studentService.getStudentByRollNo(rollNo);

        // Prepare a response map
        Map<String, Object> response = new HashMap<>();

        if (optionalStudent.isPresent()) {

            Student oldStudent = optionalStudent.get();

            sgpa.setStudent(oldStudent);

            // Update the attendence details
            oldStudent.setSgpa(sgpa);

            System.out.println(cgpa);

            oldStudent.setCgpa(cgpa);

            this.sgpaService.saveSgpa(sgpa);

            this.studentService.addStudent(oldStudent);

            this.emailService.sendEmail(oldStudent.getEmail(), "Studentrecordbyme | Update On Your SGPA Record",
                    SgpaUpdateMessageForStudent.getMessage(oldStudent,Website.link));

            this.emailService.sendEmail(oldStudent.getParentEmail(), "Studentrecordbyme | Update On Student SGPA Record",
                    SgpaUpdateMessageForParent.getMessage("parents",Website.link));

            // Return success response
            response.put("success", true);

            response.put("message", "Sgpa updated successfully.");

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
    @ResponseBody
    public Map<String,Object> uploadResult(@RequestParam("name") String name, @RequestParam("image") MultipartFile file,
                               HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {

            this.fileService.storeFile(file, name);

            List<Student> students = this.studentService.getAllStudents();

            for (Student s : students) {

                this.emailService.sendEmail(s.getEmail(), "CAE/TAE Result Has Been Declared", UploadResultMessageForStudent.getMessage(s.getName(), Website.link));
                this.emailService.sendEmail(s.getParentEmail(), "CAE/TAE Result Has Been Declared", UploadResultMessageForParent.getMessage("parents", Website.link));

            }

            // Return success response
            response.put("success", true);

            response.put("message", "Result Added successfully.");

        } catch (IOException e) {
            // If student is not found, return failure response
            response.put("success", false);
            response.put("message", "failed to add result !!");
        }

        return response;

    }

    // students details page
    @GetMapping("/student/details/{rollNo}")
    public String studentDetails(@PathVariable String rollNo, HttpSession session, Model m) {

        if (this.studentService.isStudentExits(rollNo)) {

            Student student = this.studentService.getStudentByRollNo(rollNo).get();

            Sgpa sgpa = this.sgpaService.getSgpaByRollNo(rollNo);

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


}
