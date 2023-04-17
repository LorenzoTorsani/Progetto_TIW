package it.polimi.tiw.project.beans;

public class Offerta {
	private String username;
	private int idAsta;
	private int offerta;
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	public void setIdAsta(int idAsta) {
		this.idAsta = idAsta;
	}
	
	public void setOfferta(int offerta) {
		this.offerta = offerta;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public int getIdAsta() {
		return this.idAsta;
	}
	
	public int getOfferta() {
		return this.offerta;
	}
}
