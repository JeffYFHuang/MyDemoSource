package com.liteon.icgwearable.service;

import java.util.List;

import com.liteon.icgwearable.hibernate.entity.Students;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.GuardianModel;
import com.liteon.icgwearable.transform.StudentsTransform;
import com.liteon.icgwearable.transform.TeachersStudentsTransform;

public interface ParentService {
	public List<TeachersStudentsTransform> viewKidsList(int userId, String classId);

	public void updateKids(Students kids);

	public List<StudentsTransform> findKidsClassAndSchool(int userId);

	public List<TeachersStudentsTransform> viewKidsListByClass(int userId, int classId);

	public boolean ParentKidLinked(int userId, int studentId);
	
	public void linkParentKid(int userId, int studentId);

	public boolean findKidForParent(int user_id, int student_id);
	
	public void createGuradian(Users userBySessionId,GuardianModel guardianModel);

	public void deleteGuardian(Users guardianUser);

	public void editGuradian(Users guardianUser, GuardianModel guardianModel);
}
