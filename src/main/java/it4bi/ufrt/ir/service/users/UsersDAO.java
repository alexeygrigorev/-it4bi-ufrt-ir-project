package it4bi.ufrt.ir.service.users;



import java.util.List;

import it4bi.ufrt.ir.business.UserDatabase;

public class UsersDAO {

	public static User[] getAllUsers() {
		return UserDatabase.getUsers();
	}
	
}
