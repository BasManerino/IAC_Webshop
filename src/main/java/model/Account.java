package model;

import java.util.*;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.springframework.data.rest.core.annotation.RestResource;

@Entity
@Table(name = "account")
public class Account{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Date created_on;
	private String name;
	private String phone;
	private String email;

	@ManyToOne
	@JoinColumn(name = "address_id")
	@RestResource(path = "accountAddress", rel = "address")
	private Address address;

	@ManyToOne
	@JoinColumn(name = "role_id")
	@RestResource(path = "accountRole", rel = "role")
	private Role role;

	@OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
	private List<Order> orders;

	public Account(Date createdOn, String name, String phone, String email, Address address, Role role) {
		this.created_on = createdOn;
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.address = address;
		this.role = role;
	}

	public Account() {
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

	public Date getCreated_on() {
		return created_on;
	}

	public void setCreated_on(Date created_on) {
		this.created_on = created_on;
	}

	public Address giveAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Role giveRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public List<Order> giveOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

}