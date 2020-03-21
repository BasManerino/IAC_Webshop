package model;

import java.util.*;

public class Product {

	public Product(Collection<Order> orders, Collection<Discount> discounts, Collection<Category> category, int id,
			String name, String description, float price, String imageId, int categorieId) {
		super();
		this.orders = orders;
		this.discounts = discounts;
		this.category = category;
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.imageId = imageId;
		this.categorieId = categorieId;
	}
	
	Collection<Order> orders;
	Collection<Discount> discounts;
	Collection<Category> category;
	private int id;
	private String name;
	private String description;
	private float price;
	private String imageId;
	private int categorieId;
	
	public Collection<Order> getOrders() {
		return orders;
	}
	public void setOrders(Collection<Order> orders) {
		this.orders = orders;
	}
	public Collection<Discount> getDiscounts() {
		return discounts;
	}
	public void setDiscounts(Collection<Discount> discounts) {
		this.discounts = discounts;
	}
	public Collection<Category> getCategory() {
		return category;
	}
	public void setCategory(Collection<Category> category) {
		this.category = category;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
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
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public int getCategorieId() {
		return categorieId;
	}
	public void setCategorieId(int categorieId) {
		this.categorieId = categorieId;
	}

}