package com.liteon.icgwearable.modelentity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.Students;
import com.liteon.icgwearable.model.StudentsModel;
import com.liteon.icgwearable.service.AccountService;
import com.liteon.icgwearable.util.DateUtil;

public class StudentModelEntity {

	private static Logger log = Logger.getLogger(StudentModelEntity.class);
	@Autowired
	private HttpSession httpSession;

	@Autowired
	private AccountService accountService;

	
	/*@Value("${display.dateTime}")
	private String sourceDateFormat;
	@Value("${display.dateFormat}")
	private static String sourceDisplayDateFormat;*/
	
	private DateUtil dbUtil = new DateUtil();
	
	
	public StudentsModel prepareStudentsModel(Students student, String sourceDisplayDateFormat) {
		StudentsModel sm = new StudentsModel();
		sm.setStudentId(student.getStudentId());
		//sm.setSchoolId(student.getSchoolId());
		sm.setName(student.getName());
		sm.setNickName(student.getNickName());
		//sm.setStudentClass(String.valueOf(student.getStudentClass()));
		sm.setHeight(String.valueOf(student.getHeight()));
		sm.setWeight(String.valueOf(student.getWeight()));
		sm.setRollNo(String.valueOf(student.getRollNo()));
		sm.setEmergency_contact(student.getEmergencyContactNo());
		log.info("student.getDob()"+"\t"+student.getDob());
		if(student.getDob() != null)
			sm.setDob(DateUtil.convertDateToString(student.getDob(),sourceDisplayDateFormat));
		//sm.setUuid(student.getDevices().getUuid());
	//	sm.setFirmware(student.getDevices().getFirmware().getFirmwareVersion());
		sm.setGender(student.getGender());
		//sm.setAccounts(student.getAccounts());
		
		//this.httpSession.setAttribute("accounts", student.getAccounts());
		//log.info("student.getAccounts() ID"+"\t"+student.getAccounts().getAccountId());
		//log.info("student.getAccounts() RoleType"+"\t"+student.getAccounts().getAccountType());
		//log.info("studentModel.getAccounts() ID"+"\t"+sm.getAccounts().getAccountId());
		log.info("sm.getDob()"+"\t"+sm.getDob());
		return sm;
	}
	
	
	public Students prepareStudentsEntity(StudentsModel studentsModel, String sourceDisplayDateFormat, String sourceDateFormat) {
		Students students = new Students();
		Integer schoolId = (Integer)this.httpSession.getAttribute("accountId");
		String add = (String)this.httpSession.getAttribute("Add");
		String edit = (String)this.httpSession.getAttribute("edit");
		String updateStudent = (String)this.httpSession.getAttribute("updateStudent");
		//Accounts accounts = this.accountService.createAccounts();
		String currentUser = (String) this.httpSession.getAttribute("currentUser");
		students.setStudentId(studentsModel.getStudentId());
		if(studentsModel.getName() != null)
		students.setName(studentsModel.getName());
		if(studentsModel.getNickName() != null)
		students.setNickName(studentsModel.getNickName());
		if(currentUser.equals("school_admin")) {
			if(studentsModel.getStudentId() == null || studentsModel.getStudentId().intValue() == 0) {
				log.info("Inside IF School Admin");
				Accounts accounts = this.accountService.createAccounts();
			//	students.setAccounts(accounts);
				//students.setSchoolId(schoolId);
			}else if(studentsModel.getStudentId() > 0) {
				Accounts accounts = (Accounts) this.httpSession.getAttribute("accounts");
				log.info("Inside Else School Admin");
				log.info("studentsModel.getAccounts().getAccountId()"+"\t"+accounts.getAccountId());
			//	students.setAccounts(accounts);
			//	students.setSchoolId(studentsModel.getSchoolId());
			}
				
		}else if(currentUser.equals("parent_admin")) {
			Accounts accounts = (Accounts) this.httpSession.getAttribute("accounts");
			log.info("Inside Else of Parent Admin");
			log.info("studentsModel.getAccounts().getAccountId()"+"\t"+accounts.getAccountId());
			//students.setAccounts(accounts);
		//	students.setSchoolId(studentsModel.getSchoolId());
		}
		
		if(studentsModel.getStudentClass() != null)
	//	students.setStudentClass(Integer.parseInt(studentsModel.getStudentClass()));
		if(studentsModel.getRollNo()!= null)
		students.setRollNo(studentsModel.getRollNo());
		if(studentsModel.getHeight()!= null)
		students.setHeight(Float.parseFloat(studentsModel.getHeight().trim()));
		if(studentsModel.getWeight()!=null)
		students.setWeight(Float.parseFloat(studentsModel.getWeight().trim()));
		if(studentsModel.getGender() != null)
		students.setGender(studentsModel.getGender());
		if(studentsModel.getEmergency_contact() !=null)
			students.setEmergencyContactNo(studentsModel.getEmergency_contact().trim());
		log.info("In prepareStudentsEntity Before Conversion"+"\t"+studentsModel.getDob());
		if(studentsModel.getDob() !=null)
			students.setDob(dbUtil.changeDateFormat(studentsModel.getDob(), sourceDisplayDateFormat, sourceDateFormat));
		//commented the below code because changeDateFormat method change to normal and without parameter 
		//students.setDob(DateUtil.changeDateFormat(studentsModel.getDob()));
			students.setDob(DateUtil.getDateFromString(studentsModel.getDob(),sourceDateFormat));
		if(add != null && add.equals("Add")){
			students.setDob(dbUtil.changeDateFormat(studentsModel.getDob(), sourceDisplayDateFormat, sourceDateFormat));
			students.setCreateDate(new java.util.Date());
			this.httpSession.removeAttribute("Add");
		}/*else if(edit != null && edit.equals("edit")) {
			log.info("Enter1");
			students.setUpdatedDate(new java.util.Date());
			if(studentsModel.getDob() == null) {
				log.info("entered here into update ");
				
				Date dob= (Date)this.httpSession.getAttribute("dob");
				students.setDob(dob);
				
			}else if(studentsModel.getDob() != null) {
				log.info("Enter2");
				students.setDob(DateUtil.convertStringToDate(studentsModel.getDob()));
			}
		}*/
		/*if(updateStudent != null && updateStudent.equals("updateStudent")){
			log.info("Checking Update Change for UUID");
			Devices devices = new Devices();
			Firmware f= new Firmware();
			
			f.setFirmwareVersion(studentsModel.getFirmware());
			
			devices.setUuid(studentsModel.getUuid());
			devices.setMaxParents(1);;
			devices.setDeviceActive(devices.getDeviceActive().y);
			devices.setPurchaseDate(new java.sql.Timestamp(new java.util.Date().getTime()));
			devices.setCreatedDate(new java.sql.Timestamp(new java.util.Date().getTime()));
			devices.setFirmware(f);

			students.setDevices(devices);
		}*/
		//students.setAccounts(accounts);
		return students;
	}
	
	public List<StudentsModel> prepareStudentsModelList(List<Students> studentsList , int slNo, String sourceDisplayDateFormat) {
		List<StudentsModel> studentsModelList = null;
		if (studentsList != null && !studentsList.isEmpty()) {
			studentsModelList = new ArrayList<>();
			StudentsModel studentsModel = null;

			for (Students students : studentsList) {
				studentsModel = new StudentsModel();
				//studentsModel.setSlNo(slNo);
				studentsModel.setStudentId(students.getStudentId());
				studentsModel.setName(students.getName());
				studentsModel.setNickName(students.getNickName());
				//studentsModel.setStudentClass(String.valueOf(students.getStudentClass()));
				studentsModel.setRollNo(String.valueOf(students.getRollNo()));
				studentsModel.setHeight(String.valueOf(students.getHeight()));
				studentsModel.setWeight(String.valueOf(students.getWeight()));
				studentsModel.setEmergency_contact(students.getEmergencyContactNo());
				if(students.getDob() != null)
					studentsModel.setDob(DateUtil.convertDateToString(students.getDob(), sourceDisplayDateFormat));
				studentsModel.setGender(students.getGender());
				log.info("Date Object:"+"\t"+studentsModel.getDob());
				studentsModelList.add(studentsModel);
				slNo++;
			}
			this.httpSession.setAttribute("slNo", slNo);
		}
		return studentsModelList;
	}
	
	
	public Students studentsParentEntity(StudentsModel studentsModel, Students existingStudent, String sourceDisplayDateFormat, String sourceDateFormat) {
		
		if(studentsModel.getNickname() != null && !studentsModel.getNickname().isEmpty())
			existingStudent.setNickName(studentsModel.getNickname());
		if(studentsModel.getHeight() != null && !studentsModel.getHeight().isEmpty() )
			existingStudent.setHeight(Float.valueOf(studentsModel.getHeight()));
		else
			existingStudent.setHeight(null);
		if(studentsModel.getWeight() != null && !studentsModel.getWeight().isEmpty())
			existingStudent.setWeight(Float.valueOf(studentsModel.getWeight()));
		else
			existingStudent.setWeight(null);
		if(studentsModel.getGender() != null && !studentsModel.getGender().isEmpty())
			existingStudent.setGender(studentsModel.getGender());
		else
			existingStudent.setGender(null);
		log.info("studentsModel.getDob()"+"\t"+studentsModel.getDob());
		log.info("sourceDisplayDateFormat"+"\t"+sourceDisplayDateFormat);
		if(studentsModel.getDob() != null)
			existingStudent.setDob(dbUtil.changeDateFormat(studentsModel.getDob(), sourceDisplayDateFormat, sourceDateFormat));
		log.info("exiting before :"+studentsModel.getEmergency_contact());
		if(studentsModel.getEmergency_contact() != null)
			existingStudent.setEmergencyContactNo(studentsModel.getEmergency_contact());
		if(studentsModel.getAllergies() != null)
			existingStudent.setAllergies(studentsModel.getAllergies());
		log.info("exiting after :"+existingStudent.getEmergencyContactNo());
			existingStudent.setUpdatedDate(new Date());
		
		return existingStudent;
	}
	
	public Students studentsSchoolAdminEntity(StudentsModel studentsModel, Students existingStudent) {
		
		if(studentsModel.getName() != null && !studentsModel.getName().isEmpty())
			existingStudent.setName(studentsModel.getName());
		if(studentsModel.getRoll_no() != null)
			existingStudent.setRollNo(studentsModel.getRoll_no());
		if(studentsModel.getRegistration_no() != null)
			existingStudent.setRegistartion_no(String.valueOf(studentsModel.getRegistration_no()));
		existingStudent.setUpdatedDate(new Date());
		return existingStudent;
	}
}
