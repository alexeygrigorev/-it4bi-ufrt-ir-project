package it4bi.ufrt.ir.service.users;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class UsersService {

	/*
	 * Returns all registered users from the database.
	 */
	public List<User> getUsers() {
		
		return Arrays.asList(UsersDAO.getAllUsers());
	}
}
