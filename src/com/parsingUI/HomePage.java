package com.parsingUI;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.view.VelocityViewServlet;

/**
 * Servlet implementation class HomePage
 */
public class HomePage extends VelocityViewServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see VelocityViewServlet#VelocityViewServlet()
     */
    public HomePage() {
        super();
        // TODO Auto-generated constructor stub
    }


	protected Template handleRequest(HttpServletRequest request,
			HttpServletResponse response, Context ctx) {
		
		//List<String> pathElements = Helper.createNew().getURLParts(request.getRequestURI());
		
		String url = request.getRequestURI();
		System.out.println("currentURL is: "+url);
		
		ctx.put("version", "701");

        String template = "templates/"+url+".vm"; 
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
}
