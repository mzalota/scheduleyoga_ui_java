package com.parsingUI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.scheduleyoga.common.GroupList;
import com.scheduleyoga.dao.Studio;
import com.scheduleyoga.parser.Event;
import com.scheduleyoga.parser.Helper;

@Controller
@RequestMapping("*")
public class DefaultController {
	
    protected final Log logger = LogFactory.getLog(getClass());
	
	@RequestMapping("/yoga{endingName}/*")
	public String catchAll(@PathVariable String endingName) {
		return "yoga"+endingName+"/index";
	}
	
	@RequestMapping("/{viewName}.jsp")
	public String catchJSPFiles(@PathVariable String viewName) {
		return viewName;
	}
	
	@RequestMapping("/")
	public String catchTopFile() {
		return "index";
	}
}
