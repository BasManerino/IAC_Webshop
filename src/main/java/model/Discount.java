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
@Table(name = "discount")
public class Discount {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private double price;
	private Date from_date;
	private Date until_date;
	private String adText;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "product_discount", joinColumns = @JoinColumn(name = "discount_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
	private List<Product> products;

	public Discount(double price, Date from, Date until, String adText) {
		this.price = price;
		this.from_date = from;
		this.until_date = until;
		this.adText = adText;
	}

	public Discount() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getAdText() {
		return adText;
	}

	public void setAdText(String adText) {
		this.adText = adText;
	}

	public Date getFrom_date() {
		return from_date;
	}

	public void setFrom_date(Date from_date) {
		this.from_date = from_date;
	}

	public Date getUntil_date() {
		return until_date;
	}

	public void setUntil_date(Date until_date) {
		this.until_date = until_date;
	}

	public List<Product> giveProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
}