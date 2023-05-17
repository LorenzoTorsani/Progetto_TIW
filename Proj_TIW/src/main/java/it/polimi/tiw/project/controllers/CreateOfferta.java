package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.ArticoloDAO;
import it.polimi.tiw.project.dao.AstaDAO;
import it.polimi.tiw.project.dao.OffertaDAO;
import it.polimi.tiw.project.util.ConnectionHandler;

@WebServlet("/CreateOfferta")
public class CreateOfferta extends HttpServlet{
	private static final long serialVersionUID = 1L;

	private Connection connection = null;
	private TemplateEngine templateEngine;

	public CreateOfferta() {
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
	
	/*
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		HttpSession session = request.getSession();
		if(session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}
		boolean isBadRequest = false;
		Integer idAsta = null;
		Double offerta = null;
		try {
			idAsta = Integer.parseInt(request.getParameter("idAsta"));
			System.out.println(idAsta);
			offerta = Double.parseDouble(request.getParameter("Offerta"));
			System.out.println(offerta);
			ArticoloDAO articoloDAO = new ArticoloDAO(connection);
			isBadRequest = (idAsta == null || offerta == null);
		} catch(NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if(isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Valori parametri mancanti o incorretti");
			return;
		}
		User user = (User) session.getAttribute("user");
		OffertaDAO offertaDAO = new OffertaDAO(connection);
		AstaDAO astaDAO = new AstaDAO(connection);
		Boolean error = false;
		try {
			Double off = offertaDAO.getOffertaMaxByAstaid(idAsta);
			if(off > 0) {
				if(offerta <= off) {
					error = true;
				}
				else {
					offertaDAO.createOfferta(user.getUsername(), idAsta, offerta);
				}
			}
			else {
				off = astaDAO.findAstaById(idAsta).getPrezzoIniziale();
				if(offerta <= off + astaDAO.findAstaById(idAsta).getRialzoMinimo()) {
					error = true;
				}
				else {
					offertaDAO.createOfferta(user.getUsername(), idAsta, offerta);
				}
			}
			if(error) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Offerta non sufficiente");
				return;
			}
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile creare offerta");
			return;
		}
		List<Articolo> articoli = new ArrayList<Articolo>();
		List<Offerta> offerte = new ArrayList<Offerta>();
		Asta asta;
		Double maxOfferta;
		ArticoloDAO articoloDAO = new ArticoloDAO(connection);
		
		try {
			articoli = articoloDAO.getArticoliByAsta(idAsta);
			offerte = offertaDAO.findOfferte(idAsta);
			maxOfferta = offertaDAO.getOffertaMaxByAstaid(idAsta);
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
	*/
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		HttpSession session = request.getSession();
		if(session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}
		
		boolean isBadRequest = false;
		Integer idAsta = null;
		Double offerta = null;
		try {
			idAsta = Integer.parseInt(request.getParameter("idAsta"));
			offerta = Double.parseDouble(request.getParameter("Offerta"));
			ArticoloDAO articoloDAO = new ArticoloDAO(connection);
			isBadRequest = (idAsta == null || offerta == null);
		} catch(NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if(isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Valori parametri mancanti o incorretti");
			return;
		}
		
		User user = (User) session.getAttribute("user");
		OffertaDAO offertaDAO = new OffertaDAO(connection);
		AstaDAO astaDAO = new AstaDAO(connection);
		Boolean error = false;
		try {
			Double off = offertaDAO.getOffertaMaxByAstaid(idAsta);
			if(off > 0) {
				if(offerta <= off) {
					error = true;
				}
				else {
					offertaDAO.createOfferta(user.getUsername(), idAsta, offerta);
				}
			}
			else {
				off = astaDAO.findAstaById(idAsta).getPrezzoIniziale();
				if(offerta <= off + astaDAO.findAstaById(idAsta).getRialzoMinimo()) {
					error = true;
				}
				else {
					offertaDAO.createOfferta(user.getUsername(), idAsta, offerta);
				}
			}
			if(error) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Offerta non sufficiente");
				return;
			}
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile creare offerta");
			return;
		}
		
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GoToOfferta?idasta=" + idAsta;
		response.sendRedirect(path);
		
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
