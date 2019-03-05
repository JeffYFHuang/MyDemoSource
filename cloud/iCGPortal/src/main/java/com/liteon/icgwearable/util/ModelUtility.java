package com.liteon.icgwearable.util;

import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.FamilyMemberModel;
import com.liteon.icgwearable.model.Model;
import com.liteon.icgwearable.model.ParentModel;
import com.liteon.icgwearable.model.SchoolAdminModel;
import com.liteon.icgwearable.model.TeacherModel;

public class ModelUtility {
	
	private static final String schoolAdmin = "school_admin";
	private static final String schoolTeacher = "school_teacher";
	private static final String parentAdmin = "parent_admin";
	private static final String parentMember = "parent_member";
	private static final String schoolStaff = "school_staff";
	
	public ModelUtility(){
	}
	
	public Model getModelObj(Users user){
		
		if(user.getRoleType().equals(schoolAdmin)){
			SchoolAdminModel schoolAdminModel = new SchoolAdminModel();
			schoolAdminModel.setName(user.getName());
			schoolAdminModel.setEmail(user.getUsername());
			schoolAdminModel.setMobileNumber(user.getMobileNumber());
			return schoolAdminModel;
		}else if(user.getRoleType().equals(schoolTeacher)){
			TeacherModel teacherModel = new TeacherModel();
			teacherModel.setName(user.getName());
			//teacherModel.setEmail(user.getEmail());
			teacherModel.setMobileNumber(user.getMobileNumber());
			return teacherModel;
		}else if(user.getRoleType().equals(parentAdmin)){
			ParentModel parentModel = new ParentModel();
			parentModel.setName(user.getName());
			parentModel.setPassword(user.getPassword());
			parentModel.setMobile_number(user.getMobileNumber());
			return parentModel;
		}else if(user.getRoleType().equals(parentMember)){
			FamilyMemberModel familyMemberModel = new FamilyMemberModel();
			familyMemberModel.setName(user.getName());
			//familyMemberModel.setEmail(user.getEmail());
			familyMemberModel.setMobileNumber(user.getMobileNumber());
			return familyMemberModel;
		}else if(user.getRoleType().equals(schoolStaff)){
			return null;
		}
		
		return null;
		
	}

}
