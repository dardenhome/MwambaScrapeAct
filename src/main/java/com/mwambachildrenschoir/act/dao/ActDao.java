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
			searchDonation.setAmount(donation.getAmount());
			searchDonation.setDescription(donation.getDescription());
			searchDonation.setDonorId(donation.getDonorId());
			searchDonation.setPaymentDate(donation.getPaymentDate());
			searchDonation.setPaymentNo(donation.getPaymentNo());
			session.update(searchDonation);
		}
		
		session.getTransaction().commit();
	}


	public int persistDonor(DonorEntity donor) {
		// make sure there is a donor that already exists for this donation
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		// see if the donation already exists..if so, this is an update
		DonorEntity searchDonor = (DonorEntity)session.createCriteria(DonorEntity.class )
													  .add( Restrictions.eq("name", donor.getName()))
													  .uniqueResult();		

		int id;
		if (searchDonor == null) {
			session.save(donor);
			id = donor.getId();
		} else {
			searchDonor.setAddress1(donor.getAddress1());
			searchDonor.setAddress2(donor.getAddress2());
			searchDonor.setCity(donor.getCity());
			searchDonor.setState(donor.getState());
			searchDonor.setZip(donor.getZip());
			searchDonor.setPhone(donor.getPhone());
			searchDonor.setEmail(donor.getEmail());
			session.update(searchDonor);
			id = searchDonor.getId();
		}
		
		session.getTransaction().commit();
		return id;
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
