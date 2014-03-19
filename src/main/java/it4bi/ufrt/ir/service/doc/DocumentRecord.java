package it4bi.ufrt.ir.service.doc;

public class DocumentRecord {

	private String docName;
	private String docPath;
	private String docId;

	public DocumentRecord() {
	}

	public DocumentRecord(String docName, String docPath) {
		this.docName = docName;
		this.docPath = docPath;
	}

	public String getDocName() {
		return docName;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getDocPath() {
		return docPath;
	}

	public void setDocPath(String docPath) {
		this.docPath = docPath;
	}

}
