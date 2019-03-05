package com.liteon.icgwearable.dao;

import java.util.List;

import java.lang.reflect.Field;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.SessionImpl;
import org.hibernate.loader.OuterJoinLoader;
import org.hibernate.loader.criteria.CriteriaLoader;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.liteon.icgwearable.hibernate.entity.ClassGrade;
import com.liteon.icgwearable.transform.ClassGradeTransform;

@Repository("classGradeDAO")
public class ClassGradeDAOImpl implements ClassGradeDAO {
	private static Logger log = Logger.getLogger(ClassGradeDAOImpl.class);
	@Autowired
	protected SessionFactory sessionFactory;
	
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession() {
		return sessionFactory.openSession();
	}
	@Override
	public List<ClassGradeTransform> getStudentsClass(int userId) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<ClassGradeTransform> classList = null;
		StringBuilder stClassBuilder = new StringBuilder();
		String rewardsByUUIDQuery = null;
		
		stClassBuilder.append("select distinct cg.class as studentClass ,u.role_type as role_type from class_grade as cg " )
		.append("left join users u on u.account_id = cg.school_id ")
		.append("where u.user_id=? ") ;
		rewardsByUUIDQuery= stClassBuilder.toString();
		
		/*String rewardsByUUIDQuery = "select distinct cg.class as studentClass ,u.role_type as role_type from class_grade as cg " +
									"left join users u on u.account_id = cg.school_id " +
									"where u.user_id=? " ;*/

		Query query = session.createSQLQuery(rewardsByUUIDQuery).addScalar("studentClass").addScalar("role_type")
					  .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					  .setResultTransformer(Transformers.aliasToBean(ClassGradeTransform.class));
		
		query.setParameter(0, userId);

		classList = (List<ClassGradeTransform>)query.list();
		log.info("classList Size"+"\t"+classList.size());
		tx.commit();
		session.close();
		return classList;
	}

	@Override
	public boolean classToSchoolMappingExist(Integer studentClass, Integer schoolId) {
		log.info("Into classToSchoolMappingExist {");
		// TODO Auto-generated method stub
		boolean classToSchoolExists = false;
		
		Session session = sessionFactory.openSession();
		Transaction tx =session.beginTransaction();
		
		Criteria criteria = session.createCriteria(ClassGrade.class);
		criteria.add(Restrictions.eq("studentClass", studentClass));
		criteria.add(Restrictions.eq("school_id", schoolId));
		
		CriteriaImpl c = (CriteriaImpl) criteria;
		SessionImpl s = (SessionImpl) c.getSession();
		SessionFactoryImplementor factory = (SessionFactoryImplementor) s.getSessionFactory();
		String[] implementors = factory.getImplementors(c.getEntityOrClassName());
		LoadQueryInfluencers lqis = new LoadQueryInfluencers();
		CriteriaLoader loader = new CriteriaLoader((OuterJoinLoadable) factory.getEntityPersister(implementors[0]), factory, c, implementors[0], lqis);
		Field f=null;
		try {
			f = OuterJoinLoader.class.getDeclaredField("sql");
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		f.setAccessible(true);
		try {
			String sql = (String) f.get(loader);
			log.info("SQL Of Criteria"+"\t"+sql);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ClassGrade cg = (ClassGrade) criteria.uniqueResult();
		if (cg != null) {
			classToSchoolExists = true;
		}
		tx.commit();
		session.close();
		log.info("Exiting classToSchoolMappingExist }");
		return classToSchoolExists;
	}

	@Override
	public ClassGrade createClassGrade(ClassGrade cg) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			session.save(cg);
			tx.commit();
		} catch (Exception e) {
			log.info("Exception Occured in createClassGrade()" + e);
			session.evict(cg);
			tx.rollback();
		} finally {
			session.close();
		}
		
		return cg;
	}

	@Override
	public ClassGrade findSchoolGrade(Integer userId) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		ClassGrade cg = null;
		try {
		session = sessionFactory.openSession();
		tx =session.beginTransaction();
		
		Criteria criteria = session.createCriteria(ClassGrade.class);
		criteria.add(Restrictions.eq("teacher_id", userId));
		cg = (ClassGrade)criteria.uniqueResult();
		
		tx.commit();
		}catch(Exception e) {
			log.info("Exception occured in findSchoolGrade()" +e);
			session.evict(cg);
			tx.rollback();
		}finally{
			session.close();
		}
		return cg;
	}

	@Override
	public ClassGrade updateClassGrade(ClassGrade cg) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			session.update(cg);
			tx.commit();	
		}catch(Exception e) {
			log.info("Exception Occured in updateClassGrade()" +e);
			session.evict(cg);
		}finally {
			session.close();
		}
		return cg;
	}
	
	@Override
	public List<ClassGradeTransform> getGradeClass(int recordId, String columnName, String grade) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		List<ClassGradeTransform> classList = null;
		try {
		session = sessionFactory.openSession();
		tx = session.beginTransaction();
		StringBuffer checkGradeClass = new StringBuffer("");
		
		if(null != grade){
			checkGradeClass.append("AND class_grade.grade = '" + grade + "'");
		}
		
		String gradeClassQuery = "SELECT grade as studentGrade, class as studentClass FROM class_grade "
						+ "WHERE " + columnName + "=? " + checkGradeClass + ""
						+ " ORDER BY grade, class";

		//Query query = session.createSQLQuery(gradeClassQuery);
		Query query = session.createSQLQuery(gradeClassQuery).addScalar("studentGrade").addScalar("studentClass")
				  .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				  .setResultTransformer(Transformers.aliasToBean(ClassGradeTransform.class));
		query.setParameter(0, recordId);

		classList = (List<ClassGradeTransform>)query.list();
		log.info("getGradeClass Size"+"\t"+classList.size());
		tx.commit();
		}catch(Exception e) {
			log.info("Exception in getGradeClass()" +e);
			session.evict(classList);
			tx.rollback();
		}finally{
			session.close();
		}
		return classList;
	}
	
	@Override
	public List<ClassGradeTransform> getUnassignedGradeClass(int schoolId, int teacherId) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		List<ClassGradeTransform> classList = null;
		StringBuilder unAssignedGradeClassBuilder = null;
		String gradeClassQuery = null;
		
		try {
		session = sessionFactory.openSession();
		tx = session.beginTransaction();
		StringBuffer checkTeacherClass = new StringBuffer("");
		if(teacherId > 0) {
			checkTeacherClass.append(" users.user_id = " + teacherId + " OR ");
		}
		
		unAssignedGradeClassBuilder = new StringBuilder();
		unAssignedGradeClassBuilder.append("select teacher_id as teacher_id, grade as studentGrade, class as studentClass ")
		.append("FROM class_grade LEFT JOIN users ON users.user_id = class_grade.teacher_id ")
		.append("where class_grade.school_id= ? AND (" + checkTeacherClass+ " users.user_id IS NULL ) ORDER BY grade, class" );
		gradeClassQuery = unAssignedGradeClassBuilder.toString();
		
		/*String gradeClassQuery = "select teacher_id as teacher_id, grade as studentGrade, class as studentClass "
				+ "FROM class_grade LEFT JOIN users ON users.user_id = class_grade.teacher_id "
				+ "where class_grade.school_id= ? AND (" + checkTeacherClass+ " users.user_id IS NULL )" ;*/

		//Query query = session.createSQLQuery(gradeClassQuery);
		Query query = session.createSQLQuery(gradeClassQuery).addScalar("teacher_id").addScalar("studentGrade").addScalar("studentClass")
				  .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				  .setResultTransformer(Transformers.aliasToBean(ClassGradeTransform.class));
		query.setParameter(0, schoolId);

		classList = (List<ClassGradeTransform>)query.list();
		log.info("getUnassignedGradeClass Size"+"\t"+classList.size());
		tx.commit();
		}catch(Exception e) {
			log.info("Exception in getGradeClass()" +e);
			if(null != classList)
				session.evict(classList);
			tx.rollback();
		}finally{
			session.close();
		}
		return classList;
	}
	

	@Override
	public ClassGrade checkIfClassGradeExists(int school_id, String studentGrade, String studentClass) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		ClassGrade cg = null;
		
		try {
		session = sessionFactory.openSession();
		tx =session.beginTransaction();
		
		Criteria criteria = session.createCriteria(ClassGrade.class);
		criteria.add(Restrictions.eq("schoolId", school_id));
		criteria.add(Restrictions.eq("grade", studentGrade));
		criteria.add(Restrictions.eq("studentClass", studentClass));
		
		cg = (ClassGrade)criteria.uniqueResult();
		log.info("cg.getStudentClass()"+"\t"+cg.getStudentClass());
		log.info("cg.getGrade()"+"\t"+cg.getGrade());
		tx.commit();
		}catch(Exception e) {
			log.info("Exception occured in findSchoolGrade()" +e);
			//session.evict(cg);
			tx.rollback();
		}finally{
			session.close();
		}
		return cg;
	}
}
