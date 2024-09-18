package com.record.student.controller;


import java.security.Principal;

import com.record.student.sevice.SgpaFileService;
import com.record.student.sevice.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class RouteController {


    private Logger logger=LoggerFactory.getLogger(RouteController.class);


    @ModelAttribute
    public void loggedInUserInformation(Model m, Principal principal){

        if (principal==null) {
            return;
        }

        logger.info("Logged in user {}",principal.getName());

        m.addAttribute("loggedInUser", principal.getName());

    }



}
