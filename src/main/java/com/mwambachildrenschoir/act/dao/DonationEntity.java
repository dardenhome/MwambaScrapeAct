package com.mwambachildrenschoir.act.dao;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "donation")
public class DonationEntity {
	public DonationEntity() {}

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	@JoinColumn(name="donorId")
	private DonorEntity donor;
	
	@Column(name="paymentDate")
	private Date paymentDate;
	
	@Column(name="paymentNo")
	private int paymentNo;
	
	@Column(name="description")
	private String description;

	@Column(name="amount")
	private double amount;
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public DonorEntity getDonor() {
		return donor;
	}

	public void setDonar(DonorEntity donor) {
		this.donor = donor;
	}

	public DonorEntity getDonar() {
		return this.donor;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public int getPaymentNo() {
		return paymentNo;
	}

	public void setPaymentNo(int paymentNo) {
		this.paymentNo = paymentNo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
}
