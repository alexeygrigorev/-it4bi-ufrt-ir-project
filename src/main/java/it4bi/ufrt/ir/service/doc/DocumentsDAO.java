package it4bi.ufrt.ir.service.doc;

import java.util.Vector;

public class DocumentsDAO {

	public static Vector<DocumentRecord> getDocumentList(int userId) {
		Vector<DocumentRecord> documentsList = new Vector<DocumentRecord>(); 
		
		//Implement logic for fetching list of uploaded document records (only title and id) from DB for a given user
		
		return documentsList; // no filepath set
	}
	
	public static Vector<DocumentRecord> getFavourites(int userId) {
		Vector<DocumentRecord> favouritesList = new Vector<DocumentRecord>();
		
		//Implement logic for fetching list of starred document records (only title and id) from DB for a given user
		
		return favouritesList; // no filepath set
		
	}
	
	public static String getDocPath(int docId) {
		String filePath = null;
		
		//Implement logic for retrieving docPath for a given documentId
		
		return filePath;
	}
	
	public static void insertDocumentRecord(DocumentRecord documentRecord) {
		
		// Implement logic for documen insertion to database. Careful: new document entry in DB should be associated with the user who uploaded it.
		
	}
	
	public static void uploadDoc(int userId, DocumentRecord documentRecord) {
		
	}
	
	public static Vector<DocumentRecord> getAllDocuments() {
		
		//Implement Business Logic For retrieving all document records (potentially for reindexing)
		
		Vector<DocumentRecord> allDocumentRecords = new Vector<DocumentRecord>();
		
		return allDocumentRecords;
		
	}
	
	
}
