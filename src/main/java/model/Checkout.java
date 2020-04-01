package model;

import java.util.Date;
import java.util.List;

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
@Table(name = "checkout")
public class Checkout {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String pay_method;
	private Long offer_code;
	private Date pay_date;

	@ManyToOne
	@JoinColumn(name = "account_id", nullable = false)
	@RestResource(path = "checkoutAccount", rel = "account")
	private Account account;

	@ManyToMany
	@JoinTable(name = "checkout_product", joinColumns = @JoinColumn(name = "checkout_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
	private List<Product> products;

	public Checkout(String pay_method, Long offer_code, Date pay_date) {
		this.pay_method = pay_method;
		this.offer_code = offer_code;
		this.pay_date = pay_date;
	}

	public Checkout() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPay_method() {
		return pay_method;
	}

	public void setPay_method(String pay_method) {
		this.pay_method = pay_method;
	}

	public Long getOffer_code() {
		return offer_code;
	}

	public void setOffer_code(Long offer_code) {
		this.offer_code = offer_code;
	}

	public Date getPay_date() {
		return pay_date;
	}

	public void setPay_date(Date pay_date) {
		this.pay_date = pay_date;
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
