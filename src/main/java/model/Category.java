package model;

import java.util.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "category")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String description;
	private String imageId;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "product_category", joinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
	private List<Product> products;

	public Category(String name, String description, String imageId, List<Product> products) {
		this.name = name;
		this.description = description;
		this.imageId = imageId;
		this.products = products;
	}
	
	public Category() {
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

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public List<Product> giveProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
}