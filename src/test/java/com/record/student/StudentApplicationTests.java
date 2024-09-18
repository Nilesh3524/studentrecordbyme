package com.record.student;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.record.student.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StudentApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void testStudentSerialization() throws JsonProcessingException {
        Student student = new Student();
        // Set fields and create a Certificate instance if needed
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(student);
        System.out.println(jsonString);
    }


}
