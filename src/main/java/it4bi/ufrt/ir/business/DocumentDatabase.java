package it4bi.ufrt.ir.business;

import java.util.ArrayList;
import java.util.List;

import it4bi.ufrt.ir.service.doc.DocumentRecord;


public class DocumentDatabase {
    
    private static List<DocumentRecord> documents = new ArrayList<DocumentRecord>();

	public static List<DocumentRecord> getDocuments() {
		return documents;
	}
	
	public static void insertDoc(DocumentRecord doc) {
		documents.add(doc);
	}
}

