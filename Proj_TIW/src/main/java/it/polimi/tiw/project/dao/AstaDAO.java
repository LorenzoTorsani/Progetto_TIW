package it.polimi.tiw.project.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;

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

	public Map<Asta, String> findAstaById(int idAsta) throws SQLException {
		Map<Asta, String> asta = new HashMap<Asta, String>();

		String query = "SELECT * FROM asta WHERE idasta = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idAsta);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					Asta astaTmp = new Asta();
					astaTmp.setId(result.getInt("idasta"));
					Date scadenza = new Date(result.getTimestamp("scadenza").getTime());
					LocalDate date = scadenza.toLocalDate();
					astaTmp.setScadenza(scadenza);
					Instant i = date.atStartOfDay(ZoneOffset.UTC).toInstant();
					Instant oggi = Instant.now();
					Duration tempoRimanente = Duration.between(oggi, i);
					long giorni = tempoRimanente.toDays();
					long ore = tempoRimanente.toHours() % 24;
					long minuti = tempoRimanente.toMinutes() % 60;
					long secondi = tempoRimanente.getSeconds() % 60;
					String tempo = giorni + " giorni, " + ore + " ore, " + minuti + " minuti, " + secondi + " secondi";
					astaTmp.setRialzoMinimo(result.getInt("rialzominimo")); // throws IOException
					astaTmp.setPrezzoIniziale(result.getFloat("prezzoiniziale"));
					astaTmp.setStato(result.getBoolean("stato"));
					astaTmp.setCreatore(result.getString("creatore"));
					asta.put(astaTmp, tempo);
				}
			}
		}

		return asta;
	}

	// stato == true
	public Map<Asta, String> getAsteAperteByUser(String user) throws SQLException, IOException {
		Map<Asta, String> asteAperte = new HashMap<Asta, String>();

		String query = "SELECT * " + "FROM progetto_tiw.asta " + "WHERE stato = true AND creatore = ? "
				+ "ORDER BY scadenza ASC";

		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, user);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Asta asta = new Asta();
					asta.setId(result.getInt("idasta"));
					Date scadenza = new Date(result.getTimestamp("scadenza").getTime());
					LocalDate date = scadenza.toLocalDate();
					asta.setScadenza(scadenza);
					Instant i = date.atStartOfDay(ZoneOffset.UTC).toInstant();
					Instant oggi = Instant.now();
					Duration tempoRimanente = Duration.between(oggi, i);
					long giorni = tempoRimanente.toDays();
					long ore = tempoRimanente.toHours() % 24;
					long minuti = tempoRimanente.toMinutes() % 60;
					long secondi = tempoRimanente.getSeconds() % 60;
					String tempo = giorni + " giorni, " + ore + " ore, " + minuti + " minuti, " + secondi + " secondi";
					asta.setRialzoMinimo(result.getInt("rialzominimo")); // throws IOException
					asta.setPrezzoIniziale(result.getFloat("prezzoiniziale"));
					asta.setStato(result.getBoolean("stato"));
					asta.setCreatore(result.getString("creatore"));
					asteAperte.put(asta, tempo);
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
