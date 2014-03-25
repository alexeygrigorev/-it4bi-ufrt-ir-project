package it4bi.ufrt.ir.service.doc;

import it4bi.ufrt.ir.controller.InfoController;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class DocumentIndexer {

	private Analyzer analyzer;
	private Directory indexDir;
    private IndexWriter indexWriter = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(InfoController.class);
    
    /** Creates a new instance of Indexer */
    public DocumentIndexer(Directory indexDir, Analyzer analyzer) {
        this.analyzer = analyzer;
    	this.indexDir = indexDir;
    }
 
    
    /** Gets index writer */
    public IndexWriter getIndexWriter() throws IOException {
        if (indexWriter == null) {
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_41, analyzer);
            indexWriter = new IndexWriter(indexDir, config);
        }
        return indexWriter;
   }    
   
    
    /** Closes index writer */
    public void closeIndexWriter() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
   }
    
    
    /** Deletes all documents in the index */
    public void deleteIndex() throws IOException {
        if (indexWriter != null) {
            indexWriter.deleteAll();
        }
   }

    
    /** Adds a document (a hotel) to the index */ 
    public void indexDocument(DocumentRecord documentRecord) throws IOException {
    	 LOGGER.debug("Indexing document: " + documentRecord.getDocTitle());
         IndexWriter writer = getIndexWriter();
         Document doc = new Document();
         doc.add(new StoredField("id", documentRecord.getDocId()));
         doc.add(new StoredField("uploaderId", documentRecord.getUploaderId()));
         doc.add(new TextField("title", documentRecord.getDocTitle(), Field.Store.YES));
         doc.add(new TextField("content", documentRecord.getFullText() , Field.Store.NO));
         writer.addDocument(doc);
         writer.commit();
         writer.close();
     }
}
