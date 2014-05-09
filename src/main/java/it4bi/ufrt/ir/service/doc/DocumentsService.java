package it4bi.ufrt.ir.service.doc;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.Version;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
/*
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import edu.stanford.nlp.util.ArrayMap;

@Component
public class DocumentsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentsService.class);
	
	public DocumentsDAO2 documentsDAO;
	
	//Recommendation Stuff
	
	
	private JDBCDataModel datamodel;
	private UserBasedRecommender recommender;
	private UserSimilarity similarity;
	private UserNeighborhood neighborhood;
	
	
	
	
	@Value("${documents.index}")
	private String indexLocation;

	@Value("${documents.resultranking.personalization}")
	private float personalizationCoef;
	
	 @Autowired
     public DocumentsService(DocumentsDAO2 documentsDAO) {
		 this.documentsDAO = documentsDAO;
		 
		 datamodel = new RecommenderDataModel(documentsDAO);
		 try {
			similarity = new PearsonCorrelationSimilarity(datamodel);
		} catch (TasteException e) {
			e.printStackTrace();
		}
		 neighborhood = new ThresholdUserNeighborhood(0.1, similarity, datamodel);
		 recommender = new GenericUserBasedRecommender(datamodel, neighborhood, similarity);
     }
	 
	 
	 
	 private ScoreDoc[] hits = null;
	 private List<DocumentSearchResultRow> resultSet = null;
	 private List<DocUserAssociation> associationSummaryList = null;
	 private Integer userId = 0;
	 
	 
	
	 public List<DocumentSearchResultRow> find(String query, int userID) {
		 this.userId = userID;
		 resultSet = new ArrayList<DocumentSearchResultRow>();		 
		 
        // check for existence 
        File directoryLocation = new File(indexLocation);
        if(!directoryLocation.exists()) {
        	LOGGER.debug("Directory does not exist: {}. CREATE IT MANUALLY", indexLocation);
        	return resultSet;
        }
                 
		// configure index properties
        EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_47);  
        Directory indexDir = null;
        
		try {
			indexDir = new MMapDirectory(directoryLocation);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	
		DocumentSearchEngine searcher = null;
		try {
			searcher = new DocumentSearchEngine(indexDir, analyzer);
		} catch (IOException e) {
			LOGGER.debug("Nothing to search in the directory. UPLOAD SOME DOCUMENT", indexLocation);
        	return resultSet;
		}
		
		LOGGER.debug("---Query: " + query);
		
		try {
			hits = searcher.performSearch(query);
		} 
		catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
		LOGGER.debug("---Results found: " + hits.length);
		
		long start_ms, end_ms;
		start_ms = System.currentTimeMillis();
		
		associationSummaryList = this.documentsDAO.getUserDocAssociationSummary(userID, hits);
		
		end_ms = System.currentTimeMillis();
		System.out.println((end_ms-start_ms)/1000f + "sec");
		
		
		for(int i=0; i < hits.length; ++i) {
			int docId = hits[i].doc;
		    Document doc = null;
			
		    try {
				doc = searcher.getDoc(docId);
			} 
		    catch (IOException e) {
				e.printStackTrace();
			}
						
			int docID = Integer.parseInt(doc.get("id"));
			String docTitle = doc.get("title");
			int uploaderID = Integer.parseInt(doc.get("uploaderId"));
			String docMime = doc.get("mime");
			
			DocumentRecord docRecord = new DocumentRecord(docID, docTitle, uploaderID, docMime); //documentsDAO.getDocByID(docID);
			  
			float raw_score = hits[i].score;
			
			
			
			//float personalization_score = documentsDAO.getUserDocAffinity(userId, docID);
			
			float personalization_score = associationSummaryList.get(i).affinity;
			float score = (1-personalizationCoef)*raw_score + personalizationCoef*personalization_score;
			
			
			DocumentSearchResultRow resultRow = new DocumentSearchResultRow(docRecord, score);
			//DOCUSER_ASSOC_TYPE assocType = documentsDAO.getUserDocAssociation(docID, userID);
			DOCUSER_ASSOC_TYPE assocType = associationSummaryList.get(i).assocType;
			
			
			
			if(assocType != null) {
				if(assocType.equals(DOCUSER_ASSOC_TYPE.LIKES)) resultRow.setLiked(true);
				if(assocType.equals(DOCUSER_ASSOC_TYPE.OWNS)) resultRow.setOwned(true);
			}
			
			resultSet.add(resultRow);
		    LOGGER.debug(docTitle + ", " + uploaderID + " - personalized score: " + score);
		}
		
		//List<Float> userDocScores = new ArrayList<Float>();
		/*Map<Integer,Float> userDocScores = new HashMap<Integer,Float>();
		
		for(DocumentSearchResultRow row : resultSet) {
			Integer docID = row.getDocId();
			Float score = documentsDAO.getUserDocAffinity(userId, docID);
			userDocScores.put(docID, score);
		}*/
		
		// Sort documents by score DESCENDING
		Collections.sort(resultSet,  new Comparator<DocumentSearchResultRow>() {
			
			public int compare(DocumentSearchResultRow d1, DocumentSearchResultRow d2) {
	        	if (d1.getScore() == d2.getScore()) return 0;
	        	return (d2.getScore() < d1.getScore()) ? -1 : 1; 	        	
	        }
	    });
		
		return resultSet;
	}
	
	
	 public List<DocumentSearchResultRow> getRecommendations(int userID) {
		 
		 List<RecommendedItem> recommended = new ArrayList<RecommendedItem>(); 
		 
		 try {
			recommended = recommender.recommend(userID, 1);
		} catch (TasteException e) {
			e.printStackTrace();
		}
		 
		 
		return null;
		 
	 }
	 
	 
	/*public void rebuildDocsIndex() throws Exception {

		// configure index properties
		EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_47);  
        Directory indexDir = new MMapDirectory(new File(indexLocation));
        
        DocumentIndexer indexer = new DocumentIndexer(indexDir, analyzer);
        
        indexer.deleteIndex();        
        
        List<DocumentRecord> allDocumentRecords = docsDAO.getAllDocuments();        
        
        for(DocumentRecord documentRecord : allDocumentRecords) {
			//documentRecord.setDocPath();
			documentRecord.index(indexLocation);
		}
	}*/		
}
