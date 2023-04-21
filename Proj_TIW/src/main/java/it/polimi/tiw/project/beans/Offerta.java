package it.polimi.tiw.project.beans;

public class Offerta {
	private String offerente;
	private int idAsta;
	private double offerta;
	
	public void setOfferente(String offerente) {
		this.offerente = offerente;
	}
	
	
	public void setIdAsta(int idAsta) {
		this.idAsta = idAsta;
	}
	
	public void setOfferta(double offerta) {
		this.offerta = offerta;
	}
	
	public String getOfferente() {
		return this.offerente;
	}
	
	public int getIdAsta() {
		return this.idAsta;
	}
	
	public double getOfferta() {
		return this.offerta;
	}
}
