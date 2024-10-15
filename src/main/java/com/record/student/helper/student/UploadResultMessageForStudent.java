package com.record.student.helper.student;

public class UploadResultMessageForStudent {

    public static String getMessage(String name,String link){

        return
                """
                Hello,\s"""
                        + name+
                        """
                !

               Your CAE/TAE Result has been declared. You can see by clicking following link:
               
               """+link+"""
               

               We hope you're having a great day!
               

               Best regards,

               S.B. Jain Institute

               """;

    }
}
