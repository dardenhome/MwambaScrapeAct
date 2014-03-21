package com.mwambachildrenschoir.act.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "donor")
public class DonorEntity {
	
	public DonorEntity() {};
	
	@Id
	@GeneratedValue
	private int id;

//	@OneToMany(mappedBy="donor", cascade = {CascadeType.ALL})
//	private List<DonationEntity> transactions;
	
	@Column(name="name")
	private String name;

	@Column(name="address1")
	private String address1 = "";
	
	@Column(name="address2")
	private String address2 = "";
	
	@Column(name="city")
	private String city = "";

	@Column(name="state")
	private String state = "";
	
	@Column(name="zip")
	private String zip = "";

	@Column(name="email")
	private String email = "";

	@Column(name="phone")
	private String phone = "";

	@Column(name="notes")
	private String notes = "";

	public String getPhone() {
		return phone;
	}

	public int getId() {
		return id;
	}


	public String getName() {
		return name;
	}

	public String getAddress1() {
		return address1;
	}

	public String getAddress2() {
		return address2;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getZip() {
		return zip;
	}

	public String getEmail() {
		return email;
	}

	public void setId(int id) {
		this.id = id;
	}

//	public List<DonationEntity> getTransactions() {
//		return transactions;
//	}
//	
//	public void setTransactions(List<DonationEntity> transactions) {
//		this.transactions = transactions;
//	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public String toString() {
		return "[id=" + id + ",name=" + name + ",address1=" + address1 + ",address2=" + address2 + 
				",city=" + city + ",state=" + state + ",zip=" + zip + ",phone=" + phone + ",email=" + email + ",notes=" + notes + "]"; 
	}
}
