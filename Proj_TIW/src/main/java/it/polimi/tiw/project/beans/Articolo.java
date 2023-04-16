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
	private Double price;
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

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(Blob blob) throws IOException, SQLException {
		InputStream in = blob.getBinaryStream();  
		this.image = ImageIO.read(in);
	}
}
