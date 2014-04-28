package it4bi.ufrt.ir.service.users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsersService {

	@Autowired
	private UsersDAO usersDao;

	/**
	 * Returns all registered users from the database.
	 */
	public List<User> getUsers() {
		return usersDao.getAllUsers();
	}
	
	public User userById(int id) {
		return usersDao.findUserBy(id);
	}
}
