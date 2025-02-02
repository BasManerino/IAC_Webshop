package model;

import java.util.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "discount") // Deze klasse wordt als entity discount in database gemaakt
public class Discount {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // Autogenerated attribute id
	private int percentage;
	private Date from_date;
	private Date until_date;
	private String adText;

	@ManyToMany
	@JoinTable(name = "discount_product", joinColumns = @JoinColumn(name = "discount_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
	private List<Product> products; //Many_to_many relatie met product, de koppeltable is discount_product

	public Discount(int percentage, Date from, Date until, String adText) {
		this.percentage = percentage;
		this.from_date = from;
		this.until_date = until;
		this.adText = adText;
	}

	//Deze constructor is verplicht om informatie uit te database te halen
	public Discount() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
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

	//Er wordt give i.p.v get gebruikt om geen data van products direct te weergeven
	public List<Product> giveProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
}