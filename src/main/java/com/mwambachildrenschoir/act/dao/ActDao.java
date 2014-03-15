package com.mwambachildrenschoir.act.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class ActDao {
	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("ActTransactionEntity");

	public void persistTransactions(ActTransactionEntity actTransaction) {
		EntityManager em = createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		em.persist(actTransaction);

		tx.commit();
		em.close();
	}

	@SuppressWarnings("unchecked")
	public List<ActTransactionEntity> getAllTransactions() {
		EntityManager em = createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		Query allTransacationsQuery = em.createQuery("SELECT * FROM actTransacation");

		List<ActTransactionEntity> allTransactions = allTransacationsQuery.getResultList();

		tx.commit();
		em.close();

		return allTransactions;
	}


	public EntityManager createEntityManager() {
		return emf.createEntityManager();
	}

	public static void closeEntityManager() {
		emf.close();
	}

}
