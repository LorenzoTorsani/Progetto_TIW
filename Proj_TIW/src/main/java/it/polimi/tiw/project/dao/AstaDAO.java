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

import it.polimi.tiw.project.beans.Asta;

public class AstaDAO {
	private Connection connection;

	public AstaDAO(Connection connection) {
		this.connection = connection;
	}
	
	public void createAsta(Date scadenza, Float rialzoMinimo, Float prezzoIniziale, boolean stato) throws SQLException {
		String query = "INSERT into asta (scadenza, rialzominimo, prezzoiniziale, stato) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);){
			pstatement.setDate(1, scadenza);
			pstatement.setFloat(2, rialzoMinimo);
			pstatement.setFloat(3, prezzoIniziale);
			pstatement.setBoolean(4, stato);
		}
		
	}

	// stato == true
	public List<Asta> getAsteAperteByUser(String user) throws SQLException, IOException {
		List<Asta> aste = new ArrayList<Asta>();

		String query = "SELECT * " + "FROM asta JOIN apre ON asta.idasta = apre.idasta"
				+ "JOIN utente ON utente.username = apre.username" + "WHERE stato = true AND utente.username = ?"
				+ "ORDER BY scadenza ASC";

		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, user);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Asta asta = new Asta();
					asta.setId(result.getInt("codice"));
					asta.setScadenza(result.getDate("scadenza"));
					asta.setRialzoMinimo(result.getFloat("rialzo_minimo")); // throws IOException
					asta.setPrezzoIniziale(result.getFloat("prezzo_iniziale"));
					asta.setStato(result.getBoolean("stato"));
					aste.add(asta);
				}
			} catch (SQLException e) {

			}
		}

		return aste;
	}
	
	//stato == false
	public List<Asta> getAsteChiuseByUser(String user) throws SQLException, IOException {
		List<Asta> aste = new ArrayList<Asta>();

		String query = "SELECT * " + "FROM asta JOIN apre ON asta.idasta = apre.idasta"
				+ "JOIN utente ON utente.username = apre.username" + "WHERE stato = false AND utente.username = ?"
				+ "ORDER BY scadenza ASC";

		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, user);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Asta asta = new Asta();
					asta.setId(result.getInt("codice"));
					asta.setScadenza(result.getDate("scadenza"));
					asta.setRialzoMinimo(result.getFloat("rialzo_minimo")); // throws IOException
					asta.setPrezzoIniziale(result.getFloat("prezzo_iniziale"));
					asta.setStato(result.getBoolean("stato"));
					aste.add(asta);
				}
			} catch (SQLException e) {

			}
		}

		return aste;
	}

	
	//TODO createAsta
}
