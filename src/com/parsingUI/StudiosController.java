package com.parsingUI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import com.scheduleyoga.parser.Event;
import com.scheduleyoga.parser.Helper;

@Controller
@RequestMapping("/studios")
public class StudiosController {
	
    protected final Log logger = LogFactory.getLog(getClass());
    
    EntityDAO entityDAO;
    
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}
	
    @RequestMapping(value = "*", method = RequestMethod.GET)
    public String otherURLs(ModelMap map) {

    	return "redirect:/studios/new-york/";
    }  
	
    @RequestMapping(value = "/{stateNameUrl}/", method = RequestMethod.GET)
	protected String prepareStudiosListPage(@PathVariable String stateNameUrl, 
											 ModelMap map) {

    	Map<String, String> stateURLs = LookUp.getInstance().getStatesMap();    	
    	if(!stateURLs.containsKey(stateNameUrl.toLowerCase().trim())){
    		//specified state is invalid. Redirect to new-york
    		stateNameUrl="new-york";
			return "redirect:/studios/"+stateNameUrl+"/";
    	}
    	
    	if (!stateNameUrl.trim().toLowerCase().equals(stateNameUrl)){
    		//stateNameUrl has capitalized letters.
    		return "redirect:/studios/"+stateNameUrl.trim().toLowerCase()+"/";
    	}
    	
		String stateName = WordUtils.capitalize(StringUtils.replace(stateNameUrl, "-", " "));
		
		map.put("stateNameUrl", stateNameUrl);
		map.put("stateName", stateName);
		map.put("studios", getStudios(stateNameUrl));
		
		return "/studios/studios_list";
	}
    
	@SuppressWarnings("unchecked")
	protected List<Studio> getStudios(String stateNameUrl){				
		
		//Session session = DBAccess.openSession();
		//return (List<Instructor>) session.createQuery("from Studio order by name").list();		
		
		Query q = DBAccess.openSession().createQuery(
				"from Studio where state=:stateNameUrl order by name"
				);
		q.setParameter("stateNameUrl", stateNameUrl);
		
		List<Studio> results = (List<Studio>) q.list();
		if (results.size() <= 0) {
			return null;
		}	
		return results;
	}
    
	
    @RequestMapping(value = "/{stateNameUrl}/{studioName}/", method = RequestMethod.GET)
	protected String prepareStudioPage(@PathVariable String stateNameUrl, 
									   @PathVariable String studioName, 
									   ModelMap map) {

    	Map<String, String> stateURLs = LookUp.getInstance().getStatesMap();    	
    	if(!stateURLs.containsKey(stateNameUrl.toLowerCase().trim())){
    		//specified state is invalid. Redirect to new-york
    		stateNameUrl="new-york";
			return "redirect:/studios/"+stateNameUrl+"/"+studioName.trim().toLowerCase()+"/";
    	}
    	
    	if (!stateNameUrl.trim().toLowerCase().equals(stateNameUrl)){
    		//stateNameUrl has capitalized letters.
    		return "redirect:/studios/"+stateNameUrl.trim().toLowerCase()+"/"+studioName.trim().toLowerCase()+"/";
    	}
    	
    	if (!studioName.trim().toLowerCase().equals(studioName)){
    		//studioName has capitalized letters.
    		return "redirect:/studios/"+stateNameUrl+"/"+studioName.trim().toLowerCase()+"/";
    	}
    	
		buildStudioPage(map, studioName, stateNameUrl, new Date());
		return "studios/studio";
	}
    
    @RequestMapping(value = "/{stateNameUrl}/{studioName}/{dateStr}.html", method = RequestMethod.GET)
	protected String prepareStudioSpecificDatePage(@PathVariable String stateNameUrl, 
												@PathVariable String studioName,
												@PathVariable String dateStr,
												ModelMap map) {

    	Map<String, String> stateURLs = LookUp.getInstance().getStatesMap();    	
    	if(!stateURLs.containsKey(stateNameUrl.toLowerCase().trim())){
    		//specified state is invalid. Redirect to new-york
    		stateNameUrl="new-york";
			return "redirect:/studios/"+stateNameUrl+"/"+studioName.trim().toLowerCase()+"/"+dateStr+".html";
    	}
    	
    	if (!stateNameUrl.trim().toLowerCase().equals(stateNameUrl)){
    		//stateNameUrl has capitalized letters.
    		return "redirect:/studios/"+stateNameUrl.trim().toLowerCase()+"/"+studioName.trim().toLowerCase()+"/"+dateStr+".html";
    	}    	
    	
    	if (!studioName.trim().toLowerCase().equals(studioName)){
    		//studioName has capitalized letters.
    		return "redirect:/studios/"+stateNameUrl+"/"+studioName.trim().toLowerCase()+"/"+dateStr+".html";
    	}
    	
		Date urlDate = new Date();
		if (!StringUtils.isEmpty(dateStr)){
			//URL specified date. Parse it out. 
			try {
				urlDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr); //urlLastPart
			} catch (ParseException e) {
			}
		}
    	
		buildStudioPage(map, studioName, stateNameUrl, urlDate);
		return "studios/studio";
	}
    
	protected void buildStudioPage(ModelMap map, String studioNameUrl, String stateNameUrl, Date schedDate) {
		List<Event> events = Event.findEventsForStudioForDate(studioNameUrl, schedDate);

		Studio studio = Studio.createFromNameURL(studioNameUrl);
		
		Map<Date, List<Event>> schedule = GroupList.group(events, "startTime");
		Set<Date> startTimes = new TreeSet<Date>();
		startTimes.addAll(schedule.keySet());
		
		Set<Instructor> instructors = new TreeSet<Instructor>();
		for(final Event event : events){
			if (null != event.getInstructor()){
				instructors.add(event.getInstructor());
			}
		}
			
		Map<String, String> navDates = initializeNavDates(schedDate);		
		
		String stateName = WordUtils.capitalize(StringUtils.replace(stateNameUrl, "-", " "));		
		
		map.put("events", events);
		map.put("startTimes", startTimes);
		map.put("schedule", schedule);
		map.put("studioNameUrl", studioNameUrl);
		map.put("stateName", stateName);
		map.put("stateNameUrl", stateNameUrl);
		map.put("schedDate", schedDate);
		map.put("navDates", navDates);
		map.put("studio", studio);
		map.put("instructors", instructors);
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
