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

}
