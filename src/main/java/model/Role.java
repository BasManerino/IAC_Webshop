package model;

import java.util.*;

public class Role {

	public Role(Collection<Account> account, int id, String name, String description) {
		super();
		this.account = account;
		this.id = id;
		this.name = name;
		this.description = description;
	}
	
	Collection<Account> account;
	private int id;
	private String name;
	private String description;
	
	public Collection<Account> getAccount() {
		return account;
	}
	public void setAccount(Collection<Account> account) {
		this.account = account;
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

}