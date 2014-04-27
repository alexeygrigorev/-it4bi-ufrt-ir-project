package it4bi.ufrt.ir.service.users;



import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import it4bi.ufrt.ir.business.UserDatabase;
import it4bi.ufrt.ir.service.doc.Tag;

@Repository
public class UsersDAO {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	public UsersDAO(@Qualifier("appJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private class UserRowMapper implements RowMapper {

		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setID(Integer.parseInt(rs.getString("userID")));
			user.setBirthday(rs.getString("birthday"));
			user.setCountry(rs.getString("country"));
			user.setName(rs.getString("name"));
			user.setSurname(rs.getString("surname"));
			
			return user;
		}
		
	}
	
	
	public List<User> getAllUsers() {
		//return UserDatabase.getUsers();
		
		List<User> users = new ArrayList<User>();
		List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
				"select * from Users", new HashMap<String, Object>());
		
		for(Map row : rows) {
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
	
}
