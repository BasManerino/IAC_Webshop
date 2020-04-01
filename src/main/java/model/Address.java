package model;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "address") // Deze klasse wordt als entity address in database gemaakt
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // Autogenerated attribute id
	private String street;
	private String house_number;
	private String city;
	private String state;
	private String postal_code;
	private String country;

	@OneToMany(mappedBy = "address", fetch = FetchType.EAGER)
	private List<Account> accounts; // One_to_many relatie met address, address_id is de FK in address

	public Address(String street, String house_number, String city, String state, String postal_code, String country) {
		this.street = street;
		this.house_number = house_number;
		this.city = city;
		this.state = state;
		this.postal_code = postal_code;
		this.country = country;
	}
	
	//Deze constructor is verplicht om informatie uit te database te halen
	public Address() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public String getPostal_code() {
		return postal_code;
	}

	public void setPostal_code(String postalCode) {
		this.postal_code = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getHouse_number() {
		return house_number;
	}

	public void setHouse_number(String houseNumber) {
		this.house_number = houseNumber;
	}

	//Er wordt give i.p.v get gebruikt om geen data van accounts direct te weergeven
	public List<Account> giveAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
	
	

}