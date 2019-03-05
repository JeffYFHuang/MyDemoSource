package com.liteon.icgwearable.dao;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.liteon.icgwearable.hibernate.entity.Users;

@Repository("schoolAdminDAO")
public class SchoolAdminDAOImpl implements SchoolAdminDAO {

	
	private static Logger log = Logger.getLogger(SchoolAdminDAOImpl.class);
	@Autowired
	protected SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession(){
        return sessionFactory.openSession();
	}

	@Override
	public void addUser(Users user) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.save(user);
		tx.commit();
		session.close();
		
	}

	@Override
	public Users getUser(int userId) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		return (Users)session.get(Users.class, userId);
	}

	@Override
	public void updateUser(Users user) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.update(user);
		tx.commit();
		session.close();
		
	}
	
}
