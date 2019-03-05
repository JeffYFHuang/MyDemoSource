package com.liteon.icgwearable.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.liteon.icgwearable.dao.ClassGradeDAO;
import com.liteon.icgwearable.hibernate.entity.ClassGrade;
import com.liteon.icgwearable.transform.ClassGradeTransform;

@Service("classGradeService")
public class ClassGradeServiceImpl implements ClassGradeService {
	@Autowired
    private ClassGradeDAO classGradeDAO;
	
	@Override
	public List<ClassGradeTransform> getStudentsClass(int userId) {
		return this.classGradeDAO.getStudentsClass(userId);
	}

	@Override
	public boolean classToSchoolMappingExist(Integer schoolClass, Integer schoolId) {
		return this.classGradeDAO.classToSchoolMappingExist(schoolClass, schoolId);
	}

	@Override
	public ClassGrade createClassGrade(ClassGrade cg) {
		// TODO Auto-generated method stub
		return this.classGradeDAO.createClassGrade(cg);
	}

	@Override
	public ClassGrade findSchoolGrade(Integer userId) {
		return this.classGradeDAO.findSchoolGrade(userId);
	}
	
	@Override
	public List<ClassGradeTransform> getGradeClass(int recordId, String columnName, String grade) {
		return this.classGradeDAO.getGradeClass(recordId, columnName, grade);
	}

	@Override
	public ClassGrade updateClassGrade(ClassGrade cg) {
		// TODO Auto-generated method stub
		return this.classGradeDAO.updateClassGrade(cg);
	}

	@Override
	public ClassGrade checkIfClassGradeExists(int school_id, String studentGrade, String studentClass) {
		// TODO Auto-generated method stub
		return this.classGradeDAO.checkIfClassGradeExists(school_id, studentGrade, studentClass);
	}

	@Override
	public List<ClassGradeTransform> getUnassignedGradeClass(int schoolId, int teacherId) {
		// TODO Auto-generated method stub
		return this.classGradeDAO.getUnassignedGradeClass(schoolId, teacherId);
	}	

}
