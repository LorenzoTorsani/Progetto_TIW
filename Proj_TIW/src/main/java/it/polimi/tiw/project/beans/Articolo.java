package it.polimi.tiw.project.beans;


public class Articolo {
	private int code;
	private String name;
	private String description;
	private Double price;
	private boolean sold;
	private String proprietario;
	private String image;
	private Integer idasta = null;
	
	public int getCode() {
		return this.code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public boolean isSold() {
		return sold;
	}

	public void setSold(boolean sold) {
		this.sold = sold;
	}

	public String getProprietario() {
		return proprietario;
	}

	public void setProprietario(String proprietario) {
		this.proprietario = proprietario;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getIdasta() {
		return idasta;
	}

	public void setIdasta(int idasta) {
		this.idasta = idasta;
	}
}
