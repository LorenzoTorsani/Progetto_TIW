package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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

import it.polimi.tiw.project.beans.Asta;
import it.polimi.tiw.project.beans.Offerta;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.AstaDAO;
import it.polimi.tiw.project.dao.OffertaDAO;
import it.polimi.tiw.project.util.ConnectionHandler;

@WebServlet("/GoToDettaglioAsta")
public class GoToDettaglioAsta extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GoToDettaglioAsta() {
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

		// get and check params
		Integer idAsta = null;
		try {
			idAsta = Integer.parseInt(request.getParameter("idAsta"));
		} catch (NumberFormatException | NullPointerException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		
		//return the asta with the selected id
		User user = (User) session.getAttribute("user");
		AstaDAO astaDAO = new AstaDAO(connection);
		OffertaDAO offertaDAO = new OffertaDAO(connection);
		Asta asta = new Asta();
		List<Offerta> offerte = new ArrayList<Offerta>();
		try {
			asta = astaDAO.findAstaById(idAsta);
			if (asta == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Risorsa non trovata");
				return;
			}
			if (!astaDAO.getCreatorUser(idAsta).equals(user.getUsername()) ) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Utente non autorizzato");
				return;
			}
			offerte = offertaDAO.findOfferte(idAsta);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile vedere dettagli");
			return;
		}
		
		String path = "/WEB-INF/DettaglioAsta.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("asta", asta);
		ctx.setVariable("offerte", offerte);
		java.util.Date tempdate = null;
		try {
			tempdate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(asta.getScadenza());
			System.out.println(asta.getScadenza());
			System.out.println(tempdate);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		boolean chiudibile = (tempdate.compareTo(new  Date(System.currentTimeMillis())) < 0);
		ctx.setVariable("chiudibile", chiudibile);
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
