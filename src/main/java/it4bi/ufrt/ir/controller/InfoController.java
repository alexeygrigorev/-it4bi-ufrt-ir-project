package it4bi.ufrt.ir.controller;

import it4bi.ufrt.ir.service.users.User;
import it4bi.ufrt.ir.service.users.UsersService;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/info")
public class InfoController {

	private static final Logger LOGGER = LoggerFactory.getLogger(InfoController.class);

	@Autowired
	private UsersService users;
		
	@GET
	@Path("/users")
	@Produces("application/json; charset=UTF-8")
	public List<User> users() {
		LOGGER.debug("return all registered users form the database");
		return users.getUsers();
	}
}