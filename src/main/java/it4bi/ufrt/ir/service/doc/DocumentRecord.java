package it4bi.ufrt.ir.service.doc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
	private int docId;
	private int uploaderId;
	private static int docIdCounter = 0;
	private List<Tag> tags;
	private String mime;
	
	public int getUploaderId() {
		return uploaderId;
	}
	
	public void setUploaderId(int uploaderId) {
		this.uploaderId = uploaderId;
	}

	public DocumentRecord() {
		tags = new ArrayList<Tag>();
	}
	
	public DocumentRecord(int docId, String docTitle, int uploaderId, String mime) {
		this.docId = docIdCounter++;
		this.docTitle = docTitle;
		this.uploaderId = uploaderId;
		this.mime = mime;
	}
	
	public DocumentRecord(String docTitle, int uploaderId, String mime) {
		this.docId = docIdCounter++;
		this.docTitle = docTitle;
		this.uploaderId = uploaderId;
		this.mime = mime;
	}
	
	public DocumentRecord(String docTitle, String docPath, int uploaderId, String mime) {
		this.docId = docIdCounter++;
		this.docPath = docPath;
		this.docTitle = docTitle;
		this.uploaderId = uploaderId;
		this.mime = mime;
	}
	
	public String getFullText() {
		String docText = DocumentReader.readDoc(docPath, mime);
		return docText;
	}
	
	public void index(String indexLocation) throws Exception {  // Custom Exception Classes can be introduced
		
		if(docPath == null) throw new Exception();
		
		// configure index properties
        EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_47); 
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

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}
	
	public String getMime() {
		return this.mime;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
		
	}

	public List<Tag> getTags() {
		// TODO Auto-generated method stub
		return tags;
	}
}
