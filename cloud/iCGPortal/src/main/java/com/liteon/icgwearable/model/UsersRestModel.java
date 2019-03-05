package com.liteon.icgwearable.model;

public class UsersRestModel {

	private Integer id;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/*public String getRoleTypes() {
		return roleTypes;
	}

	public void setRoleTypes(String roleTypes) {
		this.roleTypes = roleTypes;
	}*/

	public AccountsRestModel getAccounts() {
		return accounts;
	}

	public void setAccounts(AccountsRestModel accounts) {
		this.accounts = accounts;
	}

	private String name;
	private String email;
	private String username;
	private String password;
	//private String roleTypes;
	
	private AccountsRestModel accounts;
}
