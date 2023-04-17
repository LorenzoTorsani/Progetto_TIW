package it.polimi.tiw.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import it.polimi.tiw.project.beans.Articolo;
import it.polimi.tiw.project.beans.User;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;

public class PossiedeDAO {
	private Connection connection;

	public PossiedeDAO(Connection connection) {
		this.connection = connection;
	}
	
	public User getUserbyArticolo(int code) throws SQLException, IOException {
		User user = null;
		
		String query = "SELECT username, password, indirizzo"
				+ "FROM possiede JOIN utente ON possiede.username = utente.username"
				+ "WHERE possiede.codice = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, code);
			try (ResultSet result = pstatement.executeQuery();) {
				user.setUsername(result.getString("username"));
				user.setPassword(result.getString("password"));
				user.setAddress(result.getString("indirizzo"));
			}
		}
		return user;
	}
	public void createPossiede(String username, int codice) throws SQLException, IOException{
		String query = "INSERT into possiede (username, codice) VALUES(?, ?)";
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setString(1, username);
			pstatement.setInt(2, codice);
		}
	}

}
