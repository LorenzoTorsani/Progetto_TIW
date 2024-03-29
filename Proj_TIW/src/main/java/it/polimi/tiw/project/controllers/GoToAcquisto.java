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

import it.polimi.tiw.project.beans.Asta;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.AstaDAO;
import it.polimi.tiw.project.util.ConnectionHandler;

@WebServlet("/Acquisto")
public class GoToAcquisto extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GoToAcquisto() {
		super();
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

		// Redirect to the Home page and add missions to the parameters
		AstaDAO astaDAO = new AstaDAO(connection);
		User user = (User) session.getAttribute("user");
		List<Asta> asteAggiudicate = new ArrayList<Asta>();
		try {
			asteAggiudicate = astaDAO.getAsteAggiudicateByUser(user.getUsername());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String path = "/WEB-INF/Acquisto.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("asteAggiudicate", asteAggiudicate);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}

		boolean isBadRequest = false;
		String parola = null;
		try {
			parola = StringEscapeUtils.escapeJava(request.getParameter("parola"));
			isBadRequest = parola.isEmpty();
		} catch (NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}

		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Valori parametri mancanti o incorretti");
			return;
		}

		AstaDAO astaDAO = new AstaDAO(connection);
		List<Asta> aste = new ArrayList<Asta>();
		try {
			aste = astaDAO.findAstaByWord(parola);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile trovare parola");
			return;
		}
		
		User user = (User) session.getAttribute("user");
		List<Asta> asteAggiudicate = new ArrayList<Asta>();
		try {
			asteAggiudicate = astaDAO.getAsteAggiudicateByUser(user.getUsername());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
		ctx.setVariable("aste", aste);
		ctx.setVariable("asteAggiudicate", asteAggiudicate);
		String path = "/WEB-INF/Acquisto.html";
		templateEngine.process(path, ctx, response.getWriter());
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
