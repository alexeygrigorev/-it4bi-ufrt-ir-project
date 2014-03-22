package it4bi.ufrt.ir.service.doc;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.springframework.stereotype.Component;

@Component
public class DocumentsService {

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
        
        Vector<DocumentRecord> allDocumentRecords = DocumentsDAO.getAllDocuments();
        
        for(DocumentRecord documentRecord : allDocumentRecords) {
			documentRecord.setDocPath();
			documentRecord.indexDocument();
		}
        
        
	}
	
	
}
