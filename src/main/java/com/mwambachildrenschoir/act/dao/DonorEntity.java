package com.mwambachildrenschoir.act.dao;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "transaction")
public class DonorEntity {
	
	public DonorEntity() {};
	
	@Id
	@GeneratedValue
	private Integer id;

	@OneToMany(mappedBy="camera")
	private List<TransactionEntity> transactions;
	
	@Column(name="name")
	private String name;

	@Column(name="address1")
	private String address1;
	
	@Column(name="address2")
	private String address2;
	
	@Column(name="city")
	private String city;

	@Column(name="state")
	private String state;
	
	@Column(name="zip")
	private String zip;

	@Column(name="email")
	private String email;

	@Column(name="phone")
	private String phone;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}
