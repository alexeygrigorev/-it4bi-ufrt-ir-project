package it4bi.ufrt.ir.business;

import it4bi.ufrt.ir.service.users.User;
import it4bi.ufrt.ir.service.users.UserSex;

public class UserDatabase {

	private static final User[] users = {
		new User("Richard", "Kondor", "Italy", UserSex.MALE, "1995-05-28"),
		new User("Jack", "Willis", "Greece", UserSex.MALE, "1995-05-28"),
		new User("Peter", "Brando", "France", UserSex.MALE, "1982-07-14"),
		new User("Lara", "Kronch", "Spain", UserSex.FEMALE, "1992-11-16"),
		new User("Anna", "Micht", "Germany", UserSex.FEMALE, "1989-03-18")
	};

	public static User[] getUsers() {
		return users;
	}
}
