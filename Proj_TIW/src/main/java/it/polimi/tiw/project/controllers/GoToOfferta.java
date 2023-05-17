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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.project.beans.Articolo;
import it.polimi.tiw.project.beans.Asta;
import it.polimi.tiw.project.beans.Offerta;
import it.polimi.tiw.project.dao.ArticoloDAO;
import it.polimi.tiw.project.dao.AstaDAO;
import it.polimi.tiw.project.dao.OffertaDAO;
import it.polimi.tiw.project.util.ConnectionHandler;

@WebServlet("/GoToOfferta")
public class GoToOfferta extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GoToOfferta() {
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
			idAsta = Integer.parseInt(request.getParameter("idasta"));
		} catch (NumberFormatException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Valori parametri incorretti");
			return;
		}
		
		List<Articolo> articoli = new ArrayList<Articolo>();
		List<Offerta> offerte = new ArrayList<Offerta>();
		Asta asta;
		Double maxOfferta;
		ArticoloDAO articoloDAO = new ArticoloDAO(connection);
		OffertaDAO offertaDAO = new OffertaDAO(connection);
		AstaDAO astaDAO = new AstaDAO(connection);
		
		try {
			articoli = articoloDAO.getArticoliByAsta(idAsta);
			offerte = offertaDAO.findOfferte(idAsta);
			maxOfferta = offertaDAO.getOffertaMaxByAstaid(idAsta);
			System.out.println(maxOfferta);
			asta = astaDAO.findAstaById(idAsta);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile ricevere articoli");
			return;
		}
	
		
		String path = "/WEB-INF/Offerta.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("articoli", articoli);
		ctx.setVariable("offerte", offerte);
		ctx.setVariable("maxOfferta", maxOfferta);
		ctx.setVariable("asta", asta);
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
