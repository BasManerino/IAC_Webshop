package model;

import java.util.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "product")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String description;
	private double price;
	private String imageId;

	@ManyToMany(mappedBy = "products", cascade = CascadeType.ALL)
	private List<Category> categories;
	
	@ManyToMany(mappedBy = "products", cascade = CascadeType.ALL)
	private List<Discount> discounts;
	
	@ManyToMany(mappedBy = "products", cascade = CascadeType.ALL)
	private List<Order> orders;

	public Product(String name, String description, double price, String imageId) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.imageId = imageId;
	}

	public Product() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public List<Order> giveOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public List<Discount> giveDiscounts() {
		return discounts;
	}

	public void setDiscounts(List<Discount> discounts) {
		this.discounts = discounts;
	}

	public List<Category> giveCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	
}