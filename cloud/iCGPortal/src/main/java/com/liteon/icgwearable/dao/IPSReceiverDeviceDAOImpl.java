package com.liteon.icgwearable.dao;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.liteon.icgwearable.hibernate.entity.IPSReceiverDevice;

@Repository("iPSReceiverDeviceDAO")
@Transactional
public class IPSReceiverDeviceDAOImpl implements IPSReceiverDeviceDAO{

	 private static Logger log = Logger.getLogger(IPSReceiverDeviceDAOImpl.class);
	 
	 @Autowired
	 protected SessionFactory sessionFactory; 
	 
	 public void setSessionFactory(SessionFactory sessionFactory) {
	  this.sessionFactory = sessionFactory;
	 }

	@Override
	public List<Integer> getReceiverDeviceIdsForZoneId(List<Integer> zoneIdList) {
		Session session = null;
		  
		  try{
		  session = sessionFactory.openSession();		  
		   
		  String sessionQuery = "SELECT ips_receiver_device_id FROM ips_receiver_device as st " +
		    "WHERE st.ips_receiver_zone_id in ( :zoneIdList)";
		  
		  Query query = session.createSQLQuery(sessionQuery);
		  query.setParameterList("zoneIdList", zoneIdList);
		  
		  List<Integer>  idList = (List<Integer>)query.list();		  
		  return idList;
		  
		  }catch(Exception e){
		   log.debug("Exception inside IPSReceiverDeviceDAOImpl-getReceiverDeviceIdsForZoneId :  " + e);
		   e.printStackTrace();
		  }finally{
		   session.close();
		  }
		return null;
	}
	
	@Override
	public IPSReceiverDevice getReceiverDeviceIdsForDeviceUUID(String devUUID) {
		  Session session = null;		 
		  IPSReceiverDevice ips = null; 
		 
		  try {
		  session = sessionFactory.openSession();
		  Transaction tx= session.beginTransaction();
		  
		  	Criteria criteria = session.createCriteria(IPSReceiverDevice.class,"ipsd");
			criteria.add(Restrictions.eq("ipsd.deviceUUID", devUUID));	
			 ips = (IPSReceiverDevice)criteria.uniqueResult();
		 
			tx.commit();
		  }catch(Exception e) {
		   log.info("Exception Occured in getIPSReceiverEntryForMac() " +e);
		  
		  }finally {
		   session.close();
		  }

		  return ips;
	
	
		
	}
	
	@Override
	public List<IPSReceiverDevice> getReceiverDevices(Integer zoneId, int receiver_Id) {
		Session session = null;
		 String sessionQuery = null;
		  Query query = null;
		   
		
		log.info("getReceiverDevices..., zoneId="+ zoneId +"  receiver_Id="+receiver_Id);
		  
		  try{
		  session = sessionFactory.openSession();
		 
		 /* String sessionQuery = "SELECT ips_receiver_device_id as receiverDeviceId, ips_receiver_zone_id as zoneId,"+
		     "device_uuid as deviceUUID, firmware_name as firmwareName, firmware_version as firmwareVersion, "+ 
		  	 " device_model as deviceModel, status as status, status_description as statusDesc "+ 
		     "FROM ips_receiver_device as st " +
		     "WHERE st.ips_receiver_zone_id = :zoneId"; */
		  
		  if(zoneId!=null){
			  log.info("getReceiverDevices..., inside null check....");
		   sessionQuery ="SELECT * FROM ips_receiver_device WHERE ips_receiver_zone_id = :zoneId AND ips_receiver_id = :receiver_Id ";
		   
		   query = session.createSQLQuery(sessionQuery).addEntity(IPSReceiverDevice.class);
			  query.setParameter("zoneId", zoneId);
			  query.setParameter("receiver_Id", receiver_Id);
			  
		  }else{
			  
			  log.info("getReceiverDevices..., inside else check....");
		   sessionQuery = "SELECT * FROM ips_receiver_device  " +
				    "WHERE ips_receiver_zone_id IS NULL AND ips_receiver_id = :receiver_Id"; 
		      
		   	query = session.createSQLQuery(sessionQuery).addEntity(IPSReceiverDevice.class);
			  query.setParameter("receiver_Id", receiver_Id);
		   
		  }
		  
		  
		  log.info("getReceiverDevicesForZoneId, query="+ sessionQuery);
		  
		  List<IPSReceiverDevice>  list = (List<IPSReceiverDevice>)query.list();	
		  if(list!=null)
			  log.info("list: "+list.size());
		  
		  return list;
		  
		  }catch(Exception e){
		   log.debug("Exception inside IPSReceiverDeviceDAOImpl-getReceiverDeviceIdsForZoneId :  " + e);
		   
		  }finally{
		   session.close();
		  }
		return null;
	}
	
	
	@Override
	 public List<Integer> getReceiverDeviceIds(int receiver_Id, List<Integer> zoneIdList ) {
	  
		log.info(" inside DAOImpl : getReceiverDeviceIds ::"+ receiver_Id+ "  zonelist="+zoneIdList );
	  Session session = null;
	  String sessionQuery = null;
	  Query query = null;
	  try{
	  session = sessionFactory.openSession();
	  

      if(zoneIdList== null){
    	  log.info("getReceiverDevices..., inside null check....");
    	   sessionQuery = "SELECT ips_receiver_device_id FROM ips_receiver_device  " +
  			    "WHERE ips_receiver_zone_id IS NULL AND ips_receiver_id = :receiver_Id";
    	   query = session.createSQLQuery(sessionQuery);
    		  query.setParameter("receiver_Id", receiver_Id);
      } else{
    	  
    	  log.info("getReceiverDevices..., inside else check....");
	  sessionQuery = "SELECT ips_receiver_device_id FROM ips_receiver_device " +
	    "WHERE ips_receiver_zone_id in (:zoneIdList)  AND ips_receiver_id = :receiver_Id"; 
	  
	    query = session.createSQLQuery(sessionQuery);
	    query.setParameterList("zoneIdList", zoneIdList);
	    query.setParameter("receiver_Id", receiver_Id);
	   
    	  
      }
	  
	  log.info("query string:"+sessionQuery);
	 
	  List<Integer>  idList = (List<Integer>)query.list();
	  log.info("idlist: "+idList.toArray());
	  
	  	 
	  return idList;
	  
	  }catch(Exception e){
	   log.debug("Exception inside DAOImpl :  " + e.getStackTrace());
	   e.printStackTrace();
	  }finally{
	   session.close();
	  }
	  return null;
	 }
	
	
	 @Override
	 public int deleteAllReceiverDevicesForIds(List<Integer> ids, int receiverId) {
	  Session session = null;
	  Transaction tx=null;
	  try{
	  session = sessionFactory.openSession();
	   tx = session.beginTransaction();   
	   
	//  String sessionQuery = "DELETE FROM ips_receiver_device " +
	 //   "WHERE ips_receiver_device_id in ( :ids)";
	  
	  String sessionQuery = "DELETE FROM ips_receiver_device " +
			    "WHERE ips_receiver_id =:receiverId and ips_receiver_device_id in ( :ids)";
	  
	  Query query = session.createSQLQuery(sessionQuery);
	  query.setParameter("receiverId", receiverId);
	  query.setParameterList("ids", ids);
	  
	  int count = query.executeUpdate();
	  tx.commit();
	  return count;
	  
	  }catch(Exception e){
	   log.debug("Exception inside DAOImpl :  " + e);
	   
	  }finally{
	   session.close();
	  }
	  return 0;
	 }

	@Override
	public boolean createIPSReceiverDeviceEntry(IPSReceiverDevice rd) {

		  boolean insertflag = false;
		  Session session = null;
		  Transaction tx=null;
		 
		  try {
		   session = sessionFactory.openSession();
		   tx =session.beginTransaction();		      
		   
		   rd.setCreated_date(new Date());
		   rd.setUpdated_date(new Date());
		   
		   session.save(rd);
		   tx.commit();
		   
		   log.info("IPSReceiverZone DoaImpl.createIPSReceiverDeviceEntry()......... ");
		   insertflag = true;
		  } catch (Exception he) {
		   log.debug("HibernateException : Insertion Failed - " + he);
		   if(null != rd)
		    session.evict(rd);
		   tx.rollback();
		  }finally{
		   session.close();
		  }
		
		return insertflag;
	
	}
	
	@Override
	public boolean updateIPSReceiverDeviceEntry(IPSReceiverDevice ips) {
		//IPSReceiverZone ips=null;
		  Session session = null;
		  Transaction tx=null;  
		  
		  try{
		  session = sessionFactory.openSession();
		   tx = session.beginTransaction();	 	   		   	   
		   
		   ips.setUpdated_date(new Date());
		   
		   log.info("inside DAO update function IPSReceiverdevice details::"+ips.toString());

		   session.update(ips);  
		   tx.commit(); 
		     
		   return true;
		   
		  }catch(Exception e){
			   log.debug("Exception inside DAOImpl :  " + e);
			   return false;
		 }finally{
			   session.close();
		  }
	
		
	}
	
}//end of program
