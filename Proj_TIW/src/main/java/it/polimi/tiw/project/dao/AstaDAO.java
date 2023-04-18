package it.polimi.tiw.project.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import it.polimi.tiw.project.beans.Articolo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;

import it.polimi.tiw.project.beans.Asta;
import it.polimi.tiw.project.beans.User;

public class AstaDAO {
	private Connection connection;

	public AstaDAO(Connection connection) {
		this.connection = connection;
	}

	public void createAsta(Date scadenza, Float rialzoMinimo, Float prezzoIniziale, boolean stato, String creatore)
			throws SQLException {
		String query = "INSERT into asta (scadenza, rialzominimo, prezzoiniziale, stato, creatore) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setDate(1, scadenza);
			pstatement.setFloat(2, rialzoMinimo);
			pstatement.setFloat(3, prezzoIniziale);
			pstatement.setBoolean(4, stato);
			pstatement.setString(5, creatore);
		}

	}

	public Asta findAstaById(int idAsta) throws SQLException {
		Asta asta = null;

		String query = "SELECT * FROM asta WHERE idasta = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idAsta);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					asta = new Asta();
					asta.setId(result.getInt("idasta"));
					asta.setScadenza(result.getDate("scadenza"));
					asta.setRialzoMinimo(result.getInt("rialzominimo"));
					asta.setPrezzoIniziale(result.getDouble("prezzoiniziale"));
					asta.setStato(result.getBoolean("stato"));
					asta.setCreatore(result.getString("creatore"));
					asta.setAggiudicatario(result.getString("aggiudicatario"));
				}
			}
		}

		return asta;
	}

	// stato == true
	public List<Asta> getAsteAperteByUser(String user) throws SQLException, IOException {
		List<Asta> asteAperte = new ArrayList<Asta>();

		String query = "SELECT * " + "FROM progetto_tiw.asta " + "WHERE stato = true AND creatore = ? "
				+ "ORDER BY scadenza ASC";

		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, user);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Asta asta = new Asta();
					asta.setId(result.getInt("idasta"));
					asta.setScadenza(result.getDate("scadenza"));
					asta.setRialzoMinimo(result.getInt("rialzominimo")); // throws IOException
					asta.setPrezzoIniziale(result.getFloat("prezzoiniziale"));
					asta.setStato(result.getBoolean("stato"));
					asta.setCreatore(result.getString("creatore"));
					asteAperte.add(asta);
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		return asteAperte;
	}

	/**
	 * ritorna una mappa con asta e utente aggiudicatario in cui setto solo il suo
	 * indirizzo perche è l'unica cosa che mostro le aste chiuse sono quelle con
	 * stato = false
	 * 
	 * @param user
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public Map<Asta, User> getAsteChiuseByUser(String user) throws SQLException, IOException {
		Map<Asta, User> asteChiuse = new HashMap<Asta, User>();

		String query = "SELECT * " + "FROM asta JOIN utente ON asta.aggiudicatario = utente.username "
				+ "WHERE stato = false AND creatore = ? " + "ORDER BY scadenza ASC";

		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, user);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Asta asta = new Asta();
					User utente = new User();
					asta.setId(result.getInt("idasta"));
					asta.setScadenza(new Date(result.getTimestamp("scadenza").getTime()));
					asta.setRialzoMinimo(result.getInt("rialzominimo")); // throws IOException
					asta.setPrezzoIniziale(result.getFloat("prezzoiniziale"));
					asta.setStato(result.getBoolean("stato"));
					asta.setCreatore(result.getString("creatore"));
					asta.setAggiudicatario(result.getString("aggiudicatario"));
					utente.setAddress(result.getString("indirizzo"));
					utente.setPassword(result.getString("password"));
					utente.setUsername(result.getString("username"));
					asteChiuse.put(asta, utente);
				}
			} catch (SQLException e) {

			}
		}

		return asteChiuse;
	}

	public void createAsta(Date scadenza, int rialzoMinimo, double prezzoIniziale, User user)
			throws SQLException, IOException {
		String query = "INSERT INTO asta (scadenza, rialzominimo, prezzoiniziale, creatore, stato, aggiudicatario) VALUES (?, ?, ?, ?, TRUE, NULL)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setTimestamp(1, new java.sql.Timestamp(scadenza.getTime()));
			pstatement.setInt(2, rialzoMinimo);
			pstatement.setDouble(3, prezzoIniziale);
			pstatement.setString(4, user.getUsername());
			pstatement.executeUpdate();
		}
	}

	public String getCreatorUser(int idAsta) throws SQLException, IOException {
		String user = null;
		String query = "SELECT creatore FROM asta WHERE idasta = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idAsta);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					user = result.getString("creatore");
				}
			} catch (SQLException e) {

			}
		}
		return user;
	}
}
