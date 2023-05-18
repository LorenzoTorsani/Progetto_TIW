package it.polimi.tiw.project.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.IOException;

import it.polimi.tiw.project.beans.Asta;
import it.polimi.tiw.project.beans.User;

public class AstaDAO {
	private Connection connection;

	public AstaDAO(Connection connection) {
		this.connection = connection;
	}

	public int createAsta(Date scadenza, Integer rialzoMinimo, Double prezzoIniziale, String creatore)
			throws SQLException {
		connection.setAutoCommit(false);

		String query = "INSERT into asta (scadenza, rialzominimo, prezzoiniziale, stato, creatore) VALUES (?, ?, ?, true, ?)";
		PreparedStatement insertStatement = null;

		String query2 = "SELECT MAX(asta.idasta) FROM asta";
		PreparedStatement selectStatement = null;
		ResultSet resultSet = null;

		int id = 0; // Valore di default se la query non restituisce risultati

		try {
			insertStatement = connection.prepareStatement(query);
			insertStatement.setDate(1, scadenza);
			insertStatement.setInt(2, rialzoMinimo);
			insertStatement.setDouble(3, prezzoIniziale);
			insertStatement.setString(4, creatore);
			insertStatement.executeUpdate();

			selectStatement = connection.prepareStatement(query2);
			resultSet = selectStatement.executeQuery();

			if (resultSet.next()) {
				id = resultSet.getInt("MAX(asta.idasta)");
			}

			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			connection.setAutoCommit(true);

			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (Exception e) {
					throw e;
				}
			}

			if (selectStatement != null) {
				try {
					selectStatement.close();
				} catch (Exception e) {
					throw e;
				}
			}

			if (insertStatement != null) {
				try {
					insertStatement.close();
				} catch (Exception e) {
					throw e;
				}
			}
		}

		return id;
	}

	public void chiudiAsta(int idAsta) throws SQLException {
		connection.setAutoCommit(false);

		List<Integer> codici = new ArrayList<Integer>();
		String updateQuery = "UPDATE asta " + "SET `stato` = '0', `aggiudicatario` = "
				+ "(SELECT offerente FROM offerta " + "WHERE offerta.idasta = asta.idasta "
				+ "AND quantitaofferta = (SELECT MAX(quantitaofferta) FROM offerta "
				+ "WHERE offerta.idasta = asta.idasta)) WHERE (idasta = ?)";

		String selectQuery = "SELECT articolo.codice FROM asta JOIN articolo ON asta.idasta = articolo.idasta WHERE asta.idasta = ?";

		PreparedStatement updateStatement = null;
		PreparedStatement selectStatement = null;
		ResultSet resultSet = null;

		try {
			updateStatement = connection.prepareStatement(updateQuery);
			updateStatement.setInt(1, idAsta);
			updateStatement.executeUpdate();

			// Se la prima query ha avuto successo, esegui la seconda query
			selectStatement = connection.prepareStatement(selectQuery);
			selectStatement.setInt(1, idAsta);
			resultSet = selectStatement.executeQuery();

			while (resultSet.next()) {
				codici.add(resultSet.getInt("codice"));
			}

			// setta come venduti gli articoli contenuti nell'asta
			ArticoloDAO articoloDAO = new ArticoloDAO(connection);
			for (int i = 0; i < codici.size(); i++) {
				articoloDAO.setVendutiByCodice(codici.get(i));
			}

			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			connection.setAutoCommit(true);

			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (Exception e) {
					throw e;
				}
			}

			if (selectStatement != null) {
				try {
					selectStatement.close();
				} catch (Exception e) {
					throw e;
				}
			}

			if (updateStatement != null) {
				try {
					updateStatement.close();
				} catch (Exception e) {
					throw e;
				}
			}
		}

	}

	public void chiudiAstaNoOff(int idAsta) throws SQLException {
		connection.setAutoCommit(false);

		List<Integer> codici = new ArrayList<Integer>();
		String updateQuery = "UPDATE asta SET `stato` = '0' WHERE (idasta = ?)";
		String selectQuery = "SELECT articolo.codice FROM asta JOIN articolo ON asta.idasta = articolo.idasta WHERE asta.idasta = ?";

		PreparedStatement updateStatement = null;
		PreparedStatement selectStatement = null;
		ResultSet resultSet = null;

		try {
			updateStatement = connection.prepareStatement(updateQuery);
			updateStatement.setInt(1, idAsta);
			updateStatement.executeUpdate();

			// Se la prima query ha avuto successo, esegui la seconda query
			selectStatement = connection.prepareStatement(selectQuery);
			selectStatement.setInt(1, idAsta);
			resultSet = selectStatement.executeQuery();

			while (resultSet.next()) {
				codici.add(resultSet.getInt("codice"));
			}

			// elimina id asta dagli articoli contenuti in un'asta chiusa senza offerta
			ArticoloDAO articoloDAO = new ArticoloDAO(connection);
			for (int i = 0; i < codici.size(); i++) {
				articoloDAO.setInvendutiByCodice(codici.get(i));
			}

			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			connection.setAutoCommit(true);

			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (Exception e) {
					throw e;
				}
			}

			if (selectStatement != null) {
				try {
					selectStatement.close();
				} catch (Exception e) {
					throw e;
				}
			}

			if (updateStatement != null) {
				try {
					updateStatement.close();
				} catch (Exception e) {
					throw e;
				}
			}
		}
	}

	public Asta findAstaById(int idAsta) throws SQLException {
		Asta asta = new Asta();
		String query = "SELECT * FROM asta WHERE idasta = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idAsta);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					asta.setIdAsta(result.getInt("idasta"));
					java.sql.Timestamp timestamp = result.getTimestamp("scadenza");
					LocalDate scadenza = new Date(timestamp.getTime()).toLocalDate();
					java.util.Date utilDate = new java.util.Date(timestamp.getTime());
					java.time.LocalDateTime localDateTime = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
					java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
					asta.setScadenza(localDateTime.format(formatter));
					Instant i = scadenza.atStartOfDay(ZoneOffset.UTC).toInstant();
					Instant oggi = Instant.now();
					Duration tempoRimanente = Duration.between(oggi, i);
					long giorni = tempoRimanente.toDays();
					long ore = tempoRimanente.toHours() % 24;
					long minuti = tempoRimanente.toMinutes() % 60;
					long secondi = tempoRimanente.getSeconds() % 60;
					String tempo = giorni + " giorni, " + ore + " ore, " + minuti + " minuti, " + secondi + " secondi";
					asta.setTempoMancante(tempo);
					asta.setRialzoMinimo(result.getInt("rialzominimo")); // throws IOException
					asta.setPrezzoIniziale(result.getFloat("prezzoiniziale"));
					asta.setStato(result.getBoolean("stato"));
					asta.setCreatore(result.getString("creatore"));
				}
			}
		}

		return asta;
	}

	// stato == true
	public List<Asta> getAsteAperteByUser(String user) throws SQLException, IOException {
		List<Asta> asteAperte = new ArrayList<Asta>();

		String query = "SELECT IFNULL(MAX(offerta.quantitaofferta), -1) AS max_quantita, asta.idasta, asta.scadenza, asta.rialzominimo, asta.prezzoiniziale, asta.creatore "
				+ "FROM asta " + "LEFT JOIN offerta ON asta.idasta = offerta.idasta "
				+ "WHERE asta.stato = true AND asta.creatore = ? "
				+ "GROUP BY asta.idasta, asta.scadenza, asta.rialzominimo, asta.prezzoiniziale, asta.creatore "
				+ "ORDER BY asta.scadenza ASC";

		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, user);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Asta asta = new Asta();
					asta.setIdAsta(result.getInt("idasta"));
					java.sql.Timestamp timestamp = result.getTimestamp("scadenza");
					LocalDate scadenza = new Date(timestamp.getTime()).toLocalDate();
					java.util.Date utilDate = new java.util.Date(timestamp.getTime());
					java.time.LocalDateTime localDateTime = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
					java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
					asta.setScadenza(localDateTime.format(formatter));
					Instant i = scadenza.atStartOfDay(ZoneOffset.UTC).toInstant();
					Instant oggi = Instant.now();
					Duration tempoRimanente = Duration.between(oggi, i);
					long giorni = tempoRimanente.toDays();
					long ore = tempoRimanente.toHours() % 24;
					long minuti = tempoRimanente.toMinutes() % 60;
					long secondi = tempoRimanente.getSeconds() % 60;
					String tempo = giorni + " giorni, " + ore + " ore, " + minuti + " minuti, " + secondi + " secondi";
					asta.setTempoMancante(tempo);
					asta.setRialzoMinimo(result.getInt("rialzominimo")); // throws IOException
					asta.setPrezzoIniziale(result.getFloat("prezzoiniziale"));
					// asta.setStato(result.getBoolean("stato"));
					asta.setCreatore(result.getString("creatore"));
					asta.setOffertaMax(result.getDouble("max_quantita"));
					asteAperte.add(asta);
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		return asteAperte;
	}

	public Map<Asta, User> getAsteChiuseByUser(String user) throws SQLException, IOException {
		Map<Asta, User> asteChiuse = new HashMap<Asta, User>();

		String query = "SELECT IFNULL(MAX(offerta.quantitaofferta), -1) AS max_quantita, asta.idasta, asta.scadenza, asta.prezzoiniziale, asta.rialzominimo, asta.stato, asta.creatore, asta.aggiudicatario, utente.username, utente.indirizzo "
				+ "FROM asta LEFT JOIN offerta ON asta.idasta = offerta.idasta JOIN utente ON asta.aggiudicatario = utente.username "
				+ "WHERE stato = false AND creatore = ? " 
				+ "GROUP BY asta.idasta, asta.scadenza, asta.rialzominimo, asta.prezzoiniziale, asta.creatore "
				+ "ORDER BY scadenza ASC";

		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, user);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Asta asta = new Asta();
					User utente = new User();
					asta.setIdAsta(result.getInt("idasta"));
					java.sql.Timestamp timestamp = result.getTimestamp("scadenza");
					java.util.Date utilDate = new java.util.Date(timestamp.getTime());
					java.time.LocalDateTime localDateTime = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
					java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
					asta.setScadenza(localDateTime.format(formatter));
					asta.setRialzoMinimo(result.getInt("rialzominimo")); // throws IOException
					asta.setPrezzoIniziale(result.getFloat("prezzoiniziale"));
					asta.setStato(result.getBoolean("stato"));
					asta.setCreatore(result.getString("creatore"));
					asta.setAggiudicatario(result.getString("aggiudicatario"));
					asta.setOffertaMax(result.getDouble("max_quantita"));
					utente.setAddress(result.getString("indirizzo"));
					utente.setUsername(result.getString("username"));
					asteChiuse.put(asta, utente);
				}
			} catch (SQLException e) {

			}
		}

		return asteChiuse;
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

	public List<Asta> findAstaByWord(String parola) throws SQLException, IOException {
		List<Asta> aste = new ArrayList<Asta>();
		String query = "SELECT IFNULL(MAX(offerta.quantitaofferta), -1) AS max_quantita, asta.idasta, asta.scadenza, asta.rialzominimo, asta.stato, asta.prezzoiniziale, asta.creatore "
				+ "FROM asta " + "LEFT JOIN offerta ON asta.idasta = offerta.idasta "
				+ "JOIN articolo ON articolo.idasta = asta.idasta "
				+ "WHERE asta.stato = true AND (articolo.descrizione LIKE ? OR articolo.nome LIKE ?) "
				+ "GROUP BY asta.idasta, asta.scadenza, asta.rialzominimo, asta.prezzoiniziale, asta.creatore "
				+ "ORDER BY asta.scadenza ASC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			parola = "%" + parola + "%";
			pstatement.setString(1, parola);
			pstatement.setString(2, parola);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					// qui dentro ora ci entra
					Asta asta = new Asta();
					asta.setIdAsta(result.getInt("idasta"));
					java.sql.Timestamp timestamp = result.getTimestamp("scadenza");
					LocalDate scadenza = new Date(timestamp.getTime()).toLocalDate();
					java.util.Date utilDate = new java.util.Date(timestamp.getTime());
					java.time.LocalDateTime localDateTime = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
					java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
					asta.setScadenza(localDateTime.format(formatter));
					Instant i = scadenza.atStartOfDay(ZoneOffset.UTC).toInstant();
					Instant oggi = Instant.now();
					Duration tempoRimanente = Duration.between(oggi, i);
					long giorni = tempoRimanente.toDays();
					long ore = tempoRimanente.toHours() % 24;
					long minuti = tempoRimanente.toMinutes() % 60;
					long secondi = tempoRimanente.getSeconds() % 60;
					String tempo = giorni + " giorni, " + ore + " ore, " + minuti + " minuti, " + secondi + " secondi";
					asta.setTempoMancante(tempo);
					asta.setRialzoMinimo(result.getInt("rialzominimo")); // throws IOException
					asta.setPrezzoIniziale(result.getDouble("prezzoiniziale"));
					asta.setStato(result.getBoolean("stato"));
					asta.setCreatore(result.getString("creatore"));
					asta.setOffertaMax(result.getDouble("max_quantita"));
					aste.add(asta);
				}
			} catch (SQLException e) {

			}
			return aste;
		}
	}

	public List<Asta> getAsteAggiudicateByUser(String user) throws SQLException {
		List<Asta> aste = new ArrayList<Asta>();
		String query = "SELECT IFNULL(MAX(offerta.quantitaofferta), -1) AS max_quantita, asta.idasta, asta.scadenza, asta.rialzominimo, asta.stato, asta.prezzoiniziale, asta.creatore, asta.aggiudicatario "
				+ "FROM asta " + "LEFT JOIN offerta ON asta.idasta = offerta.idasta "
				+ "WHERE asta.stato = false AND asta.aggiudicatario = ? "
				+ "GROUP BY asta.idasta, asta.scadenza, asta.rialzominimo, asta.prezzoiniziale, asta.creatore "
				+ "ORDER BY asta.scadenza ASC ";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, user);
			try (ResultSet result = pstatement.executeQuery()) {
				while (result.next()) {
					Asta asta = new Asta();
					asta.setIdAsta(result.getInt("idasta"));
					java.sql.Timestamp timestamp = result.getTimestamp("scadenza");
					java.util.Date utilDate = new java.util.Date(timestamp.getTime());
					java.time.LocalDateTime localDateTime = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
					java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
					asta.setScadenza(localDateTime.format(formatter));
					asta.setRialzoMinimo(result.getInt("rialzominimo"));
					asta.setPrezzoIniziale(result.getDouble("prezzoiniziale"));
					asta.setStato(result.getBoolean("stato"));
					asta.setCreatore(result.getString("creatore"));
					asta.setOffertaMax(result.getDouble("max_quantita"));
					asta.setAggiudicatario(result.getString("aggiudicatario"));
					aste.add(asta);
				}
			} catch (SQLException e) {
			}
		}
		return aste;

	}
}
