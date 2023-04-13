package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.User;

public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}

	public User checkCredentials(String usrn, String pwd) throws SQLException {
		String query = "SELECT  id, username, name, surname FROM user  WHERE username = ? AND password =?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn); // al primo ? mette usrn
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {	// eseguo la query
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {	// ne aspetto solo uno, creao un bean per user
					result.next();
					User user = new User();
					user.setUsername(result.getString("username"));
					return user;
				}
			}
		}
	}
}
