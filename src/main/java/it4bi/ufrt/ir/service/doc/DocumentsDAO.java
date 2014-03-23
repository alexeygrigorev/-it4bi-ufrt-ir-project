package it4bi.ufrt.ir.service.doc;

import java.util.List;
import java.util.Vector;

public class DocumentsDAO {

	public List<DocumentRecord> getDocumentList(int userId) {
		List<DocumentRecord> documentsList = new Vector<DocumentRecord>();

		// Implement logic for fetching list of uploaded document records (only
		// title and id) from DB for a given user

		return documentsList; // no filepath set
	}

	public List<DocumentRecord> getFavourites(int userId) {
		List<DocumentRecord> favouritesList = new Vector<DocumentRecord>();

		// Implement logic for fetching list of starred document records (only
		// title and id) from DB for a given user

		return favouritesList; // no filepath set

	}

	public String getDocPath(int docId) {
		String filePath = null;

		// Implement logic for retrieving docPath for a given documentId

		return filePath;
	}

	public void insertDocumentRecord(int userID, DocumentRecord documentRecord) {

		// Implement logic for document insertion to database. Careful: new
		// document entry in DB should be associated with the user who uploaded
		// it.

	}


	public List<DocumentRecord> getAllDocuments() {

		// Implement Business Logic For retrieving all document records
		// (potentially for reindexing)

		List<DocumentRecord> allDocumentRecords = new Vector<DocumentRecord>();

		return allDocumentRecords;

	}

}
