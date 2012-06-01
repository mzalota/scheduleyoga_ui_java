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
import org.apache.velocity.app.FieldMethodizer;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
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
import com.scheduleyoga.dao.Style;
import com.scheduleyoga.parser.Event;
import com.scheduleyoga.parser.Helper;

@Controller
@RequestMapping("/classes")
public class ClassesController {
	
    protected final Log logger = LogFactory.getLog(getClass());
    
    EntityDAO entityDAO;
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}
	
    @RequestMapping(value = "*", method = RequestMethod.GET)
    public String otherURLs(ModelMap map) {
    	return "redirect:/classes/new-york/";
    }  
	
    @RequestMapping(value = "/{stateNameUrl}/", method = RequestMethod.GET)
	protected String prepareClassesListPage(@PathVariable String stateNameUrl, 
											 ModelMap map) {

    	Map<String, String> stateURLs = LookUp.getInstance().getStatesMap();    	
    	if(!stateURLs.containsKey(stateNameUrl.toLowerCase().trim())){
    		//specified state is invalid. Redirect to new-york
    		stateNameUrl="new-york";
			return "redirect:/classes/"+stateNameUrl+"/";
    	}
    	
    	if (!stateNameUrl.trim().toLowerCase().equals(stateNameUrl)){
    		//stateNameUrl has capitalized letters.
    		return "redirect:/classes/"+stateNameUrl.trim().toLowerCase()+"/";
    	}       	
    	
		String stateName = WordUtils.capitalize(StringUtils.replace(stateNameUrl, "-", " "));
		
		List<Style> stylesList = Style.allStyles();		
		Map<String, Style> styles = new HashMap<String, Style>();
		
		for(Style style : stylesList) {
			styles.put(style.getName(), style);
		}
		
		map.put("stateNameUrl", stateNameUrl);
		map.put("stateName", stateName);
		map.put("styles", styles);
		map.put("StyleStatic", new FieldMethodizer("com.scheduleyoga.dao.Style")); //"com.scheduleyoga.dao.Style"
				
		return "/classes/classes_list";
	}
    
    @RequestMapping(value = "/{stateName}/{styleName}/", method = RequestMethod.GET)
	protected String prepareClassPage(@PathVariable String stateName, 
									   @PathVariable String styleName, 
									   ModelMap map) {
		
    	return processClassPage(stateName, styleName, new Date(), map);
    	
	}
    
    @RequestMapping(value = "/{stateNameUrl}/{styleName}/{dateStr}.html", method = RequestMethod.GET)
	protected String prepareClassSpecificDatePage(@PathVariable String stateNameUrl, 
												@PathVariable String styleName,
												@PathVariable String dateStr,
												ModelMap map) {
    	
		Date urlDate = new Date();
		if (!StringUtils.isEmpty(dateStr)){
			//URL specified date. Parse it out. 
			try {
				urlDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr); //urlLastPart
			} catch (ParseException e) {
			}
		}
    	
		Date today = new Date();		
		if (today.after(urlDate)){
			//The date in the URL is in the past. Redirect to home page for the Class (with today's schedule).
			return "redirect:/classes/"+stateNameUrl.toLowerCase().trim()+"/"+styleName+"/";
		}
    	
		return processClassPage(stateNameUrl, styleName, urlDate, map);
	}

	/**
	 * @param stateName
	 * @param styleName
	 * @param dateStr
	 * @param map
	 * @return
	 */
	protected String processClassPage(String stateNameUrl, String styleName, Date urlDate, ModelMap map) {
		
		
    	Map<String, String> stateURLs = LookUp.getInstance().getStatesMap();    	
    	if(!stateURLs.containsKey(stateNameUrl.toLowerCase().trim())){
    		//specified state is invalid. Redirect to new-york
    		stateNameUrl="new-york";
			return "redirect:/classes/"+stateNameUrl+"/"+styleName.trim().toLowerCase()+"/";
    	}
    	
    	if (!stateNameUrl.trim().toLowerCase().equals(stateNameUrl)){
    		//stateNameUrl has capitalized letters.
    		return "redirect:/classes/"+stateNameUrl.trim().toLowerCase()+"/"+styleName.trim().toLowerCase()+"/";
    	}     
    	
    	if (!styleName.trim().toLowerCase().equals(styleName)){
    		//styleName has capitalized letters.
    		return "redirect:/classes/"+stateNameUrl.trim().toLowerCase()+"/"+styleName.trim().toLowerCase()+"/";
    	}     
    	
		Style style = Style.createNewFromUrl(styleName);
		if (null == style){
			return "redirect:/classes/"+stateNameUrl+"/"+Style.NAME_BEGINNER.toLowerCase()+"/";
		}
		
		buildClassPage(map, style, stateNameUrl, urlDate);
		return "classes/class";
	}
	
    
	protected void buildClassPage(ModelMap map, Style style, String stateNameUrl, Date schedDate) {
		
		List<Event> events = Event.findEventsForStyleForDate(style, schedDate,stateNameUrl);
		
		Map<Date, List<Event>> schedule = GroupList.group(events, "startTime");
		
		logger.info("found "+events.size()+" events for Styles "+style+" date is "+schedDate);
		
		Set<Date> startTimes = new TreeSet<Date>();
		startTimes.addAll(schedule.keySet());
			
		Map<String, String> navDates = initializeNavDates(schedDate);		
		
		String stateName = WordUtils.capitalize(StringUtils.replace(stateNameUrl, "-", " "));		
		
		map.put("events", events);
		map.put("startTimes", startTimes);
		map.put("schedule", schedule);
		map.put("styleNameUrl", style.getNameForUrl());
		map.put("stateName", stateName);
		map.put("stateNameUrl", stateNameUrl);
		map.put("schedDate", schedDate);
		map.put("navDates", navDates);
		map.put("style", style);				

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
