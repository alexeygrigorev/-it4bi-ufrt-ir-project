package it4bi.ufrt.ir.controller;

import it4bi.ufrt.ir.service.doc.DocumentRecord;
import it4bi.ufrt.ir.service.doc.DocumentSearchResultRow;
import it4bi.ufrt.ir.service.doc.DocumentsDAO;
import it4bi.ufrt.ir.service.doc.DocumentsService;
import it4bi.ufrt.ir.service.doc.Tag;

import java.util.ArrayList;

import it4bi.ufrt.ir.service.spell.FIFASpellChecker;
import it4bi.ufrt.ir.service.spell.QueryAutoCorrectionResult;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Path("/search")
public class SearchController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);
	
	@Autowired
	private DocumentsDAO docsDAO;
	
	@Autowired
	private DocumentsService documents;
	
	@Autowired
	private SocialSearchService web;
	
	@Autowired
	private FIFASpellChecker spellChecker;
	
	@Value("${web.return.count}")
	private int webReturnCount;
	
	@Value("${web.semanticAnalysis}")
	private boolean semanticAnalysis;
	
	@Value("${documents.score.query}")
	private float queryScore;
	
	@GET
	@Path("/doc")
	@Produces("application/json; charset=UTF-8")
	public List<DocumentSearchResultRow> documents(@QueryParam("q") String query, @QueryParam("u") int userID) {				
		LOGGER.debug("document search. UserID {}; Query: {}", userID, query);
		List<DocumentSearchResultRow> resultSet = documents.find(query, userID);
		
		//update user profile based on the search terms
		String delims = " ,";
		String[] tokens = query.split(delims);
		
		List<Tag> tagList = new ArrayList<Tag>();
		
		for(int ctr = 0; ctr < tokens.length; ctr++) {
			Tag tag = docsDAO.getTag(tokens[ctr]); 
			if(tag != null) {
				tagList.add(tag);
			}
		}
		
		if(!tagList.isEmpty()) {
			docsDAO.updateTagScores(userID, tagList, queryScore);
		}
		
		return resultSet;
	}	
	
	@GET
	@Path("/social")
	@Produces("application/json; charset=UTF-8")
	public List<SocialSearchRecord> web(@QueryParam("q") String query, @QueryParam("u") int userID, 
			                            @QueryParam("source") int sourceID) {
		LOGGER.debug("social search query: {}, UserID: {}, SourceID: {}", query, userID, sourceID);				
				
		try {
			SocialSearchType searchType = SocialSearchType.values()[sourceID];
			return web.search(query, searchType, semanticAnalysis, webReturnCount);
		} catch (SocialSearchException e) {
			e.printStackTrace();
			return new ArrayList<SocialSearchRecord>();
		}
	}
	
	@GET
	@Path("/autocorrection")
	@Produces("application/json; charset=UTF-8")
	public QueryAutoCorrectionResult autocorrection(@QueryParam("q") String query) {
		LOGGER.debug("get autocorrection for: {}", query);				
				
		QueryAutoCorrectionResult qr = spellChecker.autoCorrectQuery(query, true, 3);
		return qr;
	}
}