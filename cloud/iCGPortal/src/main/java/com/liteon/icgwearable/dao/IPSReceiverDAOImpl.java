package com.liteon.icgwearable.dao;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.liteon.icgwearable.hibernate.entity.IPSReceiver;
import com.liteon.icgwearable.hibernate.entity.IPSReceiverDevice;
import com.liteon.icgwearable.hibernate.entity.SchoolDetails;
import com.liteon.icgwearable.util.CommonUtil;

@Repository("iPSReceiverDAO")
@Transactional
public class IPSReceiverDAOImpl implements IPSReceiverDAO {
	 private static Logger log = Logger.getLogger(IPSReceiverDAOImpl.class);
	 
	 
	 @Autowired
	 protected SessionFactory sessionFactory;
	 
	 @Value("${IPS_SESSION_EXPIRY_TIME}")
	 private String IPS_SESSION_EXPIRY_TIME;
	 
	 
	 public void setSessionFactory(SessionFactory sessionFactory) {
	  this.sessionFactory = sessionFactory;
	 }


	@Override
	public boolean isIPSReceiverForSchooIDExist(int schoolId) {
		 boolean flag = false;
		  Session session = null;		 
		  try{
		  session = sessionFactory.openSession();		 
		   
		  String sessionQuery = "SELECT ips_receiver_id FROM ips_receiver as ips, accounts as acc WHERE ips.school_id = acc.account_id AND school_id = :schoolId";
		  Query query = session.createSQLQuery(sessionQuery);
		  query.setParameter("schoolId", schoolId);
		  
		  log.info("query string"+ sessionQuery );
		  
		  Integer receiverId = (Integer)query.uniqueResult();
		 
		  //if schoolid present in ipslocators table then locatorId value is not null.
		  //if school with  schoolid is not created then locatorId value is not null.
		  if(receiverId!=null && receiverId >0){
		    log.info("already schoold id associated with IPSReceiver::"+ receiverId.intValue());
		    flag= true;
		   }  
		  
		  }catch(Exception e){
		   log.debug("Exception inside IPSReceiverDAOImpl : isIPSReceiverForSchooIDExist " + e);		   
		  }finally{
		   session.close();
		  }
		  log.info("schoold id exist flag ="+flag);
		  return flag;
	}


	@Override
	public boolean createIPSReceiverEntry(IPSReceiver ips) {
		 boolean insertflag = false;
		  Session session = null;
		  Transaction tx=null;		 
		  try {
		   session = sessionFactory.openSession();
		   tx =session.beginTransaction();
		   
		
		   
		   ips.setCreated_date(new Date());
		   ips.setUpdated_date(new Date());
		   log.info("IPSReceiverDAOImpl.createIPSReceiverEntry():: ips entry=="+ips);
		   		   
		   session.save(ips);
		   tx.commit();
		   
		   log.info("IPSReceiverDAOImpl.createIPSReceiverEntry():: entry added");
		   insertflag = true;
		   
		  } catch (Exception he) {
		   log.debug("HibernateException : Insertion Failed - " + he);
		   he.printStackTrace();
		   if(null != ips)
		    session.evict(ips);
		   tx.rollback();
		  }finally{
		   session.close();
		  }
		   
		  return insertflag;
	}


	@Override
	public int[] getIPSReceiverDetailsForMac(String ipsReceiverMac) {
		Session session = null;
		  
		  IPSReceiver ips = null; 
		  int receiverId;
		  int schoolId;
		  int[] receiverIdAndSchoolId_Arr = new int[2];
		  
		  try {
		  session = sessionFactory.openSession();  
		  
		  String sessionQuery = "SELECT ips_receiver_id, school_id FROM ips_receiver WHERE ips_receiver_mac = :ipsReceiverMac";
		  Query query = session.createSQLQuery(sessionQuery);
		  query.setParameter("ipsReceiverMac", ipsReceiverMac);	
		  
		  log.info("query string"+ sessionQuery );
		  
		  //Integer id = (Integer) query.uniqueResult() ;  
		 
		  List<Object[]> rows = query.list();
		  log.info("rows ----" +rows.size());
		  
		if(rows!=null){	
		  for (Object[] row: rows) {
			   
			    receiverIdAndSchoolId_Arr[0] = (Integer)row[0];//receiverId
			    receiverIdAndSchoolId_Arr[1] = (Integer)row[1]; //schoolId
			    log.info("receiverId: " + receiverIdAndSchoolId_Arr[0]);
			    log.info("schoolId: " + receiverIdAndSchoolId_Arr[1]);
			}
		}
		  
				
		   return receiverIdAndSchoolId_Arr ;
		  
		  }catch(Exception e) {
		   log.info("Exception Occured in IPSReceiverDAOImpl. getIPSLocatorsDetailsForMac() "+e);
		  
		  }finally {
		   session.close();
		  }
		  
		return receiverIdAndSchoolId_Arr;
	}


	@Override
	public int updateIPSReceiverBySessionId(int receiver_Id, String sessionId) {
		
		  Session session = null;
		  Transaction tx=null;
		try{
		  session = sessionFactory.openSession();
		 tx = session.beginTransaction();
		 
		  Timestamp updatedAt = new Timestamp(Calendar.getInstance().getTimeInMillis());
		  updatedAt.setTime(updatedAt.getTime() + Long.valueOf(IPS_SESSION_EXPIRY_TIME));
		  
		  Query query = session.createSQLQuery(
		    "update ips_receiver set session_id=:sessionId, session_expiry =:sessionExpiry where ips_receiver_id=:receiver_Id");
		  query.setParameter("sessionId", sessionId);
		  query.setParameter("sessionExpiry", updatedAt);
		  query.setParameter("receiver_Id", receiver_Id);
		  
		  log.info("query string"+ query );
		  
		  int result = query.executeUpdate();
		  log.info("Result" + result);
		  
		  tx.commit();
		  return result;
		  
		}catch(Exception e){
			   log.debug("Exception inside IPSReceiverDAOImpl:updateIPSLocatorsBySessionId :  " + e);
			  
		}finally{
			   session.close();
	    }
		  
		  return 0;
	}//end of method


	@Override
	public IPSReceiver getIPSReceiverDetailsForID(int receiver_Id) {
		  Session session = null;		 
		  IPSReceiver ips = null;
		 
		  try {
		  session = sessionFactory.openSession();
		 		  
		  ips = (IPSReceiver) session.get(IPSReceiver.class, receiver_Id);
		  
		  log.info("querying IPSReceiver entry....");		 
		  
		  }catch(Exception e) {
		   log.info("Exception Occured in getIPSReceiverDetailsForID() " +e);
		   e.printStackTrace();
		  
		  }finally {
		   session.close();
		  }

		  return ips;
	}


	@Override
	public boolean updateIPSReceiverEntry(IPSReceiver ips) {
		
		  Session session = null;
		  Transaction tx=null;  
		  
		  try{
		  session = sessionFactory.openSession();
		   tx = session.beginTransaction();			   
		  
		   ips.setUpdated_date(new Date());
		   
		   log.info("inside DAO update function IPSReceiver details::"+ips.toString());

		   session.update(ips);  
		   tx.commit(); 
		     
		   return true;
		   
		  }catch(Exception e){
			   log.debug("Exception inside IPSReceiver DAOImpl :  " + e);
			   return false;
		 }finally{
			   session.close();
		  }
		
	}//end of update method


	@Override
	public boolean deleteIPSReceiverEntry(int receiver_Id) {
		log.info(" inside DAOImpl : deleteIPSReceiverEntry " );
		  Session session = null;
		  Transaction tx=null;  
		 
		  try {
		   session = sessionFactory.openSession();
		   tx =session.beginTransaction();   
		  
		   String sessionQuery = "DELETE FROM ips_receiver WHERE ips_receiver_id=:receiver_Id";
		   Query query = session.createSQLQuery(sessionQuery);
		   query.setParameter("receiver_Id", receiver_Id);
		   int result = query.executeUpdate();
		   tx.commit();
		   return true;
		   
		  }catch(Exception e){
		   log.debug("Exception inside IPSReceiver DAOImpl :  " + e);
		   e.printStackTrace();
		   return false;
		  }finally{
		   session.close();
		  }   
	}


	@Override
	public int checkMacExistWithAnyReceiver(String ipsReceiverMac) {
		//int[] arr = this.getIPSReceiverDetailsForMac(ipsReceiverMac);		
		log.info("inside checkMacExistWithAnyReceiver...");
		Session session = null;
		  
		  IPSReceiver ips = null; 
		 int receiverId =0;		
		  
		  try {
		  session = sessionFactory.openSession();  
		  
		  String sessionQuery = "SELECT ips_receiver_id FROM ips_receiver WHERE ips_receiver_mac = :ipsReceiverMac";
		  Query query = session.createSQLQuery(sessionQuery);
		  query.setParameter("ipsReceiverMac", ipsReceiverMac);	
		  
		  log.info("checkMacExistWithAnyReceiver query string"+ sessionQuery );
		  
		  receiverId = (Integer) query.uniqueResult() ;  
		 
		 
		  log.info("receiver _id got from DB" +receiverId);
		  
		   return receiverId ;
		  
		  }catch(Exception e) {
		   log.info("Exception Occured in IPSReceiverDAOImpl. checkMacExistWithAnyReceiver() "+e);
		  
		  }finally {
		   session.close();
		  }
		  
		return receiverId;
		
	}


	@Override
	public IPSReceiver getIPSReceiverEntryForMac(String ipsReceiverMac) {
		  Session session = null;		 
		  IPSReceiver ips = null;
		 
		  try {
		  session = sessionFactory.openSession();
		  Transaction tx= session.beginTransaction();
		  
		  	Criteria criteria = session.createCriteria(IPSReceiver.class,"ips");
			criteria.add(Restrictions.eq("ips.receiverMac", ipsReceiverMac));
			List<IPSReceiver> list = criteria.list();
		  
		     if(list != null && list.size() > 0){
		    	 log.info("inside getIPSReceiverEntryForMac:: list:" + list.get(0).toString());
		    	 ips = (IPSReceiver)list.get(0); 
		     }
		    	 
		     tx.commit();
		  }catch(Exception e) {
		   log.info("Exception Occured in getIPSReceiverEntryForMac() " +e);
		   e.printStackTrace();
		  
		  }finally {
		   session.close();
		  }

		  return ips;
	}


	
	@Override
	public int getSessionValidityInMinutes(String sessionId, int receiver_id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		
		Criteria criteria = session.createCriteria(IPSReceiver.class,"rec");
		criteria.add(Restrictions.eq("rec.sessionId", sessionId));
		criteria.add(Restrictions.eq("rec.receiverId", receiver_id));
		IPSReceiver ips = (IPSReceiver) criteria.uniqueResult();
		
		
		Timestamp ts= new Timestamp(ips.getSessionExpiry().getTime());
		Time ts1 = null;
		String tsQuery ="select TIMEDIFF("+"'"+ts+"'"+",NOW()) ";
		Query query = session.createSQLQuery(tsQuery);
		ts1 = (Time)query.uniqueResult();
		log.info("ts1.getMinutes()"+"\t"+ts1.getMinutes());
		tx.commit();
		session.close();
		return ts1.getMinutes();
	}

	@Override
	public int checkSchoolIdExistWithAnyReceiver(int schoolId) {
		
		  Session session = null;		 
		  try{
		  session = sessionFactory.openSession();		 
		   
		  String sessionQuery = "SELECT ips_receiver_id FROM ips_receiver as ips, accounts as acc WHERE ips.school_id = acc.account_id AND school_id = :schoolId";
		  Query query = session.createSQLQuery(sessionQuery);
		  query.setParameter("schoolId", schoolId);
		  
		  log.info("query string"+ sessionQuery );
		  
		  Integer receiverId = (Integer)query.uniqueResult();
		 
		  return  receiverId.intValue(); 
		  
		  }catch(Exception e){
		   log.debug("Exception inside IPSReceiverDAOImpl : checkSchoolIdExistWithAnyReceiver " + e);		   
		  }finally{
		   session.close();
		  }
		  
		return 0;
	}
	
	
	@Override
	public IPSReceiver getIPSReceiverDetailsForSchoolID(int schoolId) {
		  Session session = null;		 
		  IPSReceiver ips = null;	 
		  try {
		  session = sessionFactory.openSession();
		 		  
		  //ips = (IPSReceiver) session.get(IPSReceiver.class, schoolId);
		  
		  	Transaction tx = session.beginTransaction();
			
			Criteria criteria = session.createCriteria(IPSReceiver.class,"rec");
			criteria.add(Restrictions.eq("rec.schoolId", schoolId));			
			 ips = (IPSReceiver) criteria.uniqueResult();
		   
		  log.info("querying IPSReceiver entry....ips="+ips.toString());		 
		    tx.commit();
		  }catch(Exception e) {
		   log.info("Exception Occured in getIPSReceiverDetailsForSchoolID() " +e);
		   e.printStackTrace();
		  
		  }finally {
		   session.close();
		  }

		  return ips;
	}


	@Override
	public String findIPSReceiverByMacAndSessionId(String sessionId, String macAddress){
		// TODO Auto-generated method stub
	 log.info(" inside getIPSReceiverBySessionId");
	 Session session = null;
	 Transaction tx=null;  
	 IPSReceiver ipsReceiverBySession = null, ipsReceiverBySessionAndMac = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			Criteria tokenCriteria = session.createCriteria(IPSReceiver.class);
			tokenCriteria.add(Restrictions.eq("sessionId", sessionId));
			ipsReceiverBySession = (IPSReceiver) tokenCriteria.uniqueResult();
			
			if(ipsReceiverBySession == null)
				return "Token Does Not Exist";
			
			Criteria tokenAndMacCriteria = session.createCriteria(IPSReceiver.class);
			tokenAndMacCriteria.add(Restrictions.eq("sessionId", sessionId));
			tokenAndMacCriteria.add(Restrictions.eq("receiverMac", macAddress));
			ipsReceiverBySessionAndMac = (IPSReceiver)tokenAndMacCriteria.uniqueResult();
			
			if(ipsReceiverBySessionAndMac == null)
				return "SessionId, MacAddress Mapping Does Not Exist";
			
			String sessionValidityResult = CommonUtil.checkSessionValidity(ipsReceiverBySessionAndMac);
			
			if(sessionValidityResult.equals("NOTVALID")) {
				return "NOTVALID";
			}
			
			
			tx.commit();
		} catch (Exception e) {
			log.info("Exception Occured In" + "\t" + e);
			if(null != ipsReceiverBySession)
				session.evict(ipsReceiverBySession);
			if(null != ipsReceiverBySessionAndMac)
				session.evict(ipsReceiverBySessionAndMac);
			tx.rollback();
		}finally {
			session.close();
		}
		return "Success";
	}

	@Override
	public String getMobileNumberBySchoolId(int schoolId){
		// TODO Auto-generated method stub
	 log.info(" inside getIPSReceiverBySessionId");
	 Session session = null;
	 Transaction tx=null;  
	 SchoolDetails schoolDetails = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			Criteria criteria = session.createCriteria(SchoolDetails.class);
			criteria.add(Restrictions.eq("schoolId", schoolId));
			schoolDetails = (SchoolDetails) criteria.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			log.info("Exception Occured In" + "\t" + e);
			if(null != schoolDetails)
				session.evict(schoolDetails);
			tx.rollback();
		}finally {
			session.close();
		}
		return schoolDetails.getMobile_number();
	}


	@Override
	public IPSReceiverDevice findIPSReceiverDevice(int ipsReceiverId, int ips_receiver_zone_id) {
		// TODO Auto-generated method stub
		
		Session session = null;
		Transaction tx = null;
		IPSReceiverDevice ipsReceiverDevice = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			Criteria criteria = session.createCriteria(IPSReceiverDevice.class);
			criteria.add(Restrictions.eq("receiverId", ipsReceiverId));
			criteria.add(Restrictions.eq("zoneId", ips_receiver_zone_id));
			ipsReceiverDevice = (IPSReceiverDevice) criteria.uniqueResult();
		}catch(Exception e) {
			log.info("Exception Occured in findIPSReceiverDevice () " +"\t"+e);
			if(null != ipsReceiverDevice)
				session.evict(ipsReceiverDevice);
			tx.rollback();
		}finally {
			session.close();
		}
		return ipsReceiverDevice;
	}


	@Override
	public IPSReceiver updateIPSReceiverStatus(String ips_receiver_mac, String ips_receiver_status) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		IPSReceiver ipsReceiver = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			Criteria criteria = session.createCriteria(IPSReceiver.class);
			criteria.add(Restrictions.eq("receiverMac", ips_receiver_mac));
			ipsReceiver = (IPSReceiver) criteria.uniqueResult();
			ipsReceiver.setReceiver_status(ips_receiver_status);
			session.merge(ipsReceiver);
			tx.commit();
		}catch(Exception e) {
			log.error("Error Occured in updateIPSReceiverStatus ()"+"\t"+e);
			tx.rollback();
		}finally {
			session.close();
		}
		return ipsReceiver;
	}

}
