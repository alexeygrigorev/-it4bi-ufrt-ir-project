package it4bi.ufrt.ir.controller;

import it4bi.ufrt.ir.service.doc.DocumentSearchResultRow;
import it4bi.ufrt.ir.service.doc.DocumentsDao;
import it4bi.ufrt.ir.service.doc.DocumentsService;
import it4bi.ufrt.ir.service.doc.Tag;
import it4bi.ufrt.ir.service.dw.DatawarehouseService;
import it4bi.ufrt.ir.service.dw.DwhDtoResults;

import java.util.ArrayList;

import it4bi.ufrt.ir.service.spell.FIFASpellChecker;
import it4bi.ufrt.ir.service.spell.QueryAutoCorrectionResult;
import it4bi.ufrt.ir.service.users.User;
import it4bi.ufrt.ir.service.users.UsersService;
import it4bi.ufrt.ir.service.web.SocialSearchException;
import it4bi.ufrt.ir.service.web.SocialSearchRecord;
import it4bi.ufrt.ir.service.web.SocialSearchService;
import it4bi.ufrt.ir.service.web.SocialSearchType;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;
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
	private DocumentsDao documentsDAO;
	
	@Autowired
	private DocumentsService documents;
	
	@Autowired
	private SocialSearchService web;
	
	@Autowired
	private FIFASpellChecker spellChecker;
	
	@Autowired
	private DatawarehouseService datawarehouseService;
	
	@Autowired
	private UsersService usersService;
	
	@Value("${web.return.count}")
	private int webReturnCount;
	
	@Value("${web.semanticAnalysis}")
	private boolean semanticAnalysis;
	
	@Value("${documents.score.query}")
	private float queryScore;
	
	private long start_ms, end_ms;
	
	//http://localhost:8080/it4bi-ufrt-ir-project/rest/search/rec?u=408311
	@GET
	@Path("/rec")
	@Produces("application/json; charset=UTF-8")
	public List<RecommendedItem> recommendations(@QueryParam("u") int userID) {				
		LOGGER.debug("get recommendations. UserID {}", userID);
		
		return documents.getRecommendations(userID);
		
	}
	
	@GET
	@Path("/doc")
	@Produces("application/json; charset=UTF-8")
	public List<DocumentSearchResultRow> documents(@QueryParam("q") String query, @QueryParam("u") int userID) {				
		LOGGER.debug("document search. UserID {}; Query: {}", userID, query);
		
		// Query auto-correction
		QueryAutoCorrectionResult qr = spellChecker.autoCorrectQuery(query, true, 3);
		String correctedQuery = (qr.getIsCorrected() ? qr.getCorrectedQuery() : query);
		
		start_ms = System.currentTimeMillis();
		
		// Document search
		List<DocumentSearchResultRow> resultSet = documents.find(correctedQuery, userID);
		
		//update user profile based on the search terms
		String delims = " ,";
		String[] tokens = correctedQuery.split(delims);
		
		List<Tag> tagList = new ArrayList<Tag>();
		
		end_ms = System.currentTimeMillis();
		
		LOGGER.debug("Benchmark: Retrieving Result set took: " + (end_ms - start_ms)/1000.0f + "ms");
		start_ms = System.currentTimeMillis();
		
		for(int ctr = 0; ctr < tokens.length; ctr++) {
			//Tag tag = docsDAO.getTag(tokens[ctr]);
			Tag tag = documentsDAO.getTagByTagText(tokens[ctr]);
			if(tag != null) {
				tagList.add(tag);
			}
		}
		
		end_ms = System.currentTimeMillis();
		
		LOGGER.debug("Benchmark: Retrieving TagIDs from search-terms took: " + (end_ms - start_ms)/1000.0f + "ms");
		start_ms = System.currentTimeMillis();
		
		if(!tagList.isEmpty()) {
			//docsDAO.updateTagScores(userID, tagList, queryScore);
			documentsDAO.updateUserTagsScores(userID, tagList, queryScore);
		}
		
		end_ms = System.currentTimeMillis();
		
		LOGGER.debug("Benchmark: Updating User-Tag Scores took: " + (end_ms - start_ms)/1000.0f + "ms");
		
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

	@GET
	@Path("/dwh")
	@Produces("application/json; charset=UTF-8")
	public DwhDtoResults fifaDwh(@QueryParam("q") String query, @QueryParam("u") int userId) {
		LOGGER.debug("dwh query \"{}\" for user {}", query, userId);
		User user = usersService.userById(userId);
		return datawarehouseService.find(query, user);
	}

}
