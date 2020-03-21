package model;

import java.util.*;

public class Discount {

	Collection<Product> products;
	private int id;
	private int product;
	private float price;
	private Date from;
	private Date until;
	private String adText;
	
	public Discount(Collection<Product> products, int id, int product, float price, Date from, Date until,
			String adText) {
		super();
		this.products = products;
		this.id = id;
		this.product = product;
		this.price = price;
		this.from = from;
		this.until = until;
		this.adText = adText;
	}
	
	public Collection<Product> getProducts() {
		return products;
	}
	public void setProducts(Collection<Product> products) {
		this.products = products;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProduct() {
		return product;
	}
	public void setProduct(int product) {
		this.product = product;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	public Date getUntil() {
		return until;
	}
	public void setUntil(Date until) {
		this.until = until;
	}
	public String getAdText() {
		return adText;
	}
	public void setAdText(String adText) {
		this.adText = adText;
	}

}