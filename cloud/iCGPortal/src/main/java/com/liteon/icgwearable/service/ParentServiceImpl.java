package com.liteon.icgwearable.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.liteon.icgwearable.dao.ParentDAO;
import com.liteon.icgwearable.hibernate.entity.Students;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.GuardianModel;
import com.liteon.icgwearable.transform.StudentsTransform;
import com.liteon.icgwearable.transform.TeachersStudentsTransform;

@Service("parentService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ParentServiceImpl implements ParentService {

	@Autowired
    private ParentDAO parentDAO;
	
	@Override
	public List<TeachersStudentsTransform> viewKidsList(int userId, String classId){
		// TODO Auto-generated method stub
		return this.parentDAO.viewKidsList(userId, classId);
	}

	@Override
	public void updateKids(Students kids) {
		// TODO Auto-generated method stub
		this.parentDAO.updateKids(kids);
	}

	@Override
	public List<StudentsTransform> findKidsClassAndSchool(int userId) {
		// TODO Auto-generated method stub
		return this.parentDAO.findKidsClassAndSchool(userId);
	}

	@Override
	public List<TeachersStudentsTransform> viewKidsListByClass(int userId, int classId) {
		// TODO Auto-generated method stub
		return this.parentDAO.viewKidsListByClass(userId, classId);
	}

	@Override
	public boolean ParentKidLinked(int userId, int studentId) {
		// TODO Auto-generated method stub
		return this.parentDAO.ParentKidLinked(userId, studentId);
	}

	@Override
	public boolean findKidForParent(int user_id, int student_id) {
		// TODO Auto-generated method stub
		return this.parentDAO.findKidForParent(user_id, student_id);
	}

	@Override
	public void createGuradian(Users userBySessionId,GuardianModel guardianModel) {
		// TODO Auto-generated method stub
		this.parentDAO.createGuradian(userBySessionId,guardianModel);
	}

	@Override
	public void deleteGuardian(Users guardianUser) {
		this.parentDAO.deleteGuardian(guardianUser);
		
	}

	@Override
	public void editGuradian(Users guardianUser, GuardianModel guardianModel) {
		this.parentDAO.editGuradian(guardianUser,guardianModel);
	}

	@Override
	public void linkParentKid(int userId, int studentId) {
		// TODO Auto-generated method stub
		this.parentDAO.linkParentKid(userId, studentId);
	}

}
