package com.parsingUI;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.scheduleyoga.dao.Studio;
import com.scheduleyoga.parser.Parser;
import com.scheduleyoga.parser.StudioOld;

/**
 * Servlet implementation class DisplayTable
 */
public class DisplayTable extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DisplayTable() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String studioUrlName = request.getParameter("studio");
		System.out.println("Processing StudioURLName: "+studioUrlName);
		Studio studio = Studio.createFromNameURL(studioUrlName);
		
		response.setContentType("text/html");
	    PrintWriter out = response.getWriter();
	    out.println("<html>");
	    out.println("<body>");
	    out.println("<h1>Maximka ParserUI 293</h1>");
	    
	    //String studioName = Parser.STUDIO_BABTISTE;
	    //String studioName = Parser.STUDIO_OM_YOGA;
	    //String studioName = Parser.STUDIO_KAIAYOGA;
	    	    
		Parser parser;			
		parser = Parser.createNew(Parser.STUDIO_JOSCHI_NYC);

		//Studio studio = parser.getStudio(studioName);
		out.println("<h2><a href=\"" + studio.getUrlSchedule() 
				+ "\" target=\"_blank\">" + studio.getName() + "</a></h2>");

		String htmlTable = parser.parseStudioSite(studio);
		//String htmlTable = parser.parseSite();

		out.println(htmlTable);
		out.println("<p>Done 33e</p>");

	    out.println("</body>");
	    out.println("</html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
