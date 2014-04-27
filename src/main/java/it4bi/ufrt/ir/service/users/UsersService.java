package it4bi.ufrt.ir.service.users;

import it4bi.ufrt.ir.service.doc.DocumentsDAO2;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsersService {

	@Autowired
	private UsersDAO usersDAO;
	
	/*
	 * Returns all registered users from the database.
	 */
	public List<User> getUsers() {
		
		return usersDAO.getAllUsers();
	}
}
