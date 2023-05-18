package it.polimi.tiw.project.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.project.beans.Offerta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OffertaDAO {
	private Connection connection;

	public OffertaDAO(Connection connection) {
		this.connection = connection;
	}

	public void createOfferta(String user, int idAsta, Double offerta) throws SQLException {
		String query = "INSERT into offerta (offerente, idasta, quantitaofferta) VALUES (?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, user);
			pstatement.setInt(2, idAsta);
			pstatement.setDouble(3, offerta);
			// ora e data inserite automaticamente
			pstatement.executeUpdate();
		}
	}

	public List<Offerta> findOfferte(int idAsta) throws SQLException {
		List<Offerta> offerte = new ArrayList<Offerta>();

		String query = "SELECT offerta.offerente, offerta.idasta, offerta.quantitaofferta, offerta.oraofferta "
				+ "FROM asta JOIN offerta ON "
				+ "offerta.idasta = asta.idasta " + "WHERE asta.idasta = ? "
				+ "ORDER BY oraofferta DESC";

		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idAsta);
			try (ResultSet result = pstatement.executeQuery()) {
				while (result.next()) {
					Offerta offerta = new Offerta();
					offerta.setOfferente(result.getString("offerente"));
					offerta.setIdAsta(result.getInt("idasta"));
					offerta.setOfferta(result.getDouble("quantitaofferta"));
					java.sql.Timestamp timestamp = result.getTimestamp("oraofferta");
					java.util.Date utilDate = new java.util.Date(timestamp.getTime());
					java.time.LocalDateTime localDateTime = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
					java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
					offerta.setData(localDateTime.format(formatter));

					offerte.add(offerta);
				}
			} catch (SQLException e) {
			}
		}

		return offerte;
	}

	public Double getOffertaMaxByAstaid(int idAsta) throws SQLException {
		Double maxOfferta = 0.0;
		String query = "SELECT MAX(offerta.quantitaofferta) AS maxOfferta "
				+ "FROM offerta "
				+ "WHERE offerta.idasta = ? "
				+ "GROUP BY offerta.idasta";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idAsta);
			try (ResultSet result = pstatement.executeQuery()) {
				while (result.next()) {
					maxOfferta = result.getDouble("maxOfferta");

				}
			} catch (SQLException e) {
			}
		}
		return maxOfferta;
	}
}
