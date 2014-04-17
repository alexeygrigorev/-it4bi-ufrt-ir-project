package it4bi.ufrt.ir.controller;

import it4bi.ufrt.ir.service.doc.DocumentRecord;
import it4bi.ufrt.ir.service.doc.DocumentsService;
import it4bi.ufrt.ir.service.web.SocialSearchException;
import it4bi.ufrt.ir.service.web.SocialSearchRecord;
import it4bi.ufrt.ir.service.web.SocialSearchService;
import it4bi.ufrt.ir.service.web.SocialSearchType;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/search")
public class SearchController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

	@Autowired
	private DocumentsService documents;
	@Autowired
	private SocialSearchService web;
	

	@GET
	@Path("/doc")
	@Produces("application/json; charset=UTF-8")
	public List<DocumentRecord> documents(@QueryParam("q") String query, @QueryParam("u") int userID) {
		LOGGER.debug("document search query: {}, UserID: {}", query, userID);
		return documents.find(query, userID);		
	}
	
	@GET
	@Path("/social")
	@Produces("application/json; charset=UTF-8")
	public List<SocialSearchRecord> web(@QueryParam("q") String query, @QueryParam("u") int userID) {
		LOGGER.debug("social search query: {}, UserID: {}", query, userID);
		
		try {
			return web.search(query, SocialSearchType.FACEBOOK);
		} catch (SocialSearchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}