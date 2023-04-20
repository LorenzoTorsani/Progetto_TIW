package it.polimi.tiw.project.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

@WebServlet("/GetImage/*")
public class GetImage extends HttpServlet{
	
	private static final long serialVersionUID = 1L;

	String folderPath = "";

	public void init() throws ServletException {
		folderPath = getServletContext().getInitParameter("outputpath");
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if (pathInfo == null || pathInfo.equals("/")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing file name!");
			return;
		}
		/*
		String filename = URLDecoder.decode(pathInfo.substring(1), "UTF-8");
		*/
		String filename = StringEscapeUtils.escapeJava(request.getParameter("name"));
		File file = new File(folderPath, filename);
		if (!file.exists() || file.isDirectory()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not present");
			return;
		}
		response.setHeader("Content-Type", getServletContext().getMimeType(filename));
		response.setHeader("Content-Length", String.valueOf(file.length()));
		response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
		Files.copy(file.toPath(), response.getOutputStream());
	}
}
