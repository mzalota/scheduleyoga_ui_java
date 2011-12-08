package com.parsingUI;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.scheduleyoga.dao.DBAccess;
import com.scheduleyoga.dao.Instructor;
import com.scheduleyoga.parser.Event;

//This will make easier to autowired
@Repository("userDao")
// Default is read only
@Transactional
public class EntityDAO{
	private HibernateTemplate hibernateTemplate;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

//	@Transactional(readOnly = false)
//	public void saveUser(User user) {
//		hibernateTemplate.saveOrUpdate(user);
//
//	}
//
//	@Transactional(readOnly = false)
//	public void deleteUser(User user) {
//		hibernateTemplate.delete(user);
//
//	}

//	@SuppressWarnings("unchecked")
//	public List<User> getAllUser(User user) {
//		return (List<User>) hibernateTemplate.find("from "
//				+ User.class.getName());
//	}

//	public User selectUserById(String userId) {
//		return hibernateTemplate.get(User.class, userId);
//	}
	
	
	public List<Event> findEventsForInstructorForDate(Instructor instructor, Date date){
		
		String queryStr = 	" select ev " +
							" from Event as ev " +							
							" where date(ev.startTime) = date(?) " +
								" and instructor = ? ";
		
		@SuppressWarnings("unchecked")
		List<Event> events = (List<Event>) hibernateTemplate.find(queryStr,date,instructor);
		
		return events;
	}
	
	public Instructor createFromNameURL(String nameURL) {
		
		@SuppressWarnings("unchecked")
		List<Instructor> results = (List<Instructor>) hibernateTemplate.find("from Instructor where nameForUrl=?",nameURL);
		
		if (results.size() <= 0) {
			return null;
		}

		return results.get(0);
	}
}

