package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
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
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.project.beans.Asta;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.AstaDAO;
import it.polimi.tiw.project.util.ConnectionHandler;

@WebServlet("/chiudiAsta")
public class ChiudiAsta extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public ChiudiAsta() {
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		if(session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}
		
		String id_param = request.getParameter("idAsta");
		Integer idAsta = -1;
		boolean bad_request = false;
		if (id_param == null) {
			bad_request = true;
		}
		try {
			idAsta = Integer.parseInt(id_param);
		} catch (NumberFormatException e) {
			bad_request = true;
		}

		if (bad_request) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter id with format number is required");
			return;
		}
		List<Integer> codici = new ArrayList<Integer>();
		User user = (User) session.getAttribute("user");
		AstaDAO astaDAO = new AstaDAO(connection);
		try {
			
			Asta asta = astaDAO.findAstaById(idAsta);
			if (asta == null) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Asta non trovata");
				return;
			}
			if (!asta.getCreatore().equals(user.getUsername())) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Non hai il permesso");
				return;
			}
			if (!asta.getStato()) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Asta già chiusa");
				return;
			}
			if(asta.getScadenza().compareTo(new  Date(System.currentTimeMillis())) < 0) {
				try {
					codici = astaDAO.chiudiAsta(idAsta);
				} catch(SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile vendere articolo");
					return;
				}
			}
			else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Asta non ancora scaduta");
				return;
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile chiudere asta");
			return;
		}
		
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/Vendo";
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
