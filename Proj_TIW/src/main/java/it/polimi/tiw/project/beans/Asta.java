package it.polimi.tiw.project.beans;

import java.sql.Date;

public class Asta {
	private int idAsta;
<<<<<<< HEAD
	private Date scadenza;
	private Float rialzoMinimo;
	private Float prezzoIniziale;
=======
	private Date scadenza; //TODO da cambiare in datetime
	private float rialzoMinimo;
	private float prezzoIniziale;
>>>>>>> branch 'main' of https://github.com/LorenzoTorsani/Progetto_TIW.git
	private boolean stato;
	
	public void setId(int idAsta) {
		this.idAsta = idAsta;
	}
	
	public void setScadenza(Date scadenza) {
		this.scadenza = scadenza;
	}
	
<<<<<<< HEAD
	public void setRialzoMinimo(Float rialzoMinimo) {
=======
	public void setRialzoMinimo(float rialzoMinimo) {
>>>>>>> branch 'main' of https://github.com/LorenzoTorsani/Progetto_TIW.git
		this.rialzoMinimo = rialzoMinimo;
	}
	
<<<<<<< HEAD
	public void setPrezzoIniziale(Float prezzoIniziale) {
=======
	public void setPrezzoIniziale(float prezzoIniziale) {
>>>>>>> branch 'main' of https://github.com/LorenzoTorsani/Progetto_TIW.git
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
	
<<<<<<< HEAD
	public Float getRialzoMinimo() {
=======
	public float getRialzoMinimo() {
>>>>>>> branch 'main' of https://github.com/LorenzoTorsani/Progetto_TIW.git
		return this.rialzoMinimo;
	}
	
<<<<<<< HEAD
	public Float getPrezzoIniziale() {
=======
	public float getPrezzoIniziale() {
>>>>>>> branch 'main' of https://github.com/LorenzoTorsani/Progetto_TIW.git
		return this.prezzoIniziale;
	}
	
	public boolean getStato() {
		return this.stato;
	}
}
