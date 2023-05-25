package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.ArticoloDAO;
import it.polimi.tiw.project.dao.AstaDAO;
import it.polimi.tiw.project.util.ConnectionHandler;

@WebServlet("/CreateAsta")
public class CreateAsta extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Connection connection = null;

	public CreateAsta() {
		super();
	}
	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		HttpSession session = request.getSession();
		if(session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}
		boolean isBadRequest = false;
		Integer rialzoMinimo = 0;
		Double prezzoIniziale = 0.0;
		java.util.Date tempdate = null;
		String[] codes = null;
		Instant scadenza = null;
		int[] codici = null;
		try {
			try {
				codes = request.getParameterValues("articoli");
				codici = new int[codes.length];
			} catch (NullPointerException e) {
				e.printStackTrace();
				isBadRequest = true;
			}
			try {
				tempdate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(request.getParameter("scadenza"));

			} catch (ParseException e) {
				e.printStackTrace();
				isBadRequest = true;
			}
			scadenza = tempdate.toInstant();
			rialzoMinimo = Integer.parseInt(request.getParameter("rialzoMinimo"));
			for(int i = 0; i < codes.length; i++) {
				codici[i] = Integer.parseInt(codes[i]);
			}
			ArticoloDAO articoloDAO = new ArticoloDAO(connection);
			try {
				prezzoIniziale = articoloDAO.getPrezzoIniziale(codici);
			}catch(SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile ottenere prezzo iniziale");
				return;
			}
			isBadRequest = scadenza == null;
		} catch(NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if(isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Valori parametri mancanti o incorretti");
			return;
		}
		
		
		User user = (User) session.getAttribute("user");
		AstaDAO astaDAO = new AstaDAO(connection);
		ArticoloDAO articoloDAO = new ArticoloDAO(connection);
		try {
			Boolean error = false;
			Instant oggi = new java.util.Date(System.currentTimeMillis()).toInstant();
			if(rialzoMinimo > 0 && scadenza.isAfter(oggi)) {
				for(int i = 0; i < codici.length; i++) {
					Integer check = articoloDAO.getIdByCodice(codici[i]);
					if(check != null) {
						error = true;
					}
				}
				if(!error) {
					int id = astaDAO.createAsta(scadenza, rialzoMinimo, prezzoIniziale, user.getUsername());
					for(int i = 0; i < codici.length; i++) {
							articoloDAO.updateArticolo(codici[i], id);
					}
				}
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Valore parametri incorretto");
				return;
			}
			if(error) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Almeno un articolo già in un'asta");
				return;
			}
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile creare asta");
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