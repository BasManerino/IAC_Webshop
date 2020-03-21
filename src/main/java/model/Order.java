package model;

import java.util.*;

public class Order {

	public Order(Account customer, Collection<Product> products, int id, Date date, float totalPrice) {
		super();
		this.customer = customer;
		this.products = products;
		this.id = id;
		this.date = date;
		this.totalPrice = totalPrice;
	}

	Account customer;
	Collection<Product> products;
	private int id;
	private Date date;
	private float totalPrice;

	public Order getCurrentOrderValue() {
		// TODO - implement Order.getCurrentOrderValue
		throw new UnsupportedOperationException();
	}

	public Account getCustomer() {
		return customer;
	}

	public void setCustomer(Account customer) {
		this.customer = customer;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public float getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}

}