package it4bi.ufrt.ir.service.doc;

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
	
	@Value("${documents.userOwnerBonus}")
	private float userOwnerBonus;
	
	@Value("${documents.docLikedBonus}")
	private float docLikedBonus;
	
	 @Autowired
     public DocumentsService(DocumentsDAO docsDAO) {
		 this.docsDAO = docsDAO;            
     }
	
	public List<DocumentRecord> find(String query, int userID) {
        // check for existence 
        File directoryLocation = new File(indexLocation);
        if(!directoryLocation.exists()) {
        	LOGGER.debug("Directory does not exist: {}", indexLocation);
        	return null;
        }
 
		// configure index properties
        EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_41);  
        Directory indexDir = null;
        
		try {
			indexDir = new MMapDirectory(directoryLocation);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	
		List<DocumentRecord> docsList = new ArrayList<DocumentRecord>();
		DocumentSearchEngine searcher = null;
		try {
			searcher = new DocumentSearchEngine(indexDir, analyzer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		LOGGER.debug("---Query: " + query);
		ScoreDoc[] hits = null;
		try {
			hits = searcher.performSearch(query);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		LOGGER.debug("---Results found: " + hits.length);
		for(int i=0;i<hits.length;++i) {
			int docId = hits[i].doc;
		    Document doc = null;
			try {
				doc = searcher.getDoc(docId);
			} catch (IOException e) {
				e.printStackTrace();
			}
						
			String docID = doc.get("id");
			String docTitle = doc.get("title");
			int uploaderID = Integer.parseInt(doc.get("uploaderId"));
			float score = hits[i].score;
			
			// TODO: ANIL: 
			
			// if uploaderID is equal to the ID of the user that makes search then increase score by 50%.			
			if (userID == uploaderID) {
				score = score + userOwnerBonus * score;
			}
			
			// TODO: ANIL: if the document is liked by this user then increase score by 25%
			// if (false == false) {
				// score = score + docLikedBonus * score;
			// }
														
			DocumentRecord docRecord = new DocumentRecord(docID, docTitle, uploaderID, score);
			// TODO: ANIL: isLiked.
			docRecord.setIsLiked(false);
			// TODO: ANIL: DocExtension and check docTitle.
			docRecord.setDocExtension("TODO extension");
						
		    docsList.add(docRecord);
		    LOGGER.debug(docTitle + ", " + uploaderID + " - score: " + score);
		}
		System.out.println("---end of query results");
	
		// Sort documents by score DESCENDING
		Collections.sort(docsList,  new Comparator<DocumentRecord>() {

	        public int compare(DocumentRecord d1, DocumentRecord d2) {
	        	if (d2.getScore() == d1.getScore()) {
	        		return 0;
	        	}
	        	
	        	return (d2.getScore() < d1.getScore()) ? -1 : 1; 	        	
	        }
	    });
		
		return docsList;		
	}
	
	public void rebuildDocsIndex() throws Exception {

		// configure index properties
        EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_41);  
        Directory indexDir = new MMapDirectory(new File(indexLocation));
        
        DocumentIndexer indexer = new DocumentIndexer(indexDir, analyzer);
        
        indexer.deleteIndex();        
        
        List<DocumentRecord> allDocumentRecords = docsDAO.getAllDocuments();        
        
        for(DocumentRecord documentRecord : allDocumentRecords) {
			documentRecord.setDocPath();
			documentRecord.index(indexLocation);
		}
	}		
}
