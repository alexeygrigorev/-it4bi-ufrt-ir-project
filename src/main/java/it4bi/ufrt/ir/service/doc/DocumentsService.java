package it4bi.ufrt.ir.service.doc;

import it4bi.ufrt.ir.business.DocumentDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DocumentsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentsService.class);
	public DocumentsDAO docsDAO;
	
	@Value("${documents.index}")
	private String indexLocation;

	@Value("${documents.resultranking.personalization}")
	private double personalizationCoef;
	
	 @Autowired
     public DocumentsService(DocumentsDAO docsDAO) {
		 this.docsDAO = docsDAO;            
     }
	 
	 private ScoreDoc[] hits = null;
	 private List<DocumentSearchResultRow> resultSet = null;
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
			float score = hits[i].score;
														
			DocumentRecord docRecord = docsDAO.getDocByDocId(docID);
			DocumentSearchResultRow resultRow = new DocumentSearchResultRow(docRecord, score);
			DOCUSER_ASSOC assocType = docsDAO.getUserDocAssociation(docID, userID);
			
			if(assocType == DOCUSER_ASSOC.LIKES) resultRow.setLiked(true);
			if(assocType == DOCUSER_ASSOC.OWNS) resultRow.setOwned(true);
			
			
			resultSet.add(resultRow);
		    LOGGER.debug(docTitle + ", " + uploaderID + " - score: " + score);
		}
		
		
		
		
		// Sort documents by score DESCENDING
		Collections.sort(resultSet,  new Comparator<DocumentSearchResultRow>() {
			
	        public int compare(DocumentSearchResultRow d1, DocumentSearchResultRow d2) {
	        	Double score1, score2;
	        	
	        	score1 = d1.getScore()*(1-personalizationCoef) + calcUserDocAffinity(userId, d1)*personalizationCoef;
	        	score2 = d2.getScore()*(1-personalizationCoef) + calcUserDocAffinity(userId, d2)*personalizationCoef;
	        	
	        	// Quick fix of scores. 
	        	if (d1.getUploaderId() == userId){
	        		score1 = score1 + 0.25 * score1;  
	        	}
	        	if (d2.getUploaderId() == userId){
	        		score2 = score2 + 0.25 * score2;  
	        	}
	        	
	        	if (score1.equals(score2)) return 0;
	        	
	        	return (score2 < score1) ? -1 : 1; 	        	
	        }

			private double calcUserDocAffinity(Integer userId, DocumentSearchResultRow docRec) {
				// TODO Auto-generated method stub
				
				return docsDAO.calculateUserDocAffinity(docRec.getTags(), userId);
				
			}
	    });
		
		return resultSet;
	}
	
	
	 
	 
	public void rebuildDocsIndex() throws Exception {

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
	}		
}
