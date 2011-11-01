package com.parsingUI;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.view.VelocityViewServlet;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

import com.scheduleyoga.dao.DBAccess;
import com.scheduleyoga.dao.Instructor;
import com.scheduleyoga.dao.Studio;
import com.scheduleyoga.parser.Event;
import com.scheduleyoga.parser.Helper;

/**
 * Servlet implementation class TeachersListServlet
 */
public class TeachersListServlet extends VelocityViewServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TeachersListServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context ctx) {
	    
		String stateNameUrl = request.getParameter("state");
		String stateName = WordUtils.capitalize(StringUtils.replace(stateNameUrl, "-", " "));
		
		ctx.put("stateNameUrl", stateNameUrl);
		ctx.put("stateName", stateName);
		ctx.put("instructors", getAllInstructors());

        String template = "templates/teachers_list.vm"; 
        Template outty = null;
        try {        	
            outty =  getTemplate(template);
        } catch( ParseErrorException pee ) {
            //controller.error("Parse error for template."+pee);
        } catch( ResourceNotFoundException rnfe ) {
            //controller.error("Template not found " + rnfe.toString());
        } catch( Exception e ) {
            //controller.error(e.toString());
        }
        return outty;
	}

	@SuppressWarnings("unchecked")
	protected List<Instructor> getAllInstructors(){
		//List result = HibernateUtil.getSessionFactory().getCurrentSession().createCriteria(Event.class).list();
		
		Session session = DBAccess.openSession();
		
		return (List<Instructor>) session.createQuery("from Instructor").list();
	}
	
}
