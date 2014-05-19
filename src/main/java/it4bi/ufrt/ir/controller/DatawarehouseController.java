package it4bi.ufrt.ir.controller;

import it4bi.ufrt.ir.service.dw.DatawarehouseService;
import it4bi.ufrt.ir.service.dw.ExecutedDwhQuery;
import it4bi.ufrt.ir.service.dw.MatchedQueryTemplate;

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

	@POST
	@Path("/execute")
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	public ExecutedDwhQuery execute(MatchedQueryTemplate matchedQueryTemplate) {
		/* communication pattern (TODO: Remove after implementing on the client)
var url = serverURL + "rest/search/dwh?q=Russia&u=408305";
a = $.get(url);
...

var q = a.responseJSON.matched[0];
var url2 = serverURL + "rest/dwh/execute";
$.ajax({
    type: "POST",
    contentType: "application/json; charset=utf-8",
    url: url2,
    data: JSON.stringify(q),
    dataType: "json"
});
		 */
		
		// TODO: user!

		LOGGER.debug("executing {}", matchedQueryTemplate);
		ExecutedDwhQuery dwResult = datawarehouseService.execute(matchedQueryTemplate); 
		return dwResult;
	}

}
