package com.record.student.helper.parent;

public class ResultLinkMessageForParent {

    public static String getMessage(String name,String link){

        return
                """
                Hello,\s"""
                        + name+
                        """
                !

               Result has been declared by College. You can see by clicking following link:
               
               """+link+"""
               

               We hope you're having a great day!
               

               Best regards,

               S.B. Jain Institute

               """;

    }

}