package com.liteon.icgwearable.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.ClassGrade;
import com.liteon.icgwearable.hibernate.entity.Students;
import com.liteon.icgwearable.hibernate.entity.Timetable;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.TimeTableModel;
import com.liteon.icgwearable.transform.StudentTimeTableTransform;
import com.liteon.icgwearable.transform.StudentsClassListTransform;
import com.liteon.icgwearable.transform.TeacherTimeTableTransform;
import com.liteon.icgwearable.transform.TimetableTransform;

@Repository("timetableDAO")
public class TimetableDAOImpl implements TimetableDAO {
	private static Logger log = Logger.getLogger(TimetableDAOImpl.class);
	@Autowired
	protected SessionFactory sessionFactory;
	
	
	@Autowired
	private CommonDAO commonDAO;
	@Override
	public List<TimetableTransform> listTimetableUniqueClass(int userId) {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String uniqeClassQuery = "SELECT DISTINCT cg.class AS studentClass,cg.grade AS grade "
				+ "FROM class_grade AS cg "
				+ "LEFT JOIN timetable AS t ON cg.class_grade_id = t.class_grade_id "
				+ "LEFT JOIN accounts AS a ON a.account_id = cg.school_id "
				+ "LEFT JOIN users AS u ON u.account_id = a.account_id "
				+ "WHERE u.user_id = ?";
		List<TimetableTransform> timetableClassList = null;

		Query query = session.createSQLQuery(uniqeClassQuery).addScalar("studentClass").addScalar("grade")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(TimetableTransform.class));
		query.setParameter(0, userId);
		timetableClassList = query.list();
		tx.commit();
		session.close();
		return timetableClassList;
	}
	
	@Override
	public List<Timetable> findtimeTableByClass(String studentClass) {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Timetable.class);
		criteria.add(Restrictions.eq("studentClass", Integer.parseInt(studentClass)));

		List<Timetable> timetableList = criteria.list();
		tx.commit();
		session.close();

		return timetableList;
	}
	
	@Override
	public Map<String, Object>  createTimeTable(List<TimeTableModel> timeList, TimeTableModel timeTableModel, Accounts accounts) {

		Map<Integer, String> ignoredMap = new HashMap<Integer, String>();
		Users users = new Users();
		users.setId(timeTableModel.getUserId());
		users.setAccounts(accounts);
		users.setUserActive("y");
		users.setRoleType("school_admin");
		Integer insertCount =0;
		Integer ignoredCount =0;
		Integer totalCount =0;
		int deleteCount = 0;
		Map<String, Object> finalMap = new HashMap<String, Object>();

		// delete the existing timetable data for the school Id 
		// received from CSV file
		Session session = null;
		Transaction deleteTx = null;
		try {
			session = sessionFactory.openSession();
			int schoolId = accounts.getAccountId();
			log.info("Deleting existing timetable for school id: " + schoolId);
			deleteTx  = session.beginTransaction();
			String deleteQuery = "DELETE FROM timetable WHERE class_grade_id IN "
					+ "(SELECT class_grade_id FROM class_grade WHERE school_id=:schoolId)";
			Query query = session.createSQLQuery(deleteQuery);
			query.setParameter("schoolId", schoolId);
			int result = query.executeUpdate();
			log.info("Delete timetable for a school, result => " + result);
			deleteTx.commit();
			deleteCount++;
		} catch (Exception e) {
			e.printStackTrace();
			if(null != deleteTx) {
				log.info("Rolling back delete school timetable transaction");
				deleteTx.rollback();
			}
			deleteCount = -1;
		} finally {
			if(null != session) {
				session.close();
			}
		}

		if(deleteCount > 0) {
			Session saveSession = null;
			Transaction saveTx = null;
			try {
				saveSession = sessionFactory.openSession();
				log.info("size is "+timeList.size());
				for (int i = 0; i < timeList.size(); i++) {
					totalCount++;
					log.info("before insert  ");
					TimeTableModel timeTableModel1 = timeList.get(i);
					log.info("before insert  "+timeTableModel1.getStudentClass());
					log.info("before insert  "+timeTableModel1.getGrade());
					ClassGrade classGrade = commonDAO.getClassGrade(timeTableModel1.getStudentClass().toString(),timeTableModel1.getGrade().toString(),accounts.getAccountId());
					log.info("Account ID "+accounts.getAccountId());
					if(null != classGrade ) {
						try {
							log.info("in if :");
							saveTx = saveSession.beginTransaction();
							String queryString = "INSERT INTO timetable (class_grade_id, subject_one,"
									+ " subject_two, subject_three, subject_four, subject_five,"
									+ " subject_six, subject_seven, subject_eight, week_day)"
									+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
							Query query = saveSession.createSQLQuery(queryString);
							query.setParameter(0, classGrade.getClassGradeId());
							query.setParameter(1, timeTableModel1.getSubjectOne());
							query.setParameter(2, timeTableModel1.getSubjectTwo());
							query.setParameter(3, timeTableModel1.getSubjectThree());
							query.setParameter(4, timeTableModel1.getSubjectFour());
							query.setParameter(5, timeTableModel1.getSubjectFive());
							query.setParameter(6, timeTableModel1.getSubjectSix());
							query.setParameter(7, timeTableModel1.getSubjectSeven());
							query.setParameter(8, timeTableModel1.getSubjectEight());
							query.setParameter(9, timeTableModel1.getWeekDay());
							int result = query.executeUpdate();
							log.info("Timetable Insert Query Result: " + result);
							saveTx.commit();							
							insertCount++;
						} catch (Exception e) {
							e.printStackTrace();
							if(null != saveTx) {
								saveTx.rollback();
							}
						}
					} else {
						ignoredCount++;
						ignoredMap.put(i, " Invalid CLASS AND GRADE Combination ");
						log.info("wrong information provided  ");
					}
				}
				finalMap.put("insertCount", insertCount);
				finalMap.put("ignnoredCount", ignoredCount);
				finalMap.put("totalCount", totalCount);
				finalMap.put("ignoredList", ignoredMap);

			} catch (Exception exception) {
				exception.printStackTrace();
			}

			finally {
				if(null != saveSession) {
					saveSession.close();
				}
			}
		}

		return finalMap;
	}
	
	@Override
	public List<Timetable> findTimeTableByClass(String studentClass) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Timetable.class);
		criteria.add(Restrictions.eq("studentClass", studentClass));

		List<Timetable> timetableList = criteria.list();
		tx.commit();
		session.close();

		return timetableList;
	}
	
	@Override
	public void updateTimetable(Timetable timetable) {

		Session session = sessionFactory.openSession();
		Transaction tx= session.beginTransaction();
		if (timetable != null) {
			session.update(timetable);
		}
		tx.commit();
		session.close();
	}
	
	@Override
	public void deleteTimeTable(String studentClass, int userId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createSQLQuery("delete from timetable where user_id = ? and class = ?");
		query.setParameter(0, userId);
		query.setParameter(1, studentClass);
		query.executeUpdate();

		tx.commit();
		session.close();
	}
	
	@Override
	public Students checkClass(Integer studentId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Students students = new Students();
		Criteria criteria = session.createCriteria(Students.class);
		criteria.add(Restrictions.eq("studentId", studentId));
		students = (Students) criteria.uniqueResult();
		tx.commit();
		session.close();
		return students;
	}
	
	@Override
	public List<Timetable> listOfSubject(Accounts accounts, Integer studentClass) {

		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		Criteria criteria = session.createCriteria(Timetable.class);
		criteria.add(Restrictions.eq("studentClass", studentClass));
		criteria.add(Restrictions.eq("accounts", accounts));
		List<Timetable> list = criteria.list();
		transaction.commit();
		session.close();
		return list;
	}
	
	@Override
	public List<StudentsClassListTransform> listOfClass(int userId) {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		/*String classQuery = "select distinct students.school_id as schoolId, students.class as studentClass from students "
				+ "LEFT JOIN devices ON devices.student_id = students.student_id "
				+ "LEFT JOIN users ON users.user_id = devices.teacher_id "
				+ "WHERE users.user_active = 'y' AND users.role_type = 'school_teacher' AND users.user_id =?";*/

		String classQuery = "select distinct cg.class_grade_id from class_grade cg " +
							"left join users u on u.user_id = cg.teacher_id " +
							"left join students st on st.class_grade_id=cg.class_grade_id " +
							"WHERE u.user_active = 'y' AND u.role_type = 'school_teacher' AND u.user_id = ? " ;

		List<StudentsClassListTransform> classList = null;

		Query query = session.createSQLQuery(classQuery).addScalar("schoolId").addScalar("studentClass")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(StudentsClassListTransform.class));
		query.setParameter(0, userId);
		classList = query.list();
		tx.commit();
		session.close();
		return classList;
	}

	@Override
	public List<TeacherTimeTableTransform> getTeacherClassTimetable(int teacher_id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String teacherTimeTableQuery = "select cg.class as studentClass,cg.grade as grade,tt.week_day as weekDay, tt.subject_one as subjectOne,tt.subject_two as subjectTwo,tt.subject_three as subjectThree,tt.subject_four as subjectFour,\r\n" + 
				"tt.subject_five as subjectFive,tt.subject_six as subjectSix,tt.subject_seven as subjectSeven,tt.subject_eight as subjectEight  from class_grade cg \r\n" + 
				"LEFT JOIN timetable tt on cg.class_grade_id= tt.class_grade_id\r\n" + 
				"where cg.teacher_id=:teacher_id";

		List<TeacherTimeTableTransform> teacherTimeTableList = null;

		Query query = session.createSQLQuery(teacherTimeTableQuery).addScalar("studentClass").addScalar("grade")
				.addScalar("weekDay").addScalar("subjectOne").addScalar("subjectTwo").addScalar("subjectThree")
				.addScalar("subjectFour").addScalar("subjectFive").addScalar("subjectSix").addScalar("subjectSeven")
				.addScalar("subjectEight").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(TeacherTimeTableTransform.class));
		query.setParameter("teacher_id", teacher_id);
		teacherTimeTableList = query.list();
		tx.commit();
		session.close();
		return teacherTimeTableList;
	}

	@Override
	public List<StudentTimeTableTransform> getStudentClassTimetable(int user_id, String uuid) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String studentTimeTableQuery = "select cg.class as studentClass,cg.grade as grade,tt.week_day as weekDay, tt.subject_one as subjectOne,tt.subject_two as subjectTwo,tt.subject_three as subjectThree,tt.subject_four as subjectFour,\r\n" + 
				"tt.subject_five as subjectFive,tt.subject_six as subjectSix,tt.subject_seven as subjectSeven,tt.subject_eight as subjectEight  from class_grade cg \r\n" + 
				"LEFT JOIN students st on cg.class_grade_id= st.class_grade_id\r\n" + 
				"LEFT JOIN device_students ds on st.student_id=ds.student_id \r\n" + 
				"LEFT JOIN parent_kids pk on pk.student_id = ds.student_id \r\n" + 
				"LEFT JOIN timetable tt on cg.class_grade_id= tt.class_grade_id\r\n" + 
				"where pk.user_id=:user_id and ds.device_uuid =:uuid";

		List<StudentTimeTableTransform> studentTimeTableList = null;

		Query query = session.createSQLQuery(studentTimeTableQuery).addScalar("studentClass").addScalar("grade")
				.addScalar("weekDay").addScalar("subjectOne").addScalar("subjectTwo").addScalar("subjectThree")
				.addScalar("subjectFour").addScalar("subjectFive").addScalar("subjectSix").addScalar("subjectSeven")
				.addScalar("subjectEight").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(StudentTimeTableTransform.class));
		query.setParameter("user_id", user_id);
		query.setParameter("uuid", uuid);
		studentTimeTableList = query.list();
		tx.commit();
		session.close();
		return studentTimeTableList;
	}

	@Override
	public List<StudentTimeTableTransform> getTimeTable(String grade, String studentClass, Integer schoolId) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String uniqeClassQuery = "select subject_one  as subjectOne,subject_two  as subjectTwo,subject_three as subjectThree,\r\n" + 
				"subject_four as subjectFour,subject_five as subjectFive,subject_six as subjectSix ,\r\n" + 
				"subject_seven as subjectSeven,subject_eight as subjectEight,week_day as weekDay from timetable tt\r\n" + 
				"LEFT JOIN class_grade as cg on cg.class_grade_id = tt.class_grade_id \r\n" + 
				"where cg.school_id = :schoolId AND cg.class =:stdentClass and cg.grade=:grade";
		List<StudentTimeTableTransform> timetableList = null;

		Query query = session.createSQLQuery(uniqeClassQuery).addScalar("weekDay").addScalar("subjectOne").addScalar("subjectTwo").addScalar("subjectThree")
				.addScalar("subjectFour").addScalar("subjectFive").addScalar("subjectSix").addScalar("subjectSeven")
				.addScalar("subjectEight").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(StudentTimeTableTransform.class));
		query.setParameter("stdentClass", studentClass);
		query.setParameter("grade", grade);
		query.setParameter("schoolId", schoolId);
		timetableList = query.list();
		tx.commit();
		session.close();
		return timetableList;
	}

}
