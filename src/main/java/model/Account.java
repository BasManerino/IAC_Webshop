package model;

import java.util.*;

public class Account {

	public Account(Address address, Role role, Collection<Order> orders, int id, Date createdOn, String name,
			String phone, String email) {
		super();
		this.address = address;
		this.role = role;
		this.orders = orders;
		this.id = id;
		this.createdOn = createdOn;
		this.name = name;
		this.phone = phone;
		this.email = email;
	}

	Address address;
	Role role;
	Collection<Order> orders;
	
	private int id;
	private Date createdOn;
	private String name;
	private String phone;
	private String email;

	public boolean isActive() {
		// TODO - implement Account.isActive
		throw new UnsupportedOperationException();
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Collection<Order> getOrders() {
		return orders;
	}

	public void setOrders(Collection<Order> orders) {
		this.orders = orders;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}