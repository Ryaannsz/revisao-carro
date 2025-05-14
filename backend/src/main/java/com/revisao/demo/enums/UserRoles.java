package com.revisao.demo.enums;

public enum UserRoles {
	
	ADMIN("admin"),
	USER("user");
	
	private String role;
	
	UserRoles(String role){
		this.role=role;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
