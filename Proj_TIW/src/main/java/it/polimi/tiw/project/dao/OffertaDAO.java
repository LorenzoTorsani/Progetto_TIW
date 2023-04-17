package it.polimi.tiw.project.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import it.polimi.tiw.project.beans.Articolo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import it.polimi.tiw.project.beans.Offerta;


public class OffertaDAO {
	private Connection connection;

	public OffertaDAO(Connection connection) {
		this.connection = connection;
	}
	public void createOfferta(String user, int idAsta, Float offerta) throws SQLException {
		String query = "INSERT into offerta (username, idasta, offerta) VALUES (?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);){
			pstatement.setString(1, user);
			pstatement.setInt(2, idAsta);
			pstatement.setFloat(3, offerta);
		}
	}


}
