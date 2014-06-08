package it4bi.ufrt.ir.service.doc;

import it4bi.ufrt.ir.controller.UploadController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class DocumentRecord {
	
	private static final int tagsPerDoc = 4; // THIS SHOULD BE FROM custom.properties
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentRecord.class);
	
	private String docTitle;
	private String docPath;
	private int docId;
	private int uploaderId;
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
		this.docId = docId;
		this.docTitle = docTitle;
		this.uploaderId = uploaderId;
		this.mime = mime;
	}
	
	public DocumentRecord(String docTitle, int uploaderId, String mime) {
		this.docTitle = docTitle;
		this.uploaderId = uploaderId;
		this.mime = mime;
	}
	
	public DocumentRecord(String docTitle, String docPath, int uploaderId, String mime) {
		this.docPath = docPath;
		this.docTitle = docTitle;
		this.uploaderId = uploaderId;
		this.mime = mime;
	}
	
	public String getFullText() {
		String docText = DocumentReader.readDoc(docPath, mime);
		return docText;
	}
	
	public List<Tag> extractTags() throws Exception {
		
		// Temporary Index
		if(docPath == null) throw new Exception("Null DocPath Exception");
		
		// configure index properties
		
		final CharArraySet stopSet = new CharArraySet(Version.LUCENE_47, DocumentsService.extensive_domain_specific_stopWords, false);
		
        EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_47, stopSet); 
        Directory indexDir = new RAMDirectory();
      	
        DocumentIndexer indexer = new DocumentIndexer(indexDir, analyzer);
        
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
        IndexWriter writer = new IndexWriter(indexDir, config);
        
    	FieldType type = new FieldType();
   	 	type.setStoreTermVectors(true);
   	 	type.setStored(false);
   	 	type.setIndexed(true);
   	 
        Document doc = new Document();
        doc.add(new StoredField("id", 0));
        doc.add(new Field("content", this.getFullText(), type));
        writer.addDocument(doc);
        writer.commit();
        writer.close();
        
        
        IndexReader indexReader = DirectoryReader.open(indexDir);
        
        
        List<Pair<String, Integer>> termFreqs = new ArrayList<Pair<String, Integer>>();
		List<Tag> tags = new ArrayList<Tag>();

		try {
			getTF(indexReader, 0, termFreqs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Collections.sort(termFreqs, new Comparator<Pair<String, Integer>>() {
			public int compare(Pair<String, Integer> p1,
					Pair<String, Integer> p2) {
				if (p2.getRight() == p1.getRight())
					return 0;
				else
					return (p2.getRight() < p1.getRight()) ? -1 : 1;
			}
		});

		String extracted = "";
		int ctr = 0;
		for (Pair<String, Integer> tfs : termFreqs) {
			String temp = tfs.getLeft();
			if (UploadController.isNumerical(temp) == false) {
				ctr++;
				tags.add(new Tag(temp));
				extracted += temp;
			}
			if (ctr == tagsPerDoc)
				break;
			extracted += ", ";
		}

		LOGGER.debug("extracted tags: " + extracted);

		return tags;
	}
	
	public void getTF(IndexReader reader, int docID,
			List<Pair<String, Integer>> termFreqs) throws IOException {
		Fields f = reader.getTermVectors(docID);

		Iterator<String> it = f.iterator();

		while (it.hasNext()) {
			Terms t = f.terms("content");
			TermsEnum it2 = t.iterator(null);

			while (it2.next() != null) {

				// System.out.println(it2.term().utf8ToString() + " " +
				// it2.totalTermFreq());
				termFreqs.add(new ImmutablePair<String, Integer>(it2.term()
						.utf8ToString(), (int) it2.totalTermFreq()));
			}

			break;
		}

	}
	
	public void index(String indexLocation) throws Exception {  // Custom Exception Classes can be introduced
		
		if(docPath == null) throw new Exception("Null DocPath Exception");
		
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
	
	public void setMime(String mime) {
		this.mime = mime;
	}

	
	public void setTags(List<Tag> tags) {
		this.tags = tags;	
	}

	public List<Tag> getTags() {
		// TODO Auto-generated method stub
		return tags;
	}
}
