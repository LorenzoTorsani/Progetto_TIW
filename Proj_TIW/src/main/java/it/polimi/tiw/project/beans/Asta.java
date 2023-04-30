package it.polimi.tiw.project.beans;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Asta {
	private int idAsta;
	private Date scadenza; //TODO da cambiare in datetime
	private int rialzoMinimo;
	private double prezzoIniziale;
	private boolean stato;
	private String creatore;
	private String aggiudicatario;
	private double offertaMax;
	private List<Articolo> articoli = new ArrayList<Articolo>();
	
	public void setId(int idAsta) {
		this.idAsta = idAsta;
	}
	
	public void setScadenza(Date date) {
		this.scadenza = date;
	}
	
	public void setRialzoMinimo(int rialzoMinimo) {
		this.rialzoMinimo = rialzoMinimo;
	}

	public void setPrezzoIniziale(double prezzoIniziale) {
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
	
	public double getPrezzoIniziale() {
		return this.prezzoIniziale;
	}
	
	public boolean getStato() {
		return this.stato;
	}

	public String getCreatore() {
		return creatore;
	}

	public void setCreatore(String creatore) {
		this.creatore = creatore;
	}

	public String getAggiudicatario() {
		return aggiudicatario;
	}

	public void setAggiudicatario(String aggiudicatario) {
		this.aggiudicatario = aggiudicatario;
	}

	public double getOffertaMax() {
		return offertaMax;
	}

	public void setOffertaMax(double offertaMax) {
		this.offertaMax = offertaMax;
	}

	public List<Articolo> getArticoli() {
		return articoli;
	}

	public void addArticolo(Articolo articolo) {
		this.articoli.add(articolo);
	}

	

}
