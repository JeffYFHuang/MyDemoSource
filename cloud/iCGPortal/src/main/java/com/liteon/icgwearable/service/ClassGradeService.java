package com.liteon.icgwearable.service;

import java.util.List;

import com.liteon.icgwearable.hibernate.entity.ClassGrade;
import com.liteon.icgwearable.transform.ClassGradeTransform;

public interface ClassGradeService {

	public List<ClassGradeTransform> getStudentsClass(int userId);
	
	public boolean classToSchoolMappingExist(Integer schoolClass, Integer schoolId);
	
	public ClassGrade createClassGrade(ClassGrade cg);
	
	public ClassGrade findSchoolGrade(Integer userId);
	
	public ClassGrade updateClassGrade(ClassGrade cg);

	public List<ClassGradeTransform> getGradeClass(int recordId, String columnName, String grade);
	
	public ClassGrade checkIfClassGradeExists(int school_id, String studentGrade, String studentClass);
	
	public List<ClassGradeTransform> getUnassignedGradeClass(int schoolId, int teacherId);
}
