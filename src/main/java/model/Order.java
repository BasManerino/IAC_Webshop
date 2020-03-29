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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.rest.core.annotation.RestResource;

@Entity
@Table(name = "order_table")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Date date;
	private double total_price;

	@ManyToOne
	@JoinColumn(name = "account_id", nullable = false)
	@RestResource(path = "orderAccount", rel = "account")
	private Account account;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "product_order", joinColumns = @JoinColumn(name = "order_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
	private List<Product> products;

	public Order(Date date, double total_price, Account account) {
		this.date = date;
		this.total_price = total_price;
		this.account = account;
	}

	public Order() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getTotal_price() {
		return total_price;
	}

	public void setTotal_price(double total_price) {
		this.total_price = total_price;
	}

	public Account giveAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public List<Product> giveProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
}