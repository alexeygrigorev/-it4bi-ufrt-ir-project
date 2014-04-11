package it4bi.ufrt.ir.service.doc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


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
	
	 @Autowired
     public DocumentsService(DocumentsDAO docsDAO) {
             this.docsDAO = docsDAO;
             
     }
	
	public List<DocumentRecord> find(String query) {
		// configure index properties
        EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_41);  
        Directory indexDir = null;
		try {
			indexDir = new MMapDirectory(new File(indexLocation));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		List<DocumentRecord> docsList = new ArrayList<DocumentRecord>();
		DocumentSearchEngine searcher = null;
		try {
			searcher = new DocumentSearchEngine(indexDir, analyzer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LOGGER.debug("---Query: " + query);
		ScoreDoc[] hits = null;
		try {
			hits = searcher.performSearch(query);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOGGER.debug("---Results found: " + hits.length);
		for(int i=0;i<hits.length;++i) {
			int docId = hits[i].doc;
		    Document doc = null;
			try {
				doc = searcher.getDoc(docId);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    docsList.add(new DocumentRecord(doc.get("id"), doc.get("title"), Integer.parseInt(doc.get("uploaderId"))));
		    LOGGER.debug(doc.get("title") + ", " + doc.get("uploaderId") + " - score: " + hits[i].score);
		}
		System.out.println("---end of query results");
	
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
