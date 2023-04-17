package it.polimi.tiw.project.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.project.beans.Asta;

public class AstaDAO {
	private Connection connection;

	public AstaDAO(Connection connection) {
		this.connection = connection;
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
