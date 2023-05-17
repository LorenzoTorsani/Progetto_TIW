package it.polimi.tiw.project.controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.dao.ArticoloDAO;
import it.polimi.tiw.project.util.ConnectionHandler;

@WebServlet("/CreateArticolo")
@MultipartConfig
public class CreateArticolo extends HttpServlet{
	private static final long serialVersionUID = 1L;

	private Connection connection = null;
	String folderPath = "";

	public CreateArticolo() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		folderPath = getServletContext().getInitParameter("outputpath");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		HttpSession session = request.getSession();
		if(session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}
		String fileName = new String();
		boolean isBadRequest = false;
		String name = null;
		String description = null;
		Double price = 0.0;
		boolean sold = false;
		//Part bits = null;
		//BufferedImage image = null;
		try {
			name = StringEscapeUtils.escapeJava(request.getParameter("name"));
			description = StringEscapeUtils.escapeJava(request.getParameter("description"));
			price = Double.parseDouble(request.getParameter("price"));
			//bits = request.getPart("image");
			//image = ImageIO.read(bits.getInputStream());
			sold = false;
			isBadRequest = name.isEmpty() || description.isEmpty();
		} catch(NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if(isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}
		else {
			Part filePart = request.getPart("file");
			if(filePart == null || filePart.getSize() <= 0) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing file in request!");
				return;
			}
			String contentType = filePart.getContentType();
			if (!contentType.startsWith("image")) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File format not permitted");
				return;
			}
			fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
			String outputPath = folderPath + fileName;
			File file = new File(outputPath);
			try (InputStream fileContent = filePart.getInputStream()) {
				Files.copy(fileContent, file.toPath());
			} catch (Exception e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while saving file");
				return;
			}
		}
		User user = (User) session.getAttribute("user");
		ArticoloDAO articoloDAO = new ArticoloDAO(connection);
		try {
			articoloDAO.createArticolo(description, name, price, fileName, sold, user.getUsername());
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile creare articolo");
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
