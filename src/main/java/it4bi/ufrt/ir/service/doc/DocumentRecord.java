package it4bi.ufrt.ir.service.doc;

import java.io.IOException;
import java.util.Vector;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class DocumentRecord {

	private String docTitle;
	private String docPath;
	private int docId;
	private static int docIdCounter = 100000;

	public DocumentRecord() {
	}

	public String getDocTitle() {
		return docTitle;
	}
	
	public DocumentRecord(String docTitle) {
		this.docId = docIdCounter++;
		this.docTitle = docTitle;
	}
	
	public DocumentRecord(String docTitle, String docPath) {
		this.docId = docIdCounter++;
		this.docPath = docPath;
		this.docTitle = docTitle;
	}
	
	public String getFullText() {
		String docText = DocumentReader.readDoc(docPath);
		return docText;
	}
	
	public void index() throws Exception {  // Custom Exception Classes can be introduced
		
		if(docPath == null) throw new Exception();
		
		// configure index properties
        EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_41);  
      	Directory indexDir = new RAMDirectory();
      	
      	
        DocumentIndexer indexer = new DocumentIndexer(indexDir, analyzer);
        
        indexer.indexDocument(this);
		
	}
	
	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}

	public String getDocPath() {
		return docPath;
	}

	public void setDocPath() {
		
		DocumentsDAO docDAO = new DocumentsDAO();
		
		this.docPath = docDAO.getDocPath(this.docId);
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	

}
