package it4bi.ufrt.ir.controller;

import it4bi.ufrt.ir.service.dw.DatawarehouseService;
import it4bi.ufrt.ir.service.dw.ExecutedDwhQuery;
import it4bi.ufrt.ir.service.dw.MatchedQueryTemplate;
import it4bi.ufrt.ir.service.users.User;
import it4bi.ufrt.ir.service.users.UsersService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/dwh")
public class DatawarehouseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatawarehouseController.class);

	@Autowired
	private DatawarehouseService datawarehouseService;
	
	@Autowired
	private UsersService userService;

	@POST
	@Path("/execute")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public ExecutedDwhQuery execute(MatchedQueryTemplate matchedQueryTemplate) {
		LOGGER.debug("executing {}", matchedQueryTemplate);
		ExecutedDwhQuery dwResult = datawarehouseService.execute(matchedQueryTemplate);
		return dwResult;
	}

	@POST
	@Path("/executeUser")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public ExecutedDwhQuery executePersonalized(TemplateAndUser param) {
		LOGGER.debug("executing {} for user {}", param.getMatchedQueryTemplate(), param.getUserId());
		User user = userService.userById(param.getUserId());
		ExecutedDwhQuery dwResult = datawarehouseService.execute(param.getMatchedQueryTemplate(), user);
		return dwResult;
	}

	public static class TemplateAndUser {

		private MatchedQueryTemplate matchedQueryTemplate;
		private int userId;

		public MatchedQueryTemplate getMatchedQueryTemplate() {
			return matchedQueryTemplate;
		}

		public void setMatchedQueryTemplate(MatchedQueryTemplate matchedQueryTemplate) {
			this.matchedQueryTemplate = matchedQueryTemplate;
		}

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

	}

}
