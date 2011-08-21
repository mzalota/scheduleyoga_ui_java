package com.parsingUI;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.view.VelocityViewServlet;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

//import org.hibernate.cfg.Configuration;

import com.scheduleyoga.dao.Studio;
import com.scheduleyoga.parser.Event;


/**
 * Servlet implementation class StudioTemplate
 */
public class StudioTemplate extends VelocityViewServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StudioTemplate() {
        super();
        // TODO Auto-generated constructor stub
    }
    

	protected Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context ctx) {
	    
		//String value = request.getParameter("stateName");
		
		//getServletContext().
		//getRequestURI
		//getPathInfo
		List<String> pathElements = getURLParts(request.getRequestURI());	
		
		String studioNameUrl = pathElements.get(pathElements.size()-1);
		//Studio studio = Studio.createFromNameURL(studioNameUrl);
		
		List<Event> events =  getEvents(studioNameUrl);
		if (events.size()>0){
			ctx.put("version", "039 "+events.get(0).getStudio().getUrlSchedule());
		} else {
			ctx.put("version", "039 "+studioNameUrl+" did not fetch Studio Object");
		}
		ctx.put("events", events);
        String template = "templates/studio_template2.vm"; 
        Template outty = null;
        try {        	
            outty = getTemplate(template);
        } catch( ParseErrorException pee ) {
            //  controller.error("Parse error for template."+pee);
        } catch( ResourceNotFoundException rnfe ) {
            //controller.error("Template not found " + rnfe.toString());
        } catch( Exception e ) {
            //controller.error(e.toString());
        }
        return outty;
	}


	/**
	 * @param urlPath
	 * @return
	 */
	protected List<String> getURLParts(String urlPath) {
		StringTokenizer stringTokenizer = new StringTokenizer(urlPath,"/");
		
		List<String> pathElements = new ArrayList<String>();
		while (stringTokenizer.hasMoreElements()){
			String urlPart = stringTokenizer.nextToken();
			
			//remove file extension if it exists (e.g. .html or .jsp)
			urlPart = StringUtils.substringBeforeLast(urlPart,".");
			if (StringUtils.isBlank(urlPart)) {
				//this element is empty
				continue;
			}
				
			pathElements.add(urlPart);
		}
		return pathElements;
	}
    
	protected List<Event> getEvents(String studioNameUrl){
		//List result = HibernateUtil.getSessionFactory().getCurrentSession().createCriteria(Event.class).list();
		
		Configuration hiberConfig = new AnnotationConfiguration();
		SessionFactory sessFact = hiberConfig.configure().buildSessionFactory();
		Session session = sessFact.openSession();
		
		List<Event> events = (List<Event>) session.createQuery("select ev from Event as ev join ev.studio as st where st.nameForUrl='"+studioNameUrl+"'").list();
		return events;
	}
	
//	/**
//	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
//	 */
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		
//		response.setContentType("text/html");
//	    PrintWriter out = response.getWriter();
//	    //out.println("<html>");
//	    //out.println("<body> ");
//	    //out.println("<h1>Maximka Studio XYZ 193</h1>");
//	    //out.println("<p>Vsjo</p>");
//
//	    ServletContext contextServlet =getServletContext();
//	    
//	    String propertiesFileName = "C:\\workspace_springsource\\velocity-1.7\\examples\\ParserUI\\velocity.properties";
//	    Velocity.init(propertiesFileName);
//	    
//	    VelocityContext context = new VelocityContext();
//	    
//        context.put("version", "201");
//        String propVal = (String) Velocity.getProperty("file.resource.loader.path"); 
//       
//        out.println("<br> prop VAlue"+propVal);
//        
//        Template template =  null;
//        String templateFile = "studio_template.vm";// "example_max.vm";
//   
//        try
//        {
//            template = Velocity.getTemplate(templateFile);
//        }
//        catch( ResourceNotFoundException rnfe )
//        {
//            System.out.println("Example : error : cannot find template " + templateFile );
//            out.println("Example : error : cannot find template " + templateFile );
//            return;
//        }
//        catch( ParseErrorException pee )
//        {
//            System.out.println("Example : Syntax error in template " + templateFile + ":" + pee );
//            out.println("Example : Syntax error in template " + templateFile + ":" + pee );
//            return;
//        }    
//	    
//        StringWriter sw = new StringWriter();
//        template.merge( context, sw );
//        
//        //out.println("<br> printing Velocity Output<br>");
//        out.println(sw);
//        
//	    //out.println("</body>");
//	    //out.println("</html>");
//	}
	
    protected ArrayList getNames()
    {
        ArrayList list = new ArrayList();

        list.add("ArrayList element 1");
        list.add("ArrayList element 2");
        list.add("ArrayList element 3");
        list.add("ArrayList element 4");

        return list;
    }

}
