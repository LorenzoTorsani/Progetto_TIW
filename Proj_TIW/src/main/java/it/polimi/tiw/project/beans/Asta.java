package it.polimi.tiw.project.beans;

import java.sql.Date;

public class Asta {
	private int idAsta;
	private Date scadenza; //TODO da cambiare in datetime
	private float rialzoMinimo;
	private float prezzoIniziale;
	private boolean stato;
	
	public void setId(int idAsta) {
		this.idAsta = idAsta;
	}
	
	public void setScadenza(Date scadenza) {
		this.scadenza = scadenza;
	}
	

	public void setRialzoMinimo(float rialzoMinimo) {
		this.rialzoMinimo = rialzoMinimo;
	}
	


	public void setPrezzoIniziale(float prezzoIniziale) {
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

	public float getRialzoMinimo() {
		return this.rialzoMinimo;
	}
	

	public float getPrezzoIniziale() {
		return this.prezzoIniziale;
	}
	
	public boolean getStato() {
		return this.stato;
	}
}
