package it4bi.ufrt.ir.service.doc;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Value;

public class DocumentRecord {

	private String docTitle;
	private String docPath;
	private String docExtension;
	private int docId;
	private int uploaderId;
	private static int docIdCounter = 100000;
	
	public int getUploaderId() {
		return uploaderId;
	}
	
	public void setUploaderId(int uploaderId) {
		this.uploaderId = uploaderId;
	}

	public DocumentRecord() {
	}
	
	public DocumentRecord(int docId, String docTitle, int uploaderId) {
		this.docId = docIdCounter++;
		this.docTitle = docTitle;
		this.uploaderId = uploaderId;
	}
	
	public DocumentRecord(String docTitle, int uploaderId) {
		this.docId = docIdCounter++;
		this.docTitle = docTitle;
		this.uploaderId = uploaderId;
	}
	
	public DocumentRecord(String docTitle, String docPath, int uploaderId) {
		this.docId = docIdCounter++;
		this.docPath = docPath;
		this.docTitle = docTitle;
		this.uploaderId = uploaderId;
	}
	
	public String getFullText() {
		String docText = DocumentReader.readDoc(docPath);
		return docText;
	}
	
	public void index(String indexLocation) throws Exception {  // Custom Exception Classes can be introduced
		
		if(docPath == null) throw new Exception();
		
		// configure index properties
        EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_41); 
        Directory indexDir = new MMapDirectory(new File(indexLocation));
      	
      	
        DocumentIndexer indexer = new DocumentIndexer(indexDir, analyzer);
        
        indexer.indexDocument(this);
		
	}
	
	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}

	public String getDocTitle() {
		return docTitle;
	}
	
	public String getDocPath() {
		return docPath;
	}

	public void setDocPath(String docPath) {			
		this.docPath = docPath;
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
	
	public String getDocExtension() {
		return this.docExtension;
	}

	public void setDocExtension(String docExtension) {
		this.docExtension = docExtension;
	}
}
