package com.parsingUI;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
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

import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.scheduleyoga.common.GroupList;
import com.scheduleyoga.dao.Studio;
import com.scheduleyoga.parser.Event;
import com.scheduleyoga.parser.Helper;

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

	protected Template handleRequest(HttpServletRequest request,
			HttpServletResponse response, Context ctx) {

		//List<String> pathElements = Helper.createNew().getURLParts(request.getRequestURI());
		//String urlLastPart = pathElements.get(pathElements.size() - 1);
		
		String studioNameUrl = request.getParameter("studio");
		
		String urlDateStr = request.getParameter("date");
		//System.out.println("studio parameter is "+studioNameUrl+" date parameter is "+urlDateStr);
		
		Date urlDate = new Date();
		if (!StringUtils.isEmpty(urlDateStr)){
			//URL specified date. Parse it out. 
			try {
				urlDate = new SimpleDateFormat("yyyy-MM-dd").parse(urlDateStr); //urlLastPart
			} catch (ParseException e) {
			}
		}
//		if (null == urlDate) {
//			//date is not part of the string. Default to displaying today's schedule
//			urlDate = new Date();
//			studioNameUrl = pathElements.get(pathElements.size() - 1);
//		} else {
//			studioNameUrl = pathElements.get(pathElements.size() - 2);
//		}
//		
		String stateNameUrl = request.getParameter("state");
		

		Template outty = buildStudioPage(ctx, studioNameUrl, stateNameUrl, urlDate);
		
		return outty;
	}

	/**
	 * @param ctx
	 * @param studioNameUrl
	 * @return
	 */
	protected Template buildStudioPage(Context ctx, String studioNameUrl, String stateNameUrl, Date schedDate) {
		List<Event> events = Event.findEventsForStudioForDate(studioNameUrl, schedDate);

		Studio studio = Studio.createFromNameURL(studioNameUrl);
//		System.out.println("Studio is: "+ studio.toString());	
		
		Map<Date, List<Event>> schedule = GroupList.group(events, "startTime");
		Set<Date> startTimes = new TreeSet<Date>();
		startTimes.addAll(schedule.keySet());
		
		if (events.size() > 0) {
			ctx.put("version", "153");
		} else {
			ctx.put("version", "097 " + stateNameUrl
					+ " did not fetch Studio Object");
		}
		
		Map<String, String> navDates = initializeNavDates(schedDate);		
		
		String stateName = WordUtils.capitalize(StringUtils.replace(stateNameUrl, "-", " "));		
		
		ctx.put("events", events);
		ctx.put("startTimes", startTimes);
		ctx.put("schedule", schedule);
		ctx.put("studioNameUrl", studioNameUrl);
		ctx.put("stateName", stateName);
		ctx.put("stateNameUrl", stateNameUrl);
		ctx.put("schedDate", schedDate);
		ctx.put("navDates", navDates);
		ctx.put("studio", studio);
		
		String template = "templates/studio_template2.vm";
		Template outty = null;
		try {
			outty = getTemplate(template);
		} catch (ParseErrorException pee) {
			// controller.error("Parse error for template."+pee);
		} catch (ResourceNotFoundException rnfe) {
			// controller.error("Template not found " + rnfe.toString());
		} catch (Exception e) {
			// controller.error(e.toString());
		}
		
		System.out.println(schedule.toString());
		return outty;
	}

	/**
	 * @param schedDate
	 * @return
	 */
	protected Map<String, String> initializeNavDates(Date schedDate) {
		int DAYS_AHEAD_TO_SHOW = 9;
		
		Date today = new Date();
		
		long dayDiff = Helper.createNew().getDayDiffWithToday(schedDate);
		
		Map<String, String> navDates = new HashMap<String, String>(4);
		Calendar cal = new GregorianCalendar();
		
		if (dayDiff > 0){
			cal.setTime(schedDate);
			cal.add(Calendar.DATE , -1);
			String prevDateStr = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
			navDates.put("<",prevDateStr);
		}
		
		if (dayDiff > 7){
			cal.setTime(schedDate);
			cal.add(Calendar.DATE , -7);
			String prevWeekDateStr = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
			navDates.put("<<",prevWeekDateStr);
		} else if (dayDiff > 0){
			cal.setTime(today);
			String prevWeekDateStr = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
			navDates.put("<<",prevWeekDateStr);
		}
		
		if (dayDiff < (DAYS_AHEAD_TO_SHOW-1)){
			cal.setTime(schedDate);
			cal.add(Calendar.DATE , 1);
			String nextDateStr = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
			navDates.put(">",nextDateStr);
		}
		
		long daysTillLimit = DAYS_AHEAD_TO_SHOW - dayDiff;
		if (daysTillLimit<=7 && daysTillLimit>1){
			cal.setTime(today);
			cal.add(Calendar.DATE , DAYS_AHEAD_TO_SHOW);
			String nextWeekDateStr = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
			navDates.put(">>",nextWeekDateStr);
		} else if (daysTillLimit>7) {
			cal.setTime(schedDate);
			cal.add(Calendar.DATE , 7);
			String nextWeekDateStr = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
			navDates.put(">>",nextWeekDateStr);
		}
		
		System.out.println("date diff "+dayDiff+" days till limit "+daysTillLimit) ;
		
		return navDates;
	}

	



	// /**
	// * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	// response)
	// */
	// protected void doGet(HttpServletRequest request, HttpServletResponse
	// response) throws ServletException, IOException {
	// // TODO Auto-generated method stub
	//
	// response.setContentType("text/html");
	// PrintWriter out = response.getWriter();
	// //out.println("<html>");
	// //out.println("<body> ");
	// //out.println("<h1>Maximka Studio XYZ 193</h1>");
	// //out.println("<p>Vsjo</p>");
	//
	// ServletContext contextServlet =getServletContext();
	//
	// String propertiesFileName =
	// "C:\\workspace_springsource\\velocity-1.7\\examples\\ParserUI\\velocity.properties";
	// Velocity.init(propertiesFileName);
	//
	// VelocityContext context = new VelocityContext();
	//
	// context.put("version", "201");
	// String propVal = (String)
	// Velocity.getProperty("file.resource.loader.path");
	//
	// out.println("<br> prop VAlue"+propVal);
	//
	// Template template = null;
	// String templateFile = "studio_template.vm";// "example_max.vm";
	//
	// try
	// {
	// template = Velocity.getTemplate(templateFile);
	// }
	// catch( ResourceNotFoundException rnfe )
	// {
	// System.out.println("Example : error : cannot find template " +
	// templateFile );
	// out.println("Example : error : cannot find template " + templateFile );
	// return;
	// }
	// catch( ParseErrorException pee )
	// {
	// System.out.println("Example : Syntax error in template " + templateFile +
	// ":" + pee );
	// out.println("Example : Syntax error in template " + templateFile + ":" +
	// pee );
	// return;
	// }
	//
	// StringWriter sw = new StringWriter();
	// template.merge( context, sw );
	//
	// //out.println("<br> printing Velocity Output<br>");
	// out.println(sw);
	//
	// //out.println("</body>");
	// //out.println("</html>");
	// }

	protected ArrayList getNames() {
		ArrayList list = new ArrayList();

		list.add("ArrayList element 1");
		list.add("ArrayList element 2");
		list.add("ArrayList element 3");
		list.add("ArrayList element 4");

		return list;
	}
}
