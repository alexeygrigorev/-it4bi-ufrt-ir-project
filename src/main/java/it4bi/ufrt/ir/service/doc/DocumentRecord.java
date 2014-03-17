package it4bi.ufrt.ir.service.doc;

public class DocumentRecord {

	private String docName;
	private String docPath;

	public DocumentRecord() {
	}

	public DocumentRecord(String docName, String docPath) {
		this.docName = docName;
		this.docPath = docPath;
	}

	public String getDocName() {
		return docName;
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
