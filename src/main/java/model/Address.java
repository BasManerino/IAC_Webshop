package model;

import java.util.*;

public class Address {

	public Address(Collection<Account> customers, int id, String street, String city, String state, String postalCode,
			String country, String number) {
		super();
		this.customers = customers;
		this.id = id;
		this.street = street;
		this.city = city;
		this.state = state;
		this.postalCode = postalCode;
		this.country = country;
		this.number = number;
	}

	Collection<Account> customers;
	private int id;
	private String street;
	private String city;
	private String state;
	private String postalCode;
	private String country;
	private String number;

	public boolean validate() {
		// TODO - implement Address.validate
		throw new UnsupportedOperationException();
	}

	public Collection<Account> getCustomers() {
		return customers;
	}

	public void setCustomers(Collection<Account> customers) {
		this.customers = customers;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

}