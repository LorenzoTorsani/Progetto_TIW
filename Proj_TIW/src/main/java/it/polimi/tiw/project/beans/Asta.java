package it.polimi.tiw.project.beans;

import java.sql.Date;

public class Asta {
	private int idAsta;
	private Date scadenza;
	private int rialzoMinimo;
	private int prezzoIniziale;
	private boolean stato;
	
	public void setId(int idAsta) {
		this.idAsta = idAsta;
	}
	
	public void setScadenza(Date scadenza) {
		this.scadenza = scadenza;
	}
	
	public void setRialzoMinimo(int rialzoMinimo) {
		this.rialzoMinimo = rialzoMinimo;
	}
	
	public void setPrezzoIniziale(int prezzoIniziale) {
		this.prezzoIniziale = prezzoIniziale;
	}
	
	public void setStato(boolean stato) {
		this.stato = stato;
	}
	
	public int getIdAsta() {
		return this.idAsta;
	}
	
	public Date getScadenza() {
		return this.scadenza;
	}
	
	public int getRialzoMinimo() {
		return this.rialzoMinimo;
	}
	
	public int getPrezzoIniziale() {
		return this.prezzoIniziale;
	}
	
	public boolean getStato() {
		return this.stato;
	}
}
