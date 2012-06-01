package com.parsingUI;

//import org.springframework.web.servlet.mvc.Controller;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.scheduleyoga.common.GroupList;
import com.scheduleyoga.common.LookUp;
import com.scheduleyoga.dao.DBAccess;
import com.scheduleyoga.dao.Instructor;
import com.scheduleyoga.dao.Studio;
import com.scheduleyoga.parser.CalendarRenderer;
import com.scheduleyoga.parser.Event;

import java.io.IOException;
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
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//https://src.springframework.org/svn/spring-samples/petcare/trunk/src/main/java/org/springframework/samples/petcare/appointments/AppointmentsController.java
//http://stackoverflow.com/questions/4074484/using-velocity-tools-with-spring-3-0-3
//http://wiki.apache.org/velocity/VelocityAndSpringStepByStep
//http://blog.springsource.com/2009/12/21/mvc-simplifications-in-spring-3-0/?utm_source=feedburner&utm_medium=feed&utm_campaign=Feed%3A+Interface21TeamBlog+%28SpringSource+Team+Blog%29
//http://static.springsource.org/docs/Spring-MVC-step-by-step/part2.html

//http://singgihpraditya.wordpress.com/2010/02/13/spring-3-0-and-hibernate-tutorial-part-1/
//http://www.infoq.com/presentations/donald-overview-spring-3.0-web-stack

@Controller
@RequestMapping("/teachers")
public class TeachersController {

    protected final Log logger = LogFactory.getLog(getClass());
    
    EntityDAO entityDAO;
    
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}
    
    @RequestMapping(value = "*", method = RequestMethod.GET)
    public String otherURLs(ModelMap map) {

//        logger.info("Returning hello view velocity 53");
//        map.put("testVar", "Vot I Jaaa ffaaas Kukushka-bubushka addfd 44");        
//        return "hello"; 
    	
    	return "redirect:/teachers/new-york/";
    }    

    @RequestMapping(value = "/{stateNameUrl}/", method = RequestMethod.GET)
	protected String prepareTeachersListPage(@PathVariable String stateNameUrl, 
											 ModelMap map) {

    	Map<String, String> stateURLs = LookUp.getInstance().getStatesMap();    	
    	if(!stateURLs.containsKey(stateNameUrl.toLowerCase().trim())){
    		//specified state is invalid. Redirect to new-york
    		stateNameUrl="new-york";
			return "redirect:/teachers/"+stateNameUrl+"/";
    	}
    	
    	if (!stateNameUrl.trim().toLowerCase().equals(stateNameUrl)){
    		//stateNameUrl has capitalized letters.
    		return "redirect:/teachers/"+stateNameUrl.trim().toLowerCase()+"/";
    	}        	
    	
		String stateName = WordUtils.capitalize(StringUtils.replace(stateNameUrl, "-", " "));
		
		map.put("stateNameUrl", stateNameUrl);
		map.put("stateName", stateName);
		map.put("instructors", getAllInstructors(stateNameUrl));
		
		return "/teachers/teachers_list";
	}
    
    
	@SuppressWarnings("unchecked")
	protected List<Instructor> getAllInstructors(String stateNameUrl){
		
		//Session session = DBAccess.openSession();
		//return (List<Instructor>) session.createQuery("from Instructor order by name").list();		
		
		Query q = DBAccess.openSession().createQuery(
				"SELECT ev.instructor " +
				"FROM Event AS ev " +
				"JOIN ev.instructor AS instr " +
				"JOIN ev.studio AS std " +
				"WHERE std.state = :stateNameUrl " +
				"GROUP BY ev.instructor  " +
				"ORDER BY instr.name "				
				);
		q.setParameter("stateNameUrl", stateNameUrl);
		
		List<Instructor> results = (List<Instructor>) q.list();
		if (results.size() <= 0) {
			return null;
		}	
		return results;
		
	}
    
    @RequestMapping(value = "/{stateNameUrl}/{instructorName}/", method = RequestMethod.GET)
	protected String prepareTeacherPage(@PathVariable String stateNameUrl, @PathVariable String instructorName, ModelMap map) {
    	
    	Map<String, String> stateURLs = LookUp.getInstance().getStatesMap();    	
    	if(!stateURLs.containsKey(stateNameUrl.toLowerCase().trim())){
    		//specified state is invalid. Redirect to new-york
    		stateNameUrl="new-york";
			return "redirect:/teachers/"+stateNameUrl+"/"+instructorName.trim().toLowerCase()+"/";
    	}
    	
    	if (!stateNameUrl.trim().toLowerCase().equals(stateNameUrl)){
    		//stateNameUrl has capitalized letters.
    		return "redirect:/teachers/"+stateNameUrl.trim().toLowerCase()+"/"+instructorName.trim().toLowerCase()+"/";
    	}      
    	
    	if (!instructorName.trim().toLowerCase().equals(instructorName)){
    		//instructorName has capitalized letters.
    		return "redirect:/teachers/"+stateNameUrl.trim().toLowerCase()+"/"+instructorName.trim().toLowerCase()+"/";
    	}      
    	    	
		buildMap(map, instructorName, stateNameUrl, true);
		return "/teachers/teacher2";
	}

    @RequestMapping(value = "/{stateName}/{instructorName}/{reqDate}.html", method = RequestMethod.GET)
	protected String prepareTeacherPage(@PathVariable String stateName, 
										@PathVariable String instructorName,
										@PathVariable String reqDate,
										ModelMap map) {
    	
    	//We are no longer showing schedules for instructors by day. Its always by week.
		return "redirect:/teachers/"+stateName+"/"+instructorName+"/";
	}
    
    @RequestMapping(value = "/{stateNameUrl}/{instructorName}/next-week.html", method = RequestMethod.GET)
	protected String prepareTeacherNextWeekPage(@PathVariable String stateNameUrl, 
												@PathVariable String instructorName,
												ModelMap map) {

    	Map<String, String> stateURLs = LookUp.getInstance().getStatesMap();    	
    	if(!stateURLs.containsKey(stateNameUrl.toLowerCase().trim())){
    		//specified state is invalid. Redirect to new-york
    		stateNameUrl="new-york";
			return "redirect:/teachers/"+stateNameUrl+"/"+instructorName.trim().toLowerCase()+"/next-week.html";
    	}
    	
    	if (!stateNameUrl.trim().toLowerCase().equals(stateNameUrl)){
    		//stateNameUrl has capitalized letters.
    		return "redirect:/teachers/"+stateNameUrl.trim().toLowerCase()+"/"+instructorName.trim().toLowerCase()+"/next-week.html";
    	}      
    	
    	if (!instructorName.trim().toLowerCase().equals(instructorName)){
    		//instructorName has capitalized letters.
    		return "redirect:/teachers/"+stateNameUrl.trim().toLowerCase()+"/"+instructorName.trim().toLowerCase()+"/next-week.html";
    	}          	
    	
		buildMap(map, instructorName, stateNameUrl, false);
		return "/teachers/teacher2";
	}
    
	/**
	 * @param map
	 * @param instructorNameUrl
	 * @param stateNameUrl
	 * @return
	 */
	protected void buildMap(ModelMap map, String instructorNameUrl, String stateNameUrl, boolean thisWeek) {
		
		Date schedDate = new Date();
		
		if (!thisWeek){
			Calendar cal = new GregorianCalendar();
			cal.setTime(schedDate);
			cal.add(Calendar.DATE, 7);
			schedDate = cal.getTime();
		}
		
		Instructor instructor = entityDAO.createFromNameURL(instructorNameUrl); //Instructor.createFromNameURL(instructorNameUrl);
		
		List<Event> events = new ArrayList<Event>();
		for (int i=0; i<7; i++) {
			Calendar cal = new GregorianCalendar();
			cal.setTime(schedDate);
			cal.add(Calendar.DATE, i);
			events.addAll(entityDAO.findEventsForInstructorForDate(instructor, cal.getTime()));
		}		
		
		//Extract all studios into separate Set
		Set<Studio> studios = new HashSet<Studio>();
		for (final Event event : events){
			studios.add(event.getStudio());
		}
		
		CalendarRenderer renderer = CalendarRenderer.createNew();
		renderer.addEvents(events);
		
		String stateName = WordUtils.capitalize(StringUtils.replace(stateNameUrl, "-", " "));		
		
		map.put("events", events);
		map.put("stateName", stateName);
		map.put("stateNameUrl", stateNameUrl);
		map.put("schedDate", schedDate);
		map.put("instructor", instructor);
		map.put("studios", studios);
		map.put("renderer", renderer);
		map.put("thisWeek", thisWeek);		
	}
}
