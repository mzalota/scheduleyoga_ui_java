package com.parsingUI;

//import org.springframework.web.servlet.mvc.Controller;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

//https://src.springframework.org/svn/spring-samples/petcare/trunk/src/main/java/org/springframework/samples/petcare/appointments/AppointmentsController.java
//http://stackoverflow.com/questions/4074484/using-velocity-tools-with-spring-3-0-3
//http://wiki.apache.org/velocity/VelocityAndSpringStepByStep
//http://blog.springsource.com/2009/12/21/mvc-simplifications-in-spring-3-0/?utm_source=feedburner&utm_medium=feed&utm_campaign=Feed%3A+Interface21TeamBlog+%28SpringSource+Team+Blog%29
//http://static.springsource.org/docs/Spring-MVC-step-by-step/part2.html

//http://singgihpraditya.wordpress.com/2010/02/13/spring-3-0-and-hibernate-tutorial-part-1/
//http://www.infoq.com/presentations/donald-overview-spring-3.0-web-stack

//@Controller
//@RequestMapping("/hello.php")
public class HelloController {

    protected final Log logger = LogFactory.getLog(getClass());
    
    //@RequestMapping(value = "hello.php", method = RequestMethod.GET)
    public String justRunIt(ModelMap map) {

        logger.info("Returning hello view velocity 47");

        map.put("testVar", "Vot I Jaaa ffaaas");
        
        return "hello";
    }
    
//    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        logger.info("Returning hello view");
//
//        return new ModelAndView("hello.jsp");
//    }
}
