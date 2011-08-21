package com.parsingUI;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.scheduleyoga.dao.Studio;
import com.scheduleyoga.parser.Event;

/**
 * Servlet implementation class ListForState
 */
public class ListForState extends VelocityViewServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ListForState() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected ArrayList getNames()
    {
        ArrayList list = new ArrayList();

        list.add("Studio 1");
        list.add("Studio 2");
        list.add("Studio 3");
        list.add("Studio 4");

        return list;
    }
	protected Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context ctx) {
	    
		ctx.put("version", "322");
		ctx.put("studios", getStudios());
		ctx.put("list", getNames());
        String template = "templates/studios_list.vm"; 
        Template outty = null;
        try {        	
        	//String propVal = (String) Velocity.getProperty("file.resource.loader.path");
        	//PrintWriter out = response.getWriter();
        	//out.println("<br> prop VAlue"+propVal);
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

	protected List<Studio> getStudios(){
		//List result = HibernateUtil.getSessionFactory().getCurrentSession().createCriteria(Event.class).list();
		
		Session session = DBAccess.openSession();
		
		@SuppressWarnings("unchecked")
		List<Studio> studios = (List<Studio>) session.createQuery("from Studio").list();
		return studios;
	}
	
}
