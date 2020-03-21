package model;

import java.util.*;

public class Category {

	public Category(Collection<Product> product, int id, String name, String description, String imageId,
			int productId) {
		super();
		this.product = product;
		this.id = id;
		this.name = name;
		this.description = description;
		this.imageId = imageId;
		this.productId = productId;
	}
	
	Collection<Product> product;
	private int id;
	private String name;
	private String description;
	private String imageId;
	private int productId;
	
	public Collection<Product> getProduct() {
		return product;
	}
	public void setProduct(Collection<Product> product) {
		this.product = product;
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
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}

}