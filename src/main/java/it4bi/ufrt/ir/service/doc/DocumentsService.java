package it4bi.ufrt.ir.service.doc;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.ws.rs.Path;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DocumentsService {

	
	public DocumentsDAO docDAO;
	
	 @Autowired
     public DocumentsService(DocumentsDAO docDAO) {
             this.docDAO = docDAO;
             
     }
	
	public List<DocumentRecord> find(String query) {
		
		// here we implement everything
		return Arrays.asList(new DocumentRecord("England's harsh decline"), new DocumentRecord("Coaches matter much"));
		
	}
	
	public static void rebuildDocsIndex() throws Exception {

		// configure index properties
        EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_41);  
      	Directory indexDir = new RAMDirectory();
      	
        DocumentIndexer indexer = new DocumentIndexer(indexDir, analyzer);
        
        indexer.deleteIndex();
        
        List<DocumentRecord> allDocumentRecords = null;
        //List<DocumentRecord> allDocumentRecords = docDAO.getAllDocuments();
        
        // TODO: Alexey, why can't I use autowired DAO here?
        
        
        
        for(DocumentRecord documentRecord : allDocumentRecords) {
			documentRecord.setDocPath();
			documentRecord.index();
		}
        
        
	}
	
	
}
