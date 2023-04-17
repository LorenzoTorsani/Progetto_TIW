package it.polimi.tiw.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import it.polimi.tiw.project.beans.Articolo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;


public class ArticoloDAO {
	private Connection connection;

	public ArticoloDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Articolo> getArticoliByUser(String user) throws SQLException, IOException {
		List<Articolo> articoli = new ArrayList<Articolo>();
		
		String query = "SELECT * "
				+ "FROM articolo JOIN possiede ON articolo.codice = possiede.codice"
				+ "JOIN utente ON utente.username = possiede.username"
				+ "WHERE utente.username = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, user);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Articolo articolo = new Articolo();
					articolo.setCode(result.getInt("codice"));
					articolo.setDescription(result.getString("descrizione"));
					//articolo.setImage(result.getBlob("immagine"));	// throws IOException
					articolo.setName(result.getString("nome"));
					articolo.setPrice(result.getFloat("prezzo"));
					articolo.setSold(result.getBoolean("venduto"));
					articoli.add(articolo);
				}
			} catch (SQLException e) {
				
			}
		}
		
		return articoli;
	}
	
	public void createArticolo(String description, String name, Float price, boolean sold) throws SQLException, IOException {
		//ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//ImageIO.write(image, "png", baos);
		//byte[] imageBytes = baos.toByteArray();
		//Blob imageBlob = connection.createBlob();
		//imageBlob.setBytes(1, imageBytes);
		
		String query = "INSERT into articolo (descrizione, nome, prezzo, venduto) VALUES(?, ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setString(1, description);
			//pstatement.setBlob(2, imageBlob);
			pstatement.setString(2, name);
			pstatement.setFloat(3, price);
			pstatement.setBoolean(4, sold);
			pstatement.executeUpdate();
		}
	}
}
