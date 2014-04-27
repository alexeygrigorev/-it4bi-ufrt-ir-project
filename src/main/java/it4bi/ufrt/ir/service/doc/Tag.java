package it4bi.ufrt.ir.service.doc;

public class Tag {

	public int tagId;
	static public int tagIdCounter = 50000;
	public String tag;
	
	public Tag() {
	}
	
	public Tag(String tag) {
		this.tag = tag;
		tagId = tagIdCounter++;
	}	
	
	public int getTagId() {
		return tagId;
	}
	
	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	public String getTag() {
		return tag;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
}
