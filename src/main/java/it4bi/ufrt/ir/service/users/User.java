package it4bi.ufrt.ir.service.users;

public class User {
	
	private int ID;
	private String name;
	private String surname;
	private String country;
	private UserSex sex;
	private String birthday;
	
	public User() {
	}

	public User(int ID, String name, String surname, String country, UserSex sex, String birthday) {
		this.ID = ID;
		this.name = name;
		this.surname = surname;
		this.country = country;
		this.sex = sex;
		this.birthday = birthday;
	}
	
	public int getID() {
		return ID;
	}
	
	public void setID(int iD) {
		ID = iD;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	
	public UserSex getSex() {
		return sex;
	}
	
	public void setSex(UserSex sex) {
		this.sex = sex;
	}
	
	public String getBirthday() {
		return birthday;
	}
	
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
}
