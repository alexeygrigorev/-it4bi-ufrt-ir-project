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
		
		User[] users = new User[5];
		users[0] = new User(0, "Igor", "Shevchenko", "Ukraine", UserSex.MALE, "1995-05-28");
		users[1] = new User(1, "Copy", "Shevchenko", "Ukraine", UserSex.MALE, "1995-05-28");
		users[2] = new User(2, "Peter", "Markovich", "France", UserSex.MALE, "1982-07-14");
		users[3] = new User(3, "Lara", "Kronch", "Spain", UserSex.FEMALE, "1992-11-16");
		users[4] = new User(4, "Anna", "Micht", "Germany", UserSex.FEMALE, "1989-03-18");
		
		return Arrays.asList(users);
	}
}
