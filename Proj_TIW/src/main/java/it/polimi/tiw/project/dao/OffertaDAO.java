package it.polimi.tiw.project.dao;

import java.sql.Connection;
import java.sql.SQLException;
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
	
	public void createOfferta(String user, int idAsta, Float offerta) throws SQLException {
		String query = "INSERT into offerta (username, idasta, offerta) VALUES (?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);){
			pstatement.setString(1, user);
			pstatement.setInt(2, idAsta);
			pstatement.setFloat(3, offerta);
		}
	}
	
	public List<Offerta> findOfferte(int idAsta) throws SQLException {
		List<Offerta> offerte = new ArrayList<Offerta>();
		
		String query = "SELECT offerente, idasta, quantitaofferta FROM asta JOIN offerta ON "
				+ "offerta.idasta = asta.idasta "
				+ "WHERE idasta = ?";
		
		try(PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idAsta);
			try (ResultSet result = pstatement.executeQuery()){
				while (result.next()) {
					Offerta offerta = new Offerta();
					offerta.setOfferente(result.getString("offerente"));
					offerta.setIdAsta(result.getInt("idasta"));
					offerta.setOfferta(result.getDouble("quantitaofferta"));
					offerte.add(offerta);
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		
		return offerte;
	}


}
