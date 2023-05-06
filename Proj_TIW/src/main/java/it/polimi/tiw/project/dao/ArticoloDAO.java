package it.polimi.tiw.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.project.beans.Articolo;

import java.io.IOException;


public class ArticoloDAO {
	private Connection connection;

	public ArticoloDAO(Connection connection) {
		this.connection = connection;
	}
	
	public Double getPrezzoIniziale(int[] codici) throws SQLException{
		Double prezzoIniziale = 0.0;
		String query = "SELECT sum(articolo.prezzo) "
				+ "FROM articolo " 
				+ "WHERE ";
		for(int i = 0; i < codici.length; i++) {
			if(i != codici.length - 1) {
			query = query + "articolo.codice = ? OR ";
			}else {
				query = query + "articolo.codice = ?";
			}
		}
		try(PreparedStatement pstatement = connection.prepareStatement(query);) {
			for(int i = 1; i <= codici.length; i++) {
				pstatement.setInt(i, codici[i - 1]);
			}
			try(ResultSet result = pstatement.executeQuery();){
				if(result.next()) {
					prezzoIniziale = result.getDouble("sum(articolo.prezzo)");
				}
			}
		}
		return prezzoIniziale;
	}

	public List<Articolo> getArticoliByUser(String user) throws SQLException, IOException {
		List<Articolo> articoli = new ArrayList<Articolo>();
		
		String query = "SELECT * "
				+ "FROM articolo "
				+ "WHERE articolo.proprietario = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, user);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Articolo articolo = new Articolo();
					articolo.setCode(result.getInt("codice"));
					articolo.setDescription(result.getString("descrizione"));
					articolo.setImage(result.getString("immagine"));	// throws IOException
					articolo.setName(result.getString("nome"));
					articolo.setPrice(result.getDouble("prezzo"));
					articolo.setSold(result.getBoolean("venduto"));
					articolo.setProprietario(result.getString("proprietario"));
					articolo.setIdasta(result.getInt("idasta"));
					articoli.add(articolo);
				}
			} catch (SQLException e) {
				
			}
		}
		
		return articoli;
	}
	
	public void setVendutiByCodice(int codice) throws SQLException{
		String query = "UPDATE progetto_tiw.articolo SET progetto_tiw.articolo.venduto = 1 WHERE progetto_tiw.articolo.codice = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setInt(1, codice);
			pstatement.executeUpdate();
		}
	}
	
	public void createArticolo(String description, String name, Double price, String image, boolean sold, String user) throws SQLException, IOException {
		String query = "INSERT into articolo (descrizione, nome, prezzo, immagine, venduto, proprietario) VALUES(?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setString(1, description);
			//pstatement.setBlob(2, imageBlob);
			pstatement.setString(2, name);
			pstatement.setDouble(3, price);
			pstatement.setString(4, image);
			pstatement.setBoolean(5, sold);
			pstatement.setString(6, user);
			pstatement.executeUpdate();
		}
	}
	
	public Integer getIdByCodice(int codice) throws SQLException{
		String query = "SELECT progetto_tiw.articolo.idasta FROM progetto_tiw.articolo WHERE progetto_tiw.articolo.codice = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setInt(1, codice);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					int id = result.getInt("idasta");
					if (result.wasNull()) {
						return null;
					}
					else
					{
						return id;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return 0;
		}
	}
	
	public void updateArticolo(int codice, int idAsta) throws SQLException {
		String query = "UPDATE progetto_tiw.articolo SET progetto_tiw.articolo.idasta = ? WHERE progetto_tiw.articolo.codice = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setInt(1, idAsta);
			pstatement.setInt(2, codice);
			pstatement.executeUpdate();
		}
	}
	
	public List<Articolo> getArticoliByAsta(int idAsta) throws SQLException {
		List<Articolo> articoli = new ArrayList<Articolo>();
		String query = "SELECT articolo.codice, articolo.nome, articolo.descrizione, articolo.prezzo, articolo.image "
				+ "FROM articolo JOIN asta ON articolo.idasta = asta.idasta "
				+ "WHERE idasta = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setInt(1, idAsta);
			try (ResultSet result = pstatement.executeQuery()){
				while(result.next()) {
					Articolo articolo = new Articolo();
					articolo.setCode(result.getInt("codice"));
					articolo.setName(result.getString("nome"));
					articolo.setDescription(result.getString("descrizione"));
					articolo.setPrice(result.getDouble("prezzo"));
					articolo.setImage(result.getString("immagine"));
					articoli.add(articolo);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return articoli;
	}
}
