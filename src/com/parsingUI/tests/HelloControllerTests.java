package com.parsingUI.tests;

import org.springframework.web.servlet.ModelAndView;

import com.parsingUI.TeachersController;

import junit.framework.TestCase;

public class HelloControllerTests extends TestCase {

    public void testHandleRequestView() throws Exception{		
        TeachersController controller = new TeachersController();
        ModelAndView modelAndView = controller.handleRequest(null, null);		
        assertEquals("hello.jsp", modelAndView.getViewName());
    }
}
