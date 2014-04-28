package it4bi.ufrt.ir.service.users;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jersey.repackaged.com.google.common.collect.ImmutableMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UsersDAO {

	private static final String USER_BY_ID_QUERY = "SELECT userID,name,surname,country,sex,birthday "
			+ "FROM Users where userId = :userId;";

	private final NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	public UsersDAO(@Qualifier("appJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public List<User> getAllUsers() {
		//return UserDatabase.getUsers();
		
		List<User> users = new ArrayList<User>();
		List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
				"select * from Users", new HashMap<String, Object>());
		
		for (Map<String, Object> row : rows) {
			User user = new User();
			user.setBirthday(String.valueOf(row.get("birthday")));
			user.setCountry(String.valueOf(row.get("country")));
			user.setID(Integer.parseInt(String.valueOf(row.get("userID"))));
			user.setName(String.valueOf(row.get("name")));
			user.setSurname(String.valueOf(row.get("surname")));
			
			if(String.valueOf(row.get("sex")).equals("M")) user.setSex(UserSex.MALE);
			else user.setSex(UserSex.FEMALE);
			
			users.add(user);
		}
		
		return users;
	}
	
	public void insertUser(User user) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", user.getName());
		parameters.put("surname", user.getSurname());
		parameters.put("country", user.getCountry());
		parameters.put("sex", (user.getSex() == UserSex.MALE) ? "M" : "F");
		parameters.put("birthday", user.getBirthday());

		this.jdbcTemplate.update(
				"insert into Users (name, surname, country, sex, birthday) values (:name, :surname, :country, :sex, :birthday)", parameters);
	}

	public User findUserBy(int id) {
		Map<String, ?> paramMap = ImmutableMap.of("userId", id);
		return jdbcTemplate.queryForObject(USER_BY_ID_QUERY, paramMap, new UserRowMapper());
	}

	private static class UserRowMapper implements RowMapper<User> {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			// SELECT userID,name,surname,country,sex,birthday
			int userId = rs.getInt(1);
			String name = rs.getString(2);
			String surname = rs.getString(3);
			String country = rs.getString(4);
			String sex = rs.getString(5);
			String birthday = rs.getString(6);
			return new User(userId, name, surname, country, UserSex.fromDbValue(sex), birthday);
		}
	}
}
