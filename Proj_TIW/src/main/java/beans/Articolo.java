package beans;

import java.awt.image.BufferedImage;

public class Articolo {
	private int code;
	private String name;
	private String description;
	private float price;
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

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
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

	public void setImage(BufferedImage image) {
		this.image = image;
	}
}
