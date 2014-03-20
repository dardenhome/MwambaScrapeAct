package com.mwambachildrenschoir.act.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class ActDao {	
	public void persistDonation(DonationEntity donation) {
		// make sure there is a donor that already exists for this donation
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		// see if the donation already exists..if so, this is an update
		DonationEntity searchDonation = (DonationEntity)session.createCriteria(DonationEntity.class )
				  											.add( Restrictions.eq("paymentNo", donation.getPaymentNo()))
				  											.uniqueResult();		
		

		if (searchDonation == null) {
			session.save(donation);
		} else {
			session.update(donation);
		}
		
		session.getTransaction().commit();
	}


	public void persistDonor(DonorEntity donor) {
		// make sure there is a donor that already exists for this donation
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		// see if the donation already exists..if so, this is an update
		DonorEntity searchDonor = (DonorEntity)session.createCriteria(DonorEntity.class )
													  .add( Restrictions.eq("name", donor.getName()))
													  .uniqueResult();		

		if (searchDonor == null) {
			session.save(donor);
		} else {
			session.update(donor);
		}
		
		session.getTransaction().commit();
	}
	
	@SuppressWarnings("unchecked")
	public List<DonationEntity> getAllTransactions() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		List<DonationEntity> actTransactions = session.createQuery("from DonationEntity").list();
		session.getTransaction().commit();
		return actTransactions;
	}
}
