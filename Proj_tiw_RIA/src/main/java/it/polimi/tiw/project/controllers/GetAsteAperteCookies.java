package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.project.beans.Asta;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.AstaDAO;
import it.polimi.tiw.project.util.ConnectionHandler;

@WebServlet("/GetAsteAperteCookies")
public class GetAsteAperteCookies extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public void init() throws ServletException{
		ServletContext servletContext = getServletContext();
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		HttpSession session = request.getSession();
		String cookieString = request.getParameter("cookieAstaIdList");
		String[] parti = cookieString.split(" ");
		AstaDAO dao = new AstaDAO(connection);
		List<Asta> aste = new ArrayList<Asta>();
		List<Asta> toReturn = new ArrayList<Asta>();
		List<Integer> id = new ArrayList<Integer>();
		User user = (User) session.getAttribute("user");
		
		aste = null;
		
		for(int i = 0; i < parti.length; i++) {
			if(!parti[i].equals("vendo")) {
				id.add(Integer.parseInt(parti[i]));
			}
		}
		
		try {
			aste = dao.getAsteAperteByUser(user.getUsername());
		} catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		for(Integer ids : id) {
			for(Asta a : aste) {
				if(a.getIdAsta() == ids) {
					toReturn.add(a);
				}
			}
		}
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(toReturn);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		doGet(request, response);
	}
}
