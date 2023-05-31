package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import it.polimi.tiw.project.beans.Articolo;
import it.polimi.tiw.project.beans.Asta;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.ArticoloDAO;
import it.polimi.tiw.project.dao.AstaDAO;
import it.polimi.tiw.project.util.ConnectionHandler;

@WebServlet("/Vendo")
public class GoToVendo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GoToVendo() {
		super();
	}
	
	
	class RispostaJson {
	    private List<Asta> asteAperte;
	    private List<Asta> asteChiuse;
	    List<Articolo> articoli;
	    
	    public RispostaJson(List<Asta> asteAperte, List<Asta> asteChiuse, List<Articolo> articoli) {
	        this.asteAperte = asteAperte;
	        this.asteChiuse = asteChiuse;
	        this.articoli = articoli;
	    }
	}
	

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");

		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// If the user is not logged in (not present in session) redirect to the login
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}

		User user = (User) session.getAttribute("user");
		ArticoloDAO articoloDAO = new ArticoloDAO(connection);
		List<Articolo> articoli = new ArrayList<Articolo>();
		try {
			articoli = articoloDAO.getArticoliByUser(user.getUsername());
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile ricevere articoli");
			return;
		}

		AstaDAO astaDAO = new AstaDAO(connection);
		List<Asta> asteAperte = new ArrayList<Asta>();		
		List<Asta> asteChiuse = new ArrayList<Asta>();
		try {
			asteAperte = astaDAO.getAsteAperteByUser(user.getUsername());
			asteChiuse = astaDAO.getAsteChiuseByUser(user.getUsername());
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile ricevere aste");
			return;
		}
		
		RispostaJson risposta = new RispostaJson(asteAperte, asteChiuse, articoli);


		// Redirect to the Home page and add missions to the parameters
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(risposta);		

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);

	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
