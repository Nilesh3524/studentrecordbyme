package com.record.student.helper;

import lombok.Data;


public class ResultMessage {

    public static String getMessage(String name,String link){

        return
                """
                Hello,\s"""
                        + name+
                        """
                !

               Your Result has been declared. You can see by clicking following link:
               
               """+link+"""

               We hope you're having a great day!
               

               Best regards,

               S.B. Jain Institute

               """;

    }
}
