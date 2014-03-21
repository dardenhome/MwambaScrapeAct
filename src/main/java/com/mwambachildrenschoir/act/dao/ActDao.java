package com.mwambachildrenschoir.act.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author jim
 *
 */
public class ActDao {
	final static Logger logger = LoggerFactory.getLogger(ActDao.class);
	
	/**
	 * 
	 * @param donation
	 */
	public void persistDonation(DonationEntity donation) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			// make sure there is a donor that already exists for this donation
			session.beginTransaction();
			// see if the donation already exists..if so, this is an update
			DonationEntity searchDonation = (DonationEntity)session.createCriteria(DonationEntity.class )
					  											.add( Restrictions.eq("paymentNo", donation.getPaymentNo()))
					  											.uniqueResult();		
			
	
			if (searchDonation == null) {
				session.save(donation);
			} else {
				logger.warn("donation already exists in database...skipping");
				if (session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				return;
//				searchDonation.setAmount(donation.getAmount());
//				searchDonation.setDescription(donation.getDescription());
//				searchDonation.setDonorId(donation.getDonorId());
//				searchDonation.setPaymentDate(donation.getPaymentDate());
//				searchDonation.setPaymentNo(donation.getPaymentNo());
//				session.update(searchDonation);
			}
			
			session.getTransaction().commit();
		} catch (Exception e) {
			logger.error("error while persisting donor" + donation.toString(), e);
			if (session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
		}
	}


	public int persistDonor(DonorEntity donor) {
		// make sure there is a donor that already exists for this donation
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			// see if the donation already exists..if so, this is an update
			DonorEntity searchDonor = (DonorEntity)session
					.createCriteria(DonorEntity.class )
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
				searchDonor.setNotes(donor.getNotes());
				session.update(searchDonor);
				id = searchDonor.getId();
			}
			
			session.getTransaction().commit();
			
			return id;
		} catch (Exception e) {
			logger.error("error while persisting donor" + donor.toString(), e);
			if (session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			return 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<DonationEntity> getAllDonations() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		List<DonationEntity> donations = session.createQuery("from DonationEntity").list();
		session.getTransaction().commit();
		return donations;
	}
	
	@SuppressWarnings("unchecked")
	public List<DonorEntity> getAllDonors() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		List<DonorEntity> donors = session.createQuery("from DonorEntity").list();
		session.getTransaction().commit();
		return donors;
	}
	
}
