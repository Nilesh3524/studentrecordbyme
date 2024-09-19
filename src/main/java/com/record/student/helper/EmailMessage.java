package com.record.student.helper;

import com.record.student.model.Student;

public class EmailMessage {

    public static String getMessage(Student student) {

        return
                """
                Hello,\s"""
                          + student.getName()+
                        """
                !

               Your data has been updated. you can check on our website.

               We hope you're having a great day!
               

               Best regards,

               S.B. Jain Institute

               """;
    }
}
