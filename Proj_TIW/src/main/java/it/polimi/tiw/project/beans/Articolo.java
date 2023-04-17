package it.polimi.tiw.project.beans;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

import javax.imageio.ImageIO;

public class Articolo {
	private int code;
	private String name;
	private String description;
	private Float price;
	private boolean sold;
	private BufferedImage image;
	
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

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public boolean isSold() {
		return sold;
	}

	public void setSold(boolean sold) {
		this.sold = sold;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(Blob blob) {
	    try {
	        InputStream in = blob.getBinaryStream();  
	        this.image = ImageIO.read(in);
	    } catch (SQLException e) {
	        System.err.println("Errore SQL durante la lettura del BLOB: " + e.getMessage());
	        e.printStackTrace();
	    } catch (IOException e) {
	        System.err.println("Errore di IO durante la lettura dell'immagine: " + e.getMessage());
	        e.printStackTrace();
	    } catch (NullPointerException e) {
	        System.err.println("Il BLOB è NULL: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

}
