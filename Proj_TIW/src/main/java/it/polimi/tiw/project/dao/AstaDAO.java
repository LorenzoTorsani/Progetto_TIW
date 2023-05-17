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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.IOException;

import it.polimi.tiw.project.beans.Asta;
import it.polimi.tiw.project.beans.Offerta;
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

	    String query2 = "SELECT MAX(progetto_tiw.asta.idasta) FROM progetto_tiw.asta";
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
	            id = resultSet.getInt("MAX(progetto_tiw.asta.idasta)");
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


	public List<Integer> chiudiAsta(int idAsta) throws SQLException {
	    connection.setAutoCommit(false);
	    
	    List<Integer> codici = new ArrayList<Integer>();
	    String updateQuery = "UPDATE progetto_tiw.asta " + "SET `stato` = '0', `aggiudicatario` = "
	            + "(SELECT offerente FROM progetto_tiw.offerta "
	            + "WHERE progetto_tiw.offerta.idasta = progetto_tiw.asta.idasta "
	            + "AND quantitaofferta = (SELECT MAX(quantitaofferta) FROM progetto_tiw.offerta "
	            + "WHERE progetto_tiw.offerta.idasta = progetto_tiw.asta.idasta)) WHERE (idasta = ?)";
	    
	    String selectQuery = "SELECT progetto_tiw.articolo.codice FROM progetto_tiw.asta JOIN progetto_tiw.articolo ON progetto_tiw.asta.idasta = progetto_tiw.articolo.idasta WHERE progetto_tiw.asta.idasta = ?";
	    
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
	    
	    return codici;
	}


	public Asta findAstaById(int idAsta) throws SQLException {
		Asta asta = new Asta();
		String query = "SELECT * FROM asta WHERE idasta = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idAsta);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					asta.setIdAsta(result.getInt("idasta"));
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

		String query = "SELECT IFNULL(MAX(progetto_tiw.offerta.quantitaofferta), -1) AS max_quantita, progetto_tiw.asta.idasta, progetto_tiw.asta.scadenza, progetto_tiw.asta.rialzominimo, progetto_tiw.asta.prezzoiniziale, progetto_tiw.asta.creatore "
				+ "FROM progetto_tiw.asta "
				+ "LEFT JOIN progetto_tiw.offerta ON progetto_tiw.asta.idasta = progetto_tiw.offerta.idasta "
				+ "WHERE progetto_tiw.asta.stato = true AND progetto_tiw.asta.creatore = ? "
				+ "GROUP BY progetto_tiw.asta.idasta, progetto_tiw.asta.scadenza, progetto_tiw.asta.rialzominimo, progetto_tiw.asta.prezzoiniziale, progetto_tiw.asta.creatore "
				+ "ORDER BY progetto_tiw.asta.scadenza ASC";

		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, user);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Asta asta = new Asta();
					asta.setIdAsta(result.getInt("idasta"));
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
		Map<Asta, User> asteChiuse= new HashMap<Asta, User>();

		String query = "SELECT * " + "FROM asta JOIN utente ON asta.aggiudicatario = utente.username "
				+ "WHERE stato = false AND creatore = ? " + "ORDER BY scadenza ASC";

		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, user);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Asta asta = new Asta();
					User utente = new User();
					asta.setIdAsta(result.getInt("idasta"));
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
		String query = "SELECT IFNULL(MAX(progetto_tiw.offerta.quantitaofferta), -1) AS max_quantita, progetto_tiw.asta.idasta, progetto_tiw.asta.scadenza, progetto_tiw.asta.rialzominimo, progetto_tiw.asta.stato, progetto_tiw.asta.prezzoiniziale, progetto_tiw.asta.creatore "
				+ "FROM progetto_tiw.asta "
				+ "LEFT JOIN progetto_tiw.offerta ON progetto_tiw.asta.idasta = progetto_tiw.offerta.idasta "
				+ "JOIN progetto_tiw.articolo ON progetto_tiw.articolo.idasta = progetto_tiw.asta.idasta "
				+ "WHERE progetto_tiw.asta.stato = true AND (progetto_tiw.articolo.descrizione LIKE ? OR progetto_tiw.articolo.nome LIKE ?) "
				+ "GROUP BY progetto_tiw.asta.idasta, progetto_tiw.asta.scadenza, progetto_tiw.asta.rialzominimo, progetto_tiw.asta.prezzoiniziale, progetto_tiw.asta.creatore "
				+ "ORDER BY progetto_tiw.asta.scadenza ASC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			parola = "%" + parola + "%";
			pstatement.setString(1, parola);
			pstatement.setString(2, parola);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) { 
					// qui dentro ora ci entra
					Asta asta = new Asta();
					asta.setIdAsta(result.getInt("idasta"));
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
	
	public List<Asta> getAsteAggiudicateByUser(String user) throws SQLException{
		List<Asta> aste = new ArrayList<Asta>();
		String query = "SELECT IFNULL(MAX(progetto_tiw.offerta.quantitaofferta), -1) AS max_quantita, progetto_tiw.asta.idasta, progetto_tiw.asta.scadenza, progetto_tiw.asta.rialzominimo, progetto_tiw.asta.stato, progetto_tiw.asta.prezzoiniziale, progetto_tiw.asta.creatore, asta.aggiudicatario "
				+ "FROM progetto_tiw.asta "
				+ "LEFT JOIN progetto_tiw.offerta ON progetto_tiw.asta.idasta = progetto_tiw.offerta.idasta "
				+ "WHERE progetto_tiw.asta.stato = false AND asta.aggiudicatario = ? "
				+ "GROUP BY progetto_tiw.asta.idasta, progetto_tiw.asta.scadenza, progetto_tiw.asta.rialzominimo, progetto_tiw.asta.prezzoiniziale, progetto_tiw.asta.creatore "
				+ "ORDER BY progetto_tiw.asta.scadenza ASC ";
		try(PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, user);
			try (ResultSet result = pstatement.executeQuery()){
				while (result.next()) {
					Asta asta = new Asta();
					asta.setIdAsta(result.getInt("idasta"));
					asta.setScadenza(result.getDate("scadenza"));
					asta.setRialzoMinimo(result.getInt("rialzominimo"));
					asta.setPrezzoIniziale(result.getDouble("prezzoiniziale"));
					asta.setStato(result.getBoolean("stato"));
					asta.setCreatore(result.getString("creatore"));
					asta.setOffertaMax(result.getDouble("max_quantita"));
					asta.setAggiudicatario(result.getString("aggiudicatario"));
					aste.add(asta);
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return aste;
		
	}
}
