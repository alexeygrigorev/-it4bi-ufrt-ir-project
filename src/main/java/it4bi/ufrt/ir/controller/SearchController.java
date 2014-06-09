package it4bi.ufrt.ir.controller;

import it4bi.ufrt.ir.service.doc.CSVFileWriter;
import it4bi.ufrt.ir.service.doc.DocumentRecord;
import it4bi.ufrt.ir.service.doc.DocumentRecordResultRow;
import it4bi.ufrt.ir.service.doc.DocumentsDao;
import it4bi.ufrt.ir.service.doc.DocumentsService;
import it4bi.ufrt.ir.service.doc.SimilarityMeasureEnum;
import it4bi.ufrt.ir.service.doc.Tag;
import it4bi.ufrt.ir.service.dw.DatawarehouseService;
import it4bi.ufrt.ir.service.dw.DwhDtoResults;

import java.io.FileNotFoundException;
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

import org.apache.mahout.cf.taste.common.TasteException;
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
	
	@Value("${spellchecker.suggestionsCount}")
	private int suggestionsCount;
	
	@Value("${web.semanticAnalysis}")
	private boolean semanticAnalysis;
	
	@Value("${documents.score.query}")
	private float queryScore;
	
	@Value("${documents.recommender.benchmark.outputFileLocation}")
	private String benchmarkResultsFile;
	
	private long start_ms, end_ms;
	
	//http://localhost:8080/it4bi-ufrt-ir-project/rest/search/benchmarkRecDoc?
	@GET
	@Path("/benchmarkRecDoc")
	@Produces("application/json; charset=UTF-8")
	public void benchmark_docRecommender() {				
		LOGGER.debug("document recommender benchmark is starting...");
		double threshold_steps[] = {0.6,0.7,0.8,0.9};
		SimilarityMeasureEnum sim_steps[] = {SimilarityMeasureEnum.EuclideanDistance,SimilarityMeasureEnum.LogLikelihoodSimilarity,SimilarityMeasureEnum.PearsonCorrelation,SimilarityMeasureEnum.SpearmanCorrelation,SimilarityMeasureEnum.TanimotoCoefficient};
		
		String [] headers = new String [] {"threshold", "similarity_measure", "score"};
		
		CSVFileWriter benchmarkFile = null;
		try {
			benchmarkFile = new CSVFileWriter("./output.csv", headers);
		} 
		catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
		
		for(int ctr = 0; ctr < threshold_steps.length; ctr++) {
			for(int ctr2 = 0; ctr2 < sim_steps.length; ctr2++) {
				Double cur_threshold = threshold_steps[ctr];
				SimilarityMeasureEnum cur_sim = sim_steps[ctr2];
				this.documents.reconfigureRecommender(cur_threshold, cur_sim);
				try {
					LOGGER.debug("Benchmarking DocRecommender: " + "Threshold=" + cur_threshold.toString() + " SimMeasure: " + cur_sim.toString());
					double score = documents.evaluateRecommender();
					benchmarkFile.appendRow(new String [] {cur_threshold.toString(), cur_sim.toString(), String.valueOf(score)});
				} catch (TasteException e) {
					e.printStackTrace();
				}
				catch (org.springframework.jdbc.CannotGetJdbcConnectionException e2) {
					ctr2--;
					continue;
				}
			}
		}
	}
	
	
	//http://localhost:8080/it4bi-ufrt-ir-project/rest/search/evalRecDoc?u=408311
	@GET
	@Path("/configureRecDoc")
	@Produces("application/json; charset=UTF-8")
	public void configure_docRecommender(@QueryParam("simType") int sim_type, @QueryParam("threshold") double threshold) {				
		LOGGER.debug("configuring recommender...");
		
		SimilarityMeasureEnum similarityMeasureSelection = null;
		
		if(sim_type == 1) similarityMeasureSelection = SimilarityMeasureEnum.EuclideanDistance;
		else if(sim_type == 2) similarityMeasureSelection = SimilarityMeasureEnum.LogLikelihoodSimilarity;
		else if(sim_type == 3) similarityMeasureSelection = SimilarityMeasureEnum.PearsonCorrelation;
		else if(sim_type == 4) similarityMeasureSelection = SimilarityMeasureEnum.SpearmanCorrelation;
		else if(sim_type == 5) similarityMeasureSelection = SimilarityMeasureEnum.TanimotoCoefficient;
		
		this.documents.reconfigureRecommender(threshold, similarityMeasureSelection);
	}
	
	//http://localhost:8080/it4bi-ufrt-ir-project/rest/search/evalRecDoc?u=408311
	@GET
	@Path("/evalRecDoc")
	@Produces("application/json; charset=UTF-8")
	public Double evaluate_docRecommender(@QueryParam("u") int userID) {				
		LOGGER.debug("evaluating recommender. UserID {}", userID);
		
		Double score = null;
		try {
			score = documents.evaluateRecommender();
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return score;
	}
	
	//http://localhost:8080/it4bi-ufrt-ir-project/rest/search/recDoc?u=408311
	@GET
	@Path("/recDoc")
	@Produces("application/json; charset=UTF-8")
	public List<DocumentRecordResultRow> recommendations(@QueryParam("u") int userID) {				
		LOGGER.debug("get recommendations. UserID {}", userID);
		
		List<DocumentRecordResultRow> recommendations = documentsDAO.getRecommendations(userID);
		if(recommendations.size() != 0) {
			documents.updateRecommendations(userID);
			return recommendations;
			// and in another thread calculate the new recommenations
		}
		else {
			recommendations = documents.getRecommendations(userID);
			for(DocumentRecordResultRow each : recommendations) {
				documentsDAO.insertDocRecommendationEntry(userID, each.getDocId(), each.getScore());
			}
		} 
		
		return recommendations;
	}
	
	@GET
	@Path("/doc")
	@Produces("application/json; charset=UTF-8")
	public List<DocumentRecordResultRow> documents(@QueryParam("q") String query, @QueryParam("u") int userID) {				
		LOGGER.debug("document search. UserID {}; Query: {}", userID, query);
		
		String correctedQuery = correctQuery(query);
		
		start_ms = System.currentTimeMillis();
		
		// Document search
		List<DocumentRecordResultRow> resultSet = documents.find(correctedQuery, userID);
		
		try
		{
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
		}
		catch(Exception ex) {
			
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
		return spellChecker.autoCorrectQuery(query, true, suggestionsCount);
	}

	@GET
	@Path("/dwh")
	@Produces("application/json; charset=UTF-8")
	public DwhDtoResults fifaDwh(@QueryParam("q") String query, @QueryParam("u") int userId) {
		LOGGER.debug("dwh query \"{}\" for user {}", query, userId);
		String correctedQuery = correctQuery(query);
		User user = usersService.userById(userId);
		DwhDtoResults res = datawarehouseService.find(correctedQuery, user); 
		return res;
	}

	private String correctQuery(String query) {
		QueryAutoCorrectionResult qr = spellChecker.autoCorrectQuery(query, true, suggestionsCount);
		return qr.getIsCorrected() ? qr.getCorrectedQuery() : query;
	}
}
