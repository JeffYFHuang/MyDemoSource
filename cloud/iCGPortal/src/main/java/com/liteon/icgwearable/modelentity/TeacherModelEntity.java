package com.liteon.icgwearable.modelentity;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.UsersModel;
import com.liteon.icgwearable.security.AESEncryption;
import com.liteon.icgwearable.service.UserService;

public class TeacherModelEntity {
	
	private static Logger log = Logger.getLogger(TeacherModelEntity.class);
	@Autowired
	private UserService userService;
	@Autowired
	private HttpSession httpSession;
	
	public UsersModel prepareTecherModel(Users users) {
		UsersModel um = new UsersModel();
		um.setId(users.getId());
		um.setName(users.getName());
		um.setUsername(users.getUsername());
		um.setPassword(users.getPassword());
		//um.setEmail(users.getEmail());
/*		um.setUsername(users.getUsername());
		um.setPassword(users.getPassword());
*/		return um;
	}

	public Users prepareUsersEntity(UsersModel usersModel) {
		UsersModel model = (UsersModel) this.httpSession.getAttribute("userModel");
		Users users = new Users();
		Integer userId = (Integer) this.httpSession.getAttribute("userId");
		Accounts accounts = this.userService.getAccounts(userId);

		users.setId(usersModel.getId());
		if(usersModel.getName() != null)
			users.setName(usersModel.getName());
		//if(usersModel.getEmail() != null)
			//users.setEmail(usersModel.getEmail());

		/**
		 * This logic is required if we dont want to show username and password in the form, in that case we need to set session object in add and edit functionality
		 * upfront and need to check accordingly below
		 */
		
		String add= (String)this.httpSession.getAttribute("Add");
		String edit= (String)this.httpSession.getAttribute("edit");
		
		if(add != null && add.equals("Add")){
			log.info("Adding Teacher");
			if(usersModel.getUsername() != null)
				users.setUsername(usersModel.getUsername());
			if(usersModel.getPassword() != null)
				users.setPassword(usersModel.getPassword());
			this.httpSession.removeAttribute("Add");
			
		}else if(edit != null && edit.equals("edit")){
			log.info("Editing Teacher");
			if(usersModel.getUsername().trim().length() > 0 && usersModel.getPassword().trim().length() == 0){
				log.info("Only Username Changing");
				users.setUsername(usersModel.getUsername());
				users.setPassword((String)this.httpSession.getAttribute("password"));
			}else if(usersModel.getPassword().trim().length() > 0 && usersModel.getUsername().trim().length() == 0){
				log.info("Only Password Changing");
				users.setUsername((String)this.httpSession.getAttribute("username"));
				try {
					users.setPassword(AESEncryption.generatePasswordHash(usersModel.getPassword()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}else if(usersModel.getUsername().trim().length() > 0 && usersModel.getPassword().trim().length() > 0){
				log.info("Both Are Changing");
				users.setUsername(usersModel.getUsername());
				users.setPassword(usersModel.getPassword());
			}else if(usersModel.getUsername().trim().length() == 0 && usersModel.getPassword().trim().length() == 0){
				log.info("None Changing");
				users.setUsername((String)this.httpSession.getAttribute("username"));
				users.setPassword((String)this.httpSession.getAttribute("password"));
			}
			this.httpSession.removeAttribute("edit");
		}
		
		if(usersModel.getRoletypes() != null)
			users.setRoleType(usersModel.getRoletypes());
		
		users.setCreatedDate(new java.util.Date());
		users.setUserActive("y");
		users.setAccounts(accounts);
		return users;
	}
	
	public List<UsersModel> prepareTeachersModelList(List<Users> usersList , int slNo) {
		List<UsersModel> userModelList = null;
		if (usersList != null && !usersList.isEmpty()) {
			userModelList = new ArrayList<>();
			UsersModel userModel = null;

			for (Users u : usersList) {
				String next = (String)this.httpSession.getAttribute("next");
				String previous = (String)this.httpSession.getAttribute("previous");
				
				userModel = new UsersModel();
				userModel.setSlNo(slNo);
				userModel.setId(u.getId());
				userModel.setName(u.getName());
				//userModel.setEmail(u.getEmail());
				userModel.setUsername(u.getUsername());
				userModelList.add(userModel);
				if(next != null)
					slNo ++;
				else if(previous != null)
					slNo--;
			}
			this.httpSession.setAttribute("slNo", slNo);
		}
		return userModelList;
	}

}
