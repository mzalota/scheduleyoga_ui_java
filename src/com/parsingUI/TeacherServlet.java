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
import java.util.HashSet;
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
import com.scheduleyoga.dao.Instructor;

/**
 * Servlet implementation class Instructor
 */
public class TeacherServlet extends VelocityViewServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TeacherServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected Template handleRequest(HttpServletRequest request,
			HttpServletResponse response, Context ctx) {

		String instructorNameUrl = request.getParameter("teacher");
		
		String urlDateStr = request.getParameter("date");
		
		Date urlDate = new Date();
		if (!StringUtils.isEmpty(urlDateStr)){
			//URL specified date. Parse it out. 
			try {
				urlDate = new SimpleDateFormat("yyyy-MM-dd").parse(urlDateStr); //urlLastPart
			} catch (ParseException e) {
			}
		}

		String stateNameUrl = request.getParameter("state");

		Template outty = buildStudioPage(ctx, instructorNameUrl, stateNameUrl, urlDate);
		
		return outty;
	}

	/**
	 * @param ctx
	 * @param studioNameUrl
	 * @return
	 */
	protected Template buildStudioPage(Context ctx, String instructorNameUrl, String stateNameUrl, Date schedDate) {
		
		

		//Studio studio = Studio.createFromNameURL(studioNameUrl);
		Instructor instructor = Instructor.createFromNameURL(instructorNameUrl);
		
		List<Event> events = Event.findEventsForInstructorForDate(instructor, schedDate);
		
		//Extract all studios into separate Set
		Set<Studio> studios = new HashSet<Studio>();
		for (final Event event : events){
			studios.add(event.getStudio());
		}
		
		Map<Date, List<Event>> schedule = GroupList.group(events, "startTime");
		Set<Date> startTimes = new TreeSet<Date>();
		startTimes.addAll(schedule.keySet());
		
		if (events.size() > 0) {
			ctx.put("version", "161");
		} else {
			ctx.put("version", "101 " + stateNameUrl
					+ " did not fetch Studio Object");
		}
		System.out.println("in Teacher Servlet version 161");
		
		Map<String, String> navDates = initializeNavDates(schedDate);		
		
		String stateName = WordUtils.capitalize(StringUtils.replace(stateNameUrl, "-", " "));		
		
		ctx.put("events", events);
		ctx.put("startTimes", startTimes);
		ctx.put("schedule", schedule);
		ctx.put("stateName", stateName);
		ctx.put("stateNameUrl", stateNameUrl);
		ctx.put("schedDate", schedDate);
		ctx.put("navDates", navDates);
		ctx.put("instructor", instructor);
		ctx.put("studios", studios);
		
		String template = "templates/teacher.vm";
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
}
