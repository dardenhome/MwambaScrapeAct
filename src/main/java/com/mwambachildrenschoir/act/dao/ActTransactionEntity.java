package com.mwambachildrenschoir.act.dao;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "actTransacation")
public class ActTransactionEntity {
	@Id
	@GeneratedValue
	private Integer id;

	@Column(name="donarName")
	private Date donarName;

	@Column(name="paymentDate")
	private Date paymentDate;
	
	@Column(name="paymentNo")
	private int paymentNo;
	
	@Column(name="description")
	private String description;

	@Column(name="amount")
	private double amount;
	
	public ActTransactionEntity() {}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDonarName() {
		return donarName;
	}

	public void setDonarName(Date donarName) {
		this.donarName = donarName;
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
