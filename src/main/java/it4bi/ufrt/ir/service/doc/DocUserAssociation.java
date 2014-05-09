package it4bi.ufrt.ir.service.doc;

public class DocUserAssociation {

	DOCUSER_ASSOC_TYPE assocType;
	float affinity;
	
	public DocUserAssociation(DOCUSER_ASSOC_TYPE assocType, float affinity) {
		this.assocType = assocType;
		this.affinity = affinity;
	}

	public DOCUSER_ASSOC_TYPE getAssocType() {
		return assocType;
	}

	public void setAssocType(DOCUSER_ASSOC_TYPE assocType) {
		this.assocType = assocType;
	}

	public float getAffinity() {
		return affinity;
	}

	public void setAffinity(float affinity) {
		this.affinity = affinity;
	}
	
}
