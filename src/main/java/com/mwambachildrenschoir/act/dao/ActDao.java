package com.mwambachildrenschoir.act.dao;

import java.util.List;

import org.hibernate.Session;

public class ActDao {	
	public void persistTransactions(ActTransactionEntity actTransaction) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		session.persist(actTransaction);
		session.getTransaction().commit();
	}

	@SuppressWarnings("unchecked")
	public List<ActTransactionEntity> getAllTransactions() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		List<ActTransactionEntity> actTransactions = session.createQuery("from ActTransactionEntity").list();
		session.getTransaction().commit();
		return actTransactions;
	}
}
